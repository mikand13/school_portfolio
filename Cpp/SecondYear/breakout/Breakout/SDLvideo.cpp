#include "SDLvideo.h"

// C libraries
#include <SDL_image.h>

// C++ libraries
#include <string>
#include <thread>

// local headers
#include "Debug.h"
#include "GameManager.h"
#include "GameObject.h"
#include "Player.h"
#include "Ball.h"
#include "Brick.h"
#include "SDLInitializationException.h"

SDLvideo::SDLvideo() :
	window_(nullptr),
	screenSurface_(nullptr),
	window_renderer_(nullptr),
	intro_(nullptr),
	game_over_(nullptr),
	game_font_(nullptr),
	update_text_surface_(nullptr),
	update_text_texture_(nullptr) {}

SDLvideo::~SDLvideo() {
	DEBUG_DESTRUCTOR("SDLvideo has been destroyed...");
}

void SDLvideo::Init() {
	try {
		// init video
		if (SDL_Init(SDL_INIT_VIDEO) < 0 ||
				TTF_Init() != 0 ||
				!(IMG_Init(IMG_INIT_PNG) & IMG_INIT_PNG)) {
			DEBUG("SDL_Error" << SDL_GetError() << " SDL_ttf error: " << TTF_GetError());

			throw SDLInitializationException();
		}

		// init old score to default
		old_score_ = INT16_MAX;

		// set refreshrate / fps
		SDL_DisplayMode mode = { SDL_PIXELFORMAT_UNKNOWN, 0, 0, 0, 0 };
		SDL_GetDisplayMode(0, 0, &mode);

		// set to 60 if ungettable
		if (mode.refresh_rate == 0) {
			refresh_rate_ = 60;
		} else {
			refresh_rate_ = mode.refresh_rate;
		}

		// init color for text
		sdl_color_.r = 255;
		sdl_color_.g = 255;
		sdl_color_.b = 0;
		sdl_color_.a = 255;

		// init font
		game_font_ = TTF_OpenFont("..//Resources//fonts//breakout.ttf", 32);
		update_text_coords_.y = GUI_AREA / 4;

		// init window
		window_ = SDL_CreateWindow(
			"Breakout",
			SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED,
			SCREEN_WIDTH, SCREEN_HEIGHT,
			SDL_WINDOW_SHOWN);

		if (window_ == nullptr) {
			DEBUG("Window could not be created! SDL_Error: " << SDL_GetError());

			throw SDLInitializationException();
		}

		// init renderer
		window_renderer_ = SDL_CreateRenderer(window_, -1, SDL_RENDERER_ACCELERATED | SDL_RENDERER_PRESENTVSYNC);

		if (window_renderer_ == nullptr) {
			DEBUG("Renderer could not be created! SDL_Error: " << SDL_GetError());

			throw SDLInitializationException();
		}

		// config renderer
		SDL_SetRenderDrawColor(window_renderer_, 0, 0, 0, 255);
		SDL_RenderClear(window_renderer_);

		// init screen
		screenSurface_ = SDL_GetWindowSurface(window_);

		if (screenSurface_ == nullptr) {
			DEBUG("Surface not successfully gathered from window: SDL_Error: "
				<< SDL_GetError());
		}
	} catch (SDLInitializationException) {
		throw;
	}
}

void SDLvideo::Update(
	EGameState game_state, 
	const std::vector<std::shared_ptr<GameObject>>& game_objects,
	int level, 
	int current_lives, int current_score) {
	SDL_RenderClear(window_renderer_);

	if (game_state == INTRO) {
		RenderMenu(intro_);
	} else if (game_state == RUNNING || game_state == INIT) {
		SDL_RenderClear(window_renderer_);

		RenderGUI(level, current_lives, current_score);
		RenderGameObjects(game_objects);
	} else if (game_state == EXIT) {
		RenderMenu(game_over_);
	}

	SDL_RenderPresent(window_renderer_);
}

void SDLvideo::CreateIntroTexture() {
	auto intro_picture = SDL_LoadBMP("..//Resources//images//intro_placeholder.bmp");

	if (intro_picture == nullptr) {
		DEBUG("Unable to load image! SDL Error: " << SDL_GetError());
	} else {
		intro_ = SDL_CreateTextureFromSurface(window_renderer_, intro_picture);
		SDL_FreeSurface(intro_picture);
		intro_picture = nullptr;

		CreateMenuCoords();
	}
}

void SDLvideo::CreateGameOverTexture(bool new_high_score, std::vector<std::string> results) {
	auto game_over_surface = SDL_LoadBMP("..//Resources//images//game_over.bmp");

	if (game_over_surface == nullptr) {
		DEBUG("Unable to load image! SDL Error: " << SDL_GetError());
	} else {
		game_over_ = SDL_CreateTextureFromSurface(window_renderer_, game_over_surface);
		SDL_FreeSurface(game_over_surface);
		game_over_surface = nullptr;

		CreateMenuCoords();
	}

	CreateResultList(new_high_score, results);
}

void SDLvideo::CreateResultList(bool new_high_score, std::vector<std::string> results) {
	auto result_list = BuildStringListOfResults(results);

	std::string text;

	if (new_high_score) {
		text = "New highscore!\n\n";
	}

	text += result_list;

	DEBUG(text);

	game_over_text_surface_ =
			TTF_RenderText_Blended_Wrapped(game_font_, text.c_str(), sdl_color_, SCREEN_WIDTH);

	if (game_over_text_surface_ == nullptr) {
		DEBUG("Unable to render game over text surface! SDL_ttf Error: " << TTF_GetError());
	} else {
		game_over_text_texture_ =
				SDL_CreateTextureFromSurface(window_renderer_, game_over_text_surface_);

		if (game_over_text_texture_ == nullptr) {
			DEBUG("Unable to render game over text texture! SDL_ttf Error: " << TTF_GetError());
		} else {
			game_over_text_coords_.h = game_over_text_surface_->h;
			game_over_text_coords_.w = game_over_text_surface_->w;
			game_over_text_coords_.x = 
				static_cast<int>(SCREEN_WIDTH / 2 - game_over_text_coords_.w / 6.5);
			game_over_text_coords_.y = SCREEN_HEIGHT / 2 - game_over_text_coords_.h + GUI_AREA * 2;

			SDL_FreeSurface(game_over_text_surface_);
			game_over_text_surface_ = nullptr;

			DEBUG(game_over_text_coords_.h);
			DEBUG(game_over_text_coords_.w);
			DEBUG(game_over_text_coords_.x);
			DEBUG(game_over_text_coords_.y);
		}
	}
}

std::string SDLvideo::BuildStringListOfResults(std::vector<std::string> results) {
	std::string result_list = "Highscores:\n\n";

	std::for_each(results.begin(), results.end(), [&result_list](std::string result) {
		result_list += result + "\n";
	});

	return result_list;
}

void SDLvideo::CreateMenuCoords() {
	menu_coords_.h = screenSurface_->h;
	menu_coords_.w = screenSurface_->w;
	menu_coords_.x = 0;
	menu_coords_.y = 0;
}

void SDLvideo::RenderMenu(SDL_Texture* const texture) {
	SDL_RenderCopy(window_renderer_, texture, nullptr, &menu_coords_);

	if (texture == game_over_) {
		SDL_RenderCopy(window_renderer_, game_over_text_texture_, nullptr, &game_over_text_coords_);
	}
}

void SDLvideo::RenderGUI(
	int level, 
	int current_lives, int current_score) {
	// no reconstruct texture if score not updated
	if (current_score != old_score_) {
		SDL_FreeSurface(update_text_surface_);
		update_text_surface_ = nullptr;

		SDL_DestroyTexture(update_text_texture_);
		update_text_texture_ = nullptr;

		auto text = "Level: " + std::to_string(level) + " Score: " + std::to_string(current_score);

		update_text_surface_ = TTF_RenderText_Solid(game_font_, text.c_str(), sdl_color_);

		update_text_coords_.h = update_text_surface_->h;
		update_text_coords_.w = update_text_surface_->w;

		if (update_text_surface_ == nullptr) {
			DEBUG("Unable to render text surface! SDL_ttf Error: " << TTF_GetError());
		} else {
			update_text_texture_ = SDL_CreateTextureFromSurface(window_renderer_, update_text_surface_);

			if (update_text_texture_ == nullptr) {
				DEBUG("Unable to create texture from rendered text! SDL Error: " << SDL_GetError());
			} else {
				RenderText(update_text_texture_);
			}

			old_score_ = current_score;
		}
	} else {
		RenderText(update_text_texture_);
	}

	// generates lives marker on screen
	for (auto i = 0; i < current_lives; ++i) {
		SDL_Rect life_placement;
		life_placement.h = GUI_AREA / 6;
		life_placement.w = SCREEN_WIDTH / 15;
		life_placement.y = life_placement.h * 3;

		if (life_placement.y + life_placement.h > GUI_AREA) {
			life_placement.y = GUI_AREA - life_placement.h;
		}

		life_placement.x = TEXT_OFFSET + i * (life_placement.w + 10); // 10 is spacer for lives

		auto player = GameManager::Instance().GetPlayer();
		SDL_RenderCopy(
			window_renderer_, 
			game_objects_textures_[player->GetName()], 
			nullptr, 
			&life_placement);
	}
}

void SDLvideo::RenderText(SDL_Texture* const text_texture) {
	update_text_coords_.x = SCREEN_WIDTH - TEXT_OFFSET - update_text_coords_.w;

	SDL_RenderCopy(window_renderer_, text_texture, nullptr, &update_text_coords_);
}

void SDLvideo::RenderGameObjects(const std::vector<std::shared_ptr<GameObject>>& game_objects) {
	for (auto const& go : game_objects) {
		auto go_coords = go->GetCoords();
		
		SDL_RenderCopy(
			window_renderer_, 
			game_objects_textures_[go->GetName()], 
			nullptr, 
			go_coords);
	}
}

void SDLvideo::CreatePlayerTexture(Player const& player) {
	auto player_name = player.GetName();

	DEBUG("Adding player " << player_name);

	// create optimized png
	auto temp_surface = player.GetSurface();
	auto player_surface = SDL_ConvertSurfaceFormat(temp_surface, screenSurface_->format->format, 0);
	
	SDL_FreeSurface(const_cast<SDL_Surface*>(temp_surface));
	temp_surface = nullptr;

	// create texture
	auto player_texture_ = SDL_CreateTextureFromSurface(
								window_renderer_, 
								const_cast<SDL_Surface*>(player_surface));

	if (player_texture_ == nullptr) {
		DEBUG(("Unable to make texture! SDL Error: ", SDL_GetError()));
	} else {
		game_objects_textures_[player_name] = player_texture_;

		DEBUG("Added player " << player_name <<
					  " Texture: " << game_objects_textures_[player_name]);
		DEBUG("Player ready...");

		SDL_FreeSurface(const_cast<SDL_Surface*>(player_surface));
		player_surface = nullptr;
	}
}

void SDLvideo::CreateBallTexture(Ball const& ball) {
	auto ball_name = ball.GetName();

	DEBUG("Adding ball " << ball_name);
	
	// create optimized png
	auto temp_surface = ball.GetSurface();
	auto ball_surface = SDL_ConvertSurfaceFormat(temp_surface, screenSurface_->format->format, 0);

	SDL_FreeSurface(const_cast<SDL_Surface*>(temp_surface));
	temp_surface = nullptr;

	// create texture
	auto ball_texture_ = SDL_CreateTextureFromSurface(
								window_renderer_, 
								const_cast<SDL_Surface*>(ball_surface));

	if (ball_texture_ == nullptr) {
		DEBUG(("Unable to make texture! SDL Error: ", SDL_GetError()));
	} else {
		game_objects_textures_[ball_name] = ball_texture_;

		DEBUG("Added ball " << ball_name << " Texture: " << game_objects_textures_[ball_name]);
		DEBUG("Ball ready...");

		SDL_FreeSurface(const_cast<SDL_Surface*>(ball_surface));
		ball_surface = nullptr;
	}
}

void SDLvideo::CreateBrickTexture(Brick const& brick) {
	auto brick_name = brick.GetName();

	DEBUG("Adding brick " << brick_name);
	auto brick_fly_weight = brick.GetBrickFlyWeight();
	auto brick_type = brick_fly_weight->GetBrickType();

	if (!brick_type_texture_.count(brick_type)) {
		DEBUG("Making new texture");
		brick_type_texture_[brick_type] =
				SDL_CreateTextureFromSurface(
					window_renderer_,
					const_cast<SDL_Surface*>(brick_fly_weight->GetBrickSurface()));
		DEBUG("Made new texture: " << brick_type_texture_[brick_type]);
	}

	game_objects_textures_[brick_name] = brick_type_texture_[brick_type];

	DEBUG("Added brick " << brick_name << " Texture: " << game_objects_textures_[brick_name]);
}

void SDLvideo::KillGameObjectTexture(const GameObject& go) {
	auto go_name = go.GetName();

	if (!dynamic_cast<Brick*>(const_cast<GameObject*>(&go))) {
		DEBUG("Destroying texture: " << go_name);
		SDL_DestroyTexture(game_objects_textures_[go_name]);
		game_objects_textures_[go_name] = nullptr;
	}
}

void SDLvideo::KillGameOver() {
	SDL_DestroyTexture(game_over_);
	game_over_ = nullptr;
	SDL_DestroyTexture(game_over_text_texture_);
	game_over_text_texture_ = nullptr;
}

void SDLvideo::KillIntro() {
	SDL_DestroyTexture(intro_);
	intro_ = nullptr;
}

void SDLvideo::CleanUp() {
	for (auto it = game_objects_textures_.begin(); it != game_objects_textures_.end(); ++it) {
		DEBUG("Destroying game_object texture: " << it->second);
		// no longer needed, can be freed concurrently.
		std::thread destroy_thread(SDL_DestroyTexture, it->second);
		destroy_thread.detach();
	}

	game_objects_textures_.clear();

	for (auto it = brick_type_texture_.begin(); it != brick_type_texture_.end(); ++it) {
		DEBUG("Destroying brick texture: " << it->second);
		// no longer needed, can be freed concurrently.
		std::thread destroy_thread(SDL_DestroyTexture, it->second);
		destroy_thread.detach();
	}

	brick_type_texture_.clear();

	SDL_DestroyTexture(intro_);
	intro_ = nullptr;
}

bool SDLvideo::Kill() {
	DEBUG("Killing SDLvideo...");

	CleanUp();

	// free update text resources
	SDL_FreeSurface(update_text_surface_);
	update_text_surface_ = nullptr;
	SDL_DestroyTexture(update_text_texture_);
	update_text_texture_ = nullptr;

	// free game over resources
	SDL_DestroyTexture(game_over_);
	game_over_ = nullptr;
	SDL_DestroyTexture(game_over_text_texture_);
	game_over_text_texture_ = nullptr;

	// free window surface
	SDL_FreeSurface(screenSurface_);
	screenSurface_ = nullptr;

	// free renderer
	SDL_DestroyRenderer(window_renderer_);
	window_renderer_ = nullptr;

	// free window
	SDL_DestroyWindow(window_);
	window_ = nullptr;

	// free font
	TTF_CloseFont(game_font_);
	game_font_ = nullptr;

	// ensure closing of subelements of SDL //
	TTF_Quit();
	IMG_Quit();
	SDL_VideoQuit();
	// ensure closing of subelements of SDL //

	DEBUG("SDLvideo dead...");
	return true;
}