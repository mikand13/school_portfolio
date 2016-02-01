#include "LevelManager.h"

// local headers
#include "Debug.h"
#include "GameManager.h"
#include "SDLvideo.h"
#include "Player.h"
#include "Ball.h"
#include "BrickFactory.h"
#include "PlayerMovementComponent.h"
#include "PlayerCollisionComponent.h"
#include "BallMovementComponent.h"
#include "BallCollisionComponent.h"
#include "BrickCollisionComponent.h"

LevelManager::LevelManager() : 
	player_(nullptr),
	ball_(nullptr) {
	//ensures no rearrangement of vectors as components are added
	collision_components_.reserve(400);
	brick_collision_components_.reserve(400);
}

LevelManager::~LevelManager() {
	DEBUG_DESTRUCTOR("Levelmanager has been destroyed...");
}

void LevelManager::NewGame() {
	current_lives_ = TOTAL_PLAYER_LIVES;
	level_ = 1;
	level_velocity_ = 0.5f;

	InitLevel();
}

void LevelManager::IncreaseLevel() {
	level_++;
	level_velocity_ += 0.1f;

	// recreate level
	InitLevel();
}

inline void LevelManager::InitLevel() {
	CreatePlayer();
	CreateBall();
	CreateBricks();
}

void LevelManager::CleanUp() {
	//clears all flyweights
	brick_factory_.CleanUp();

	//clear all components
	collision_components_.clear();
	player_collision_components_.clear();
	ball_collision_components_.clear();
	brick_collision_components_.clear();
	movement_components_.clear();
	player_movement_components_.clear();
	ball_movement_components_.clear();
}

void LevelManager::CreatePlayer() {
	DEBUG("Making player...");

	try {
		auto player = player_spawner_.Spawn();
		player_ = player.get();

		// ADDS AND SETS DATA LOCALITY FOR COMPONENTS //
		player_movement_components_.push_back(PlayerMovementComponent(player.get()));
		movement_components_.push_back(&player_movement_components_.back());
		player->AddComponent(*movement_components_.back());

		player_collision_components_.push_back(PlayerCollisionComponent(player.get()));
		collision_components_.push_back(&player_collision_components_.back());
		player->AddComponent(*collision_components_.back());
		// ADDS AND SETS DATA LOCALITY FOR COMPONENTS //

		auto player_coords = player->GetCoords();
		auto player_surface = player->GetSurface();

		player_coords->x = SDLvideo::GetScreenWidth() / 2 - (player_surface->w / 2);
		player_coords->y = SDLvideo::GetScreenHeight() - player_surface->h * 2;

		GameManager::Instance().AddTexture(*player);
		GameManager::Instance().AddGameObject(move(player));

		DEBUG("Player done...");
	} catch (std::bad_alloc ba) {
		DEBUG("LevelManager could not create smart pointer for player: " << ba.what());
	}
}

void LevelManager::CreateBall() {
	DEBUG("Making ball...");

	try {
		auto ball = ball_spawner_.Spawn();
		ball_ = ball.get();

		// ADDS AND SETS DATA LOCALITY FOR COMPONENTS //
		ball_movement_components_.push_back(BallMovementComponent(ball.get()));
		movement_components_.push_back(&ball_movement_components_.back());
		ball->AddComponent(*movement_components_.back());

		ball_collision_components_.push_back(BallCollisionComponent(ball.get()));
		collision_components_.push_back(&ball_collision_components_.back());
		ball->AddComponent(*collision_components_.back());
		// ADDS AND SETS DATA LOCALITY FOR COMPONENTS //

		auto ball_coords = ball->GetCoords();
		auto ball_surface = ball->GetSurface();

		ball_coords->x = SDLvideo::GetScreenWidth() / 2 - (ball_surface->w / 2);
		ball_coords->y = SDLvideo::GetScreenHeight() - ball_surface->h * 2 - ball_surface->h;

		GameManager::Instance().AddTexture(*ball);
		GameManager::Instance().AddGameObject(move(ball));
	
		DEBUG("Ball done...");
	} catch (std::bad_alloc ba) {
		DEBUG("LevelManager could not create smart pointer for ball: " << ba.what());
	}
}

void LevelManager::CreateBricks() {
	for (auto i = 0; i < 6; ++i) {
		switch (i) {
		case 0:
			MakeBricks(BrickFlyWeight::RED, i);
			break;
		case 1:
			MakeBricks(BrickFlyWeight::ORANGE, i);
			break;
		case 2:
			MakeBricks(BrickFlyWeight::BROWN, i);
			break;
		case 3:
			MakeBricks(BrickFlyWeight::YELLOW, i);
			break;
		case 4:
			MakeBricks(BrickFlyWeight::GREEN, i);
			break;
		case 5:
			MakeBricks(BrickFlyWeight::BLUE, i);
			break;
		}
	}

	// "hack" to release all surfaces after brickinitilization
	// due to surface being used for SDL_Rect initialization on go's
	brick_factory_.FreeAllSurfaces();
}

void LevelManager::MakeBricks(BrickFlyWeight::EBrickType brick_type, int i) {
	DEBUG("Making brick...");

	auto GAME_WIDTH = INT16_MAX; //arbitrary starting value

	for (auto j = 0; j < GAME_WIDTH; ++j) {
		// returns a heap brick
		auto brick = brick_factory_.GetBrick(brick_type);

		if (brick != nullptr) {
			auto brick_w = brick->GetBrickFlyWeight()->GetBrickSurface()->w;

			if (j == 0) {
				GAME_WIDTH = (SDLvideo::GetScreenWidth() - brick_w * 2) / brick_w;
			}

			auto brick_coords = brick->GetCoords();

			auto spacer = 1; // pixel spacer for height and width
			auto WIDTH_OFFSET = 
				(SDLvideo::GetScreenWidth() - GAME_WIDTH * (brick_w + spacer)) / 2 + spacer;
			auto HEIGHT_OFFSET = brick_coords->h + spacer;

			brick_coords->y = SDLvideo::GetGUIArea() + HEIGHT_OFFSET * (2 + i);
			brick_coords->x = WIDTH_OFFSET + j * (brick_coords->w + spacer); 

			DEBUG("H: " << brick->GetCoords()->h);
			DEBUG("W: " << brick->GetCoords()->w);
			DEBUG("X: " << brick->GetCoords()->x);
			DEBUG("Y: " << brick->GetCoords()->y);
			DEBUG("Name: " << brick->GetName());

			// ADDS AND SETS DATA LOCALITY FOR COMPONENTS //
			brick_collision_components_.push_back(BrickCollisionComponent(brick.get()));
			collision_components_.push_back(&brick_collision_components_.back());
			brick->AddComponent(*collision_components_.back());
			// ADDS AND SETS DATA LOCALITY FOR COMPONENTS //

			GameManager::Instance().AddTexture(*brick);
			GameManager::Instance().AddGameObject(move(brick));

			DEBUG("Brick done...");
		} else {
			DEBUG("Bad allocation.... Brick not created...");
		}
	}
}