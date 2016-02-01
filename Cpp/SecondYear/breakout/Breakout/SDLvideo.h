#pragma once
#ifndef BREAKOUT_SDL_VIDEO_
#define BREAKOUT_SDL_VIDEO_

// C libraries
#include <SDL.h>
#include <SDL_ttf.h>

// C++ libraries
#include <vector>
#include <unordered_map>
#include <memory>

// local headers
#include "BrickFlyWeight.h"

// forward declarations
class GameObject;
class Player;
class Ball;
class Brick;

// enumerations
enum EBrickType;
enum EGameState;

/**
 * This header file declares the facade for SDL video rendering. It accepts normal game
 * mechanical items such as GameObjects and gui texts and abstracts away any SDL for the
 * outside classes.
 *
 * I showcase use of SDL_image for pngs for the player and ball, normal BMP for bricks.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class SDLvideo {
public:
	//typedefs
	typedef std::vector<std::shared_ptr<GameObject>> vector_of_shared_go_ptrs;
	typedef std::vector<std::string> vector_of_strings;

	SDLvideo();
	~SDLvideo();

	// config
	void Init();
	void CleanUp();
	bool Kill();

	// gameloop
	void Update(
		EGameState game_state, 
		const vector_of_shared_go_ptrs& game_objects,
		int level, int current_lives, int current_score);

	// getter for refreshrate
	int GetRefreshRate() const { return refresh_rate_; }

	// generation of textures
	void CreateIntroTexture();
	void CreateGameOverTexture(bool new_high_score, vector_of_strings results);
	void CreatePlayerTexture(const Player& player);
	void CreateBrickTexture(const Brick& brick);
	void CreateBallTexture(const Ball& ball);

	// destruction of textures
	void KillGameObjectTexture(const GameObject& go);
	void KillGameOver();
	void KillIntro();

	// getters for consts (and showing i can make class methods)
	static int GetScreenWidth() { return SCREEN_WIDTH; }
	static int GetScreenHeight() { return SCREEN_HEIGHT; }
	static int GetGUIArea() { return GUI_AREA; }
private:
	// typedefs
	typedef std::unordered_map<BrickFlyWeight::EBrickType, SDL_Texture*> map_of_brick_textures;
	typedef std::unordered_map<int, SDL_Texture*> map_of_go_textures;

	// SDL structs
	SDL_Window* window_;
	SDL_Surface* screenSurface_;
	SDL_Renderer* window_renderer_;
	SDL_Texture* intro_;
	SDL_Texture* game_over_;
	SDL_Rect menu_coords_;

	// text
	TTF_Font* game_font_;
	SDL_Color sdl_color_;

	// update
	SDL_Surface* update_text_surface_;
	SDL_Texture* update_text_texture_;
	SDL_Rect update_text_coords_;
	void RenderGameObjects(const vector_of_shared_go_ptrs& game_objects);

	// refresh_rate for determining fps in game manager
	int refresh_rate_;

	// game over results
	SDL_Surface* game_over_text_surface_;
	SDL_Texture* game_over_text_texture_;
	SDL_Rect game_over_text_coords_;

	// GUI
	const int TEXT_OFFSET = 75;
	int old_score_;
	void CreateResultList(bool new_high_score, vector_of_strings results);
	std::string BuildStringListOfResults(vector_of_strings results);
	void CreateMenuCoords();
	void RenderMenu(SDL_Texture* const texture);
	void RenderGUI(int level, int current_lives, int current_score);
	void RenderText(SDL_Texture* const text_texture);

	// gameobjects
	map_of_brick_textures brick_type_texture_;
	map_of_go_textures game_objects_textures_;

	// consts
	static const int SCREEN_WIDTH = 1280;
	static const int SCREEN_HEIGHT = 960;

	static const int GUI_AREA = SCREEN_HEIGHT / 10;
};

#endif
