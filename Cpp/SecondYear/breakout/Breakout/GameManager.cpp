#include "GameManager.h"

// C++ libraries
#include <thread>
#include <iostream>
#include <string>
#include <sstream>

// local headers
#include "Debug.h"
#include "SDLvideo.h"
#include "SDLsound.h"
#include "DeltaTimer.h"
#include "Collision.h"
#include "GameObject.h"
#include "Ball.h"
#include "Brick.h"
#include "SDLInitializationException.h"

/**
 * Several inline private methods in this class. This is my attempt to increase performance.
 * However I am not entirely convinced they should all be inline because i might get a bloated
 * executable but I'll leave that up to the compiler with the inline keyword.
 */

GameManager::~GameManager() {
	DEBUG_DESTRUCTOR("GameManager has been destroyed...");
}

void GameManager::PrepareGame() {
	try {
		sdl_video_.Init();
		sdl_sound_.Init();
	} catch (SDLInitializationException sie) {
		std::cout << sie.what() << " Exiting...";

		abort();
	}

	game_state_ = INTRO;

	sdl_video_.CreateIntroTexture();
	sdl_sound_.PlaySong(SDLsound::INTRO_MUSIC);

	// initialize the InputManager
	input_manager_.Update();
}

void GameManager::RunGameLoop() {
	PrepareGame();

	DeltaTimer delta_timer;
	delta_timer.Start();

	DeltaTimer fps_cap_timer;
	fps_cap_timer.Start();

	// gameloop
	while (game_state_ != TERMINATE) {
		// fps_cap_timer
		fps_cap_timer.Reset();

		// update timer
		delta_timer.Update();

		// deltatime
		delta_time_ = delta_timer.GetDeltaTime();
		//DEBUG_FPS(delta_time_);

		// gather input
		input_manager_.Update();

		// global exit
		CheckForTerminatedWindow();

		// introscreeninput
		if (game_state_ == INTRO) {
			CheckForKeysIntro();
		}

		// game running or ball waiting
		if (game_state_ == RUNNING || game_state_ == INIT) {
			CheckForKeysInitAndRunning();

			IterateComponents();

			// player dead?
			ExitGameIfPlayerDead();
		}

		// garbage collection
		KillDestroyables();

		// level cleared?
		CheckForClearedLevel();

		// render asynchronally with lambdathread. 
		// & references this instance (singleton).
		std::thread render_thread([&]() {
			DoAsyncRender();
		});

		// pause for fps sync if necessary
		CheckForFPSSynchronization(&delta_timer, &fps_cap_timer);

		// wait for complete render
		render_thread.join();
		
		// handle gameover actions
		GameOver(&fps_cap_timer);
	}

	// cleans up and quits the game
	ExitGame();
}

inline void GameManager::ExitGameIfPlayerDead() {
	if (level_manager_.GetPlayerLives() < 1) {
		game_state_ = EXIT;
	}
}

void GameManager::GameOver(DeltaTimer* fps_cap_timer) {
	auto fps_cap_timer_ = fps_cap_timer;

	if (game_state_ == EXIT) {
		// calculate results
		auto results = score_manager_.GetHighScores();
		auto new_highscore = ValidateOrUpdateResults(results);

		score_manager_.Reset();

		if (new_highscore) {
			score_manager_.WriteHighScores(results);
		}

		CleanUp();
		sdl_video_.CreateGameOverTexture(new_highscore, results);
		sdl_sound_.SwapSong(SDLsound::GAMEOVER_MUSIC);

		while (game_state_ == EXIT) {
			// fps_cap_timer
			fps_cap_timer_->Reset();

			// gather input
			input_manager_.Update();

			// global exit
			CheckForTerminatedWindow();

			// gameover input
			CheckForKeysGameOver();

			// update render asynchronally with lambdathread. 
			// & references this instance (singleton).
			std::thread t([&]() {
				DoAsyncRender();
			});

			// pause for fps sync if necessary
			CheckForFPSSynchronization(fps_cap_timer_);

			// wait for complete render
			t.join();
		}
	}
}

inline void GameManager::DoAsyncRender() {
	sdl_video_.Update(
		game_state_,
		game_objects_,
		level_manager_.GetLevel(),
		level_manager_.GetPlayerLives(),
		score_manager_.GetScore());
}

void GameManager::CheckForCollisions(
	CollisionComponent& collision_component, GameObject& go) {
	auto collisions = collision_manager_.CheckForCollisions(go);

	if (collisions->size() > 0) {
		for (auto const& collision : *collisions.get()) {
			collision_component.HandleCollision(*collision->GetTwo());
			collision->GetTwo()->GetCollider()->HandleCollision(*collision->GetOne());
		}
	}
}

bool GameManager::ValidateOrUpdateResults(std::vector<std::string>& results) {
	// if less than 5 registered, pop it into the list.
	if (results.size() < ScoreManager::HIGH_SCORE_LIMIT) {
		std::string result_string = "Level: ";
		result_string.append(std::to_string(level_manager_.GetLevel()));
		result_string.append(" ");
		result_string.append(std::to_string(score_manager_.GetScore()));

		results.push_back(result_string);
	}

	// check if new score should on the list, and / or climb it.
	if (score_manager_.GetScore() > GetScoreFromResultLine(results[results.size() - 1])) {
		std::string result_string = "Level: ";
		result_string.append(std::to_string(level_manager_.GetLevel()));
		result_string.append(" ");
		result_string.append(std::to_string(score_manager_.GetScore()));

		results[results.size() - 1] = result_string;

		for (auto i = results.size() - 1; i > 0; --i) {
			DEBUG("Checking next element of resultlist: " << results[i - 1]);
			if (GetScoreFromResultLine(results[i]) > GetScoreFromResultLine(results[i - 1])) {
				auto defeated_score = results[i - 1];
				results[i - 1] = results[i];
				results[i] = defeated_score;
			}
		}

		return true;
	}
		
	return false;
}

int GameManager::GetScoreFromResultLine(std::string line) {
	std::vector<std::string> tokens;
	std::stringstream ss(line);
	std::string item;

	while (std::getline(ss, item, ' ')) {
		tokens.push_back(item);
	}

	// points score
	return atoi(tokens.back().c_str());
}

void GameManager::AddGameObject(std::shared_ptr<GameObject> go) {
	game_objects_.push_back(go);
	collision_manager_.AddGameObjectToCollisionGrid(*go);
}

void GameManager::KillGameObject(GameObject& go) {
	collision_manager_.RemoveGameObjectFromCollisionGrid(go);
	DEBUG("Destroyable GameObject removed from collisiongrid: " << go.GetName());

	// prepare go for gc
	destroyables_.push_back(&go);

	// score
	score_manager_.AddScore(go, level_manager_.GetLevel());

	// sound if any
	if (dynamic_cast<Brick*>(&go)) {
		sdl_sound_.PlaySound(SDLsound::BRICK_HIT_SOUND);
	}
}

inline void GameManager::CheckForClearedLevel() {
	if (game_state_ == RUNNING && game_objects_.size() < 5) {
		auto brick_found = false;

		for (auto const& go : game_objects_) {
			if (dynamic_cast<Brick*>(go.get())) {
				brick_found = true;
				break;
			}
		}

		if (!brick_found) {
			IncreaseLevel();
		}
	}
}

inline void GameManager::InitiateGame() {
	sdl_sound_.SwapSong(SDLsound::GAME_MUSIC);
	level_manager_.NewGame();
}

inline void GameManager::IncreaseLevel() {
	CleanUp();
	game_state_ = INIT;
	level_manager_.IncreaseLevel();
}

void GameManager::BallDeath() {
	sdl_sound_.PlaySound(SDLsound::BALL_DEATH_SOUND);
	level_manager_.ReducePlayerLives();
}

// demonstrate use of functor for calling member function of my components
void GameManager::IterateComponents() {
	auto movables = level_manager_.GetMovables();
	std::for_each(movables.begin(), movables.end(), std::mem_fun(&MovementComponent::Update));

	if (game_state_ == RUNNING) {
		auto collidables = level_manager_.GetCollidables();
		std::for_each(
				collidables.begin(), collidables.end(), std::mem_fun(&CollisionComponent::Update));
	}
}

inline void GameManager::CheckForFPSSynchronization(DeltaTimer* fps_cap_timer) {
	fps_cap_timer->Update();
	auto frame_time = fps_cap_timer->GetDeltaTime();
	if (frame_time <= SCREEN_TICKS_PER_FRAME_) {
		auto sleep_time = SCREEN_TICKS_PER_FRAME_ - frame_time;
		DEBUG_FPS("Thread: " << sleep_time << " Frame: " << frame_time);
		SDL_Delay(static_cast<Uint32>(sleep_time));
	} else {
		DEBUG_CAP("OVERSHOT FPS CAP WITH: " << frame_time - SCREEN_TICKS_PER_FRAME);
	}
}

inline void GameManager::CheckForFPSSynchronization(
		DeltaTimer* delta_timer, DeltaTimer* fps_cap_timer) {
	fps_cap_timer->Update();
	auto frame_time = fps_cap_timer->GetDeltaTime();

	if (frame_time <= SCREEN_TICKS_PER_FRAME_) {
		auto sleep_time = SCREEN_TICKS_PER_FRAME_ - frame_time;
		DEBUG_FPS("Thread: " << sleep_time << " Frame: " << frame_time);
		delta_timer->Pause();
		// reduce by 1 ms to allow for correct vsync wait, sleep_for instead of SDL_Delay for precision
		std::this_thread::sleep_for(std::chrono::milliseconds(static_cast<int>(sleep_time - 1.f)));
		delta_timer->UnPause();
	} else {
		DEBUG_CAP("OVERSHOT FPS CAP WITH: " << frame_time - SCREEN_TICKS_PER_FRAME);
	}
}

inline void GameManager::KillDestroyables() {
	for (auto go : destroyables_) {
		for (size_t i = 0; i < game_objects_.size(); ++i) {
			if (*go == *game_objects_.at(i).get()) {
				// async texturedeletion is safe here
				DEBUG("Destroyable GameObject being removed from game_objects_: " << go->GetName());
				std::thread kill_thread([&]() { sdl_video_.KillGameObjectTexture(*go); });
				kill_thread.detach();
				game_objects_.erase(game_objects_.begin() + i);
				break;
			}
		}
		DEBUG("Destroyable has been destroyed!");
	}
	destroyables_.clear();
}

/**
 * Under follows the methods that communicate with the InputManager.
 */
inline void GameManager::CheckForKeysIntro() {
	if (input_manager_.KeyDown(SDL_SCANCODE_RETURN) ||
		input_manager_.KeyStillDown(SDL_SCANCODE_RETURN)) {
		InitiateGame();

		game_state_ = INIT;
		
		sdl_video_.KillIntro();
	} else if (input_manager_.KeyDown(SDL_SCANCODE_ESCAPE) ||
		input_manager_.KeyStillDown(SDL_SCANCODE_ESCAPE)) {
		game_state_ = TERMINATE;

		sdl_video_.KillIntro();
	}
}

inline void GameManager::CheckForKeysInitAndRunning() {
	if (input_manager_.KeyDown(SDL_SCANCODE_ESCAPE) ||
		input_manager_.KeyStillDown(SDL_SCANCODE_ESCAPE)) {

		game_state_ = EXIT;
		input_manager_.Clear();
	}

	if (input_manager_.KeyDown(SDL_SCANCODE_SPACE) ||
		input_manager_.KeyStillDown(SDL_SCANCODE_SPACE)) {

		level_manager_.GetBall()->ActivateBall();


		if (game_state_ == INIT) {
			game_state_ = RUNNING;
		}
	}
}

int GameManager::CheckPlayerMovement() {
	if (input_manager_.KeyDown(SDL_SCANCODE_LEFT) ||
		input_manager_.KeyStillDown(SDL_SCANCODE_LEFT)) {
		return SDL_SCANCODE_LEFT;
	}

	if (input_manager_.KeyDown(SDL_SCANCODE_RIGHT) ||
		input_manager_.KeyStillDown(SDL_SCANCODE_RIGHT)) {
		return SDL_SCANCODE_RIGHT;
	}

	return 0;
}

inline void GameManager::CheckForKeysGameOver() {
	if (input_manager_.KeyDown(SDL_SCANCODE_RETURN) ||
		input_manager_.KeyStillDown(SDL_SCANCODE_RETURN)) {
		game_state_ = INTRO;
		sdl_video_.CreateIntroTexture();
		sdl_sound_.SwapSong(SDLsound::INTRO_MUSIC);

		input_manager_.Clear();

		sdl_video_.KillGameOver();
	} else if (input_manager_.KeyDown(SDL_SCANCODE_ESCAPE) ||
		input_manager_.KeyStillDown(SDL_SCANCODE_ESCAPE)) {
		game_state_ = TERMINATE;
	}
}

inline void GameManager::CheckForTerminatedWindow() {
	if (SDL_HasEvent(SDL_QUIT)) {
		game_state_ = TERMINATE;
	}
}
/**
 * End InputManager methods.
 */

inline void GameManager::CleanUp() {
	// clean up any destroyables
	KillDestroyables();

	// clean up textures
	sdl_video_.CleanUp();

	// clean/reset collision_grid
	collision_manager_.CleanUp();

	// kill all go components
	level_manager_.CleanUp();

	// destroy all go's
	game_objects_.clear();
}

inline void GameManager::ExitGame() {
	CleanUp();

	sdl_video_.Kill();
	sdl_sound_.Kill();

	DEBUG("Shutdown SDL...");
	SDL_Quit();
}
