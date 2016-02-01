#pragma once
#ifndef BREAKOUT_GAME_MANAGER_
#define BREAKOUT_GAME_MANAGER_

// C++ libraries
#include <vector>

// local headers
#include "LevelManager.h"
#include "InputManager.h"
#include "CollisionManager.h"
#include "ScoreManager.h"
#include "SDLvideo.h"
#include "SDLsound.h"
#include "Component.h"

// forward declarations
class DeltaTimer;
class Collision;
class GameObject;
class Player;
class Ball;
class Brick;
class CollisionComponent;

// enumerations
// gamestate will be accesed everywhere and needs to be availible at compiletime
enum EGameState { INTRO, INIT, RUNNING, PAUSED, RESULT, EXIT, TERMINATE };
enum EMusic;
enum ESound;

/**
 * This header file declares the GameManager. The GameManager is responsible for keeping track
 * of the state of the game and mediating communication between the different managers and
 * GameObjects/Components of the game. This is what ensures low coupling in my application as
 * every GameObject every only has one object to talk to and the GameManager also mediates all
 * information from Managers. One might say that this gives GameManager High Coupling and Low
 * Cohesion, but I disagree. It is only doing one thing, and it does it well.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class GameManager {
public:
	// typedefs
	typedef std::shared_ptr<GameObject> shared_ptr_go;

	// Meyer singleton pattern
	static GameManager& Instance() {
		static GameManager singleton;
		return singleton;
	}

	// game state
	EGameState GetGameState() { return game_state_; }

	// gameloop
	void RunGameLoop();
	// game events
	void BallDeath();
	void PlayerHitSound() { sdl_sound_.PlaySound(SDLsound::PLAYER_HIT_SOUND); }

	//player
	int CheckPlayerMovement();

	// collisiondetection
	void GameObjectMoved(GameObject& go) { collision_manager_.UpdateGameObjectPosition(go); }
	void CheckForCollisions(CollisionComponent& collision_component, GameObject& go);

	// prepare garbage collection
	void KillGameObject(GameObject& go);

	// gameobject initialization
	void AddTexture(Player const& player) { sdl_video_.CreatePlayerTexture(player); }
	void AddTexture(Ball const& ball) { sdl_video_.CreateBallTexture(ball); }
	void AddTexture(Brick const& brick) { sdl_video_.CreateBrickTexture(brick); }
	void AddGameObject(shared_ptr_go go);

	// getters
	const float& GetDeltaTime() { return delta_time_; }
	const Player* GetPlayer() const { return level_manager_.GetPlayer(); }
	const float& GetLevelVelocity() { return level_manager_.GetLevelVelocity(); }
private:
	// typedefs 
	typedef std::vector<shared_ptr_go> vector_of_shared_ptrs_go;
	typedef std::vector<GameObject*> vector_of_go_ptrs;
	typedef std::vector<std::string> vector_of_strings;

	// Meyer singleton pattern
	GameManager() {}
	GameManager(GameManager const&);
	GameManager& operator=(GameManager const&);
	~GameManager();
	
	// multithreading
	void DoAsyncRender();

	// config init
	EGameState game_state_;
	SDLvideo sdl_video_;
	SDLsound sdl_sound_;

	// managers
	InputManager input_manager_;
	CollisionManager collision_manager_;
	LevelManager level_manager_;
	ScoreManager score_manager_;

	// gameobjects
	vector_of_shared_ptrs_go game_objects_;

	// initialize game
	void InitiateGame();

	// fps cap
	const float SCREEN_FPS_ = static_cast<float>(sdl_video_.GetRefreshRate());
	const float SCREEN_TICKS_PER_FRAME_ = 1000.f / SCREEN_FPS_;

	// gameloop
	float delta_time_;
	void PrepareGame();
	void CheckForKeysIntro();
	void CheckForKeysInitAndRunning();
	void IterateComponents();
	void CheckForTerminatedWindow();
	void CheckForFPSSynchronization(DeltaTimer* fps_cap_timer);
	void CheckForFPSSynchronization(DeltaTimer* delta_timer, DeltaTimer* fps_cap_timer);
	void CheckForClearedLevel();
	void IncreaseLevel();
	void ExitGameIfPlayerDead();
	void CheckForKeysGameOver();
	void GameOver(DeltaTimer* fps_cap_timer);
	bool ValidateOrUpdateResults(vector_of_strings& results);
	int GetScoreFromResultLine(std::string line);

	// garbage collection
	bool garbage_collection_active;
	vector_of_go_ptrs destroyables_;
	void KillDestroyables();

	// exit game
	void ExitGame();

	// cleaners
	void CleanUp();
};

#endif

