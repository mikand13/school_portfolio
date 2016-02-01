#pragma once
#ifndef BREAKOUT_LEVEL_MANAGER_
#define BREAKOUT_LEVEL_MANAGER_

// local headers
#include "BrickFactory.h"
#include "GameObjectSpawner.h"

// forward declarations
class BrickCollisionComponent;
class PlayerCollisionComponent;
class PlayerMovementComponent;
class BallCollisionComponent;
class BallMovementComponent;
class MovementComponent;
class CollisionComponent;
class Player;
class Ball;
class GameObject;

// enumerations
enum EBrickType;

/**
 * This header file declares the LevelManager. The LevelManager is responsible for the creation
 * and initialization of all gameplay elements of a level.
 *
 * In this class i have implemented the Data Locality Pattern for my components, this GREATLY
 * increased the performance of my gameloop.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class LevelManager {
public:
	// typedefs
	typedef std::vector<MovementComponent*> vector_of_ptrs_movement_components;
	typedef std::vector<CollisionComponent*> vector_of_ptrs_collision_components;

	LevelManager();
	~LevelManager();

	// component getters
	vector_of_ptrs_movement_components GetMovables() const { return movement_components_; }
	vector_of_ptrs_collision_components GetCollidables() const { return collision_components_; }
	
	// gameplay operations
	void NewGame();
	void IncreaseLevel();

	// get singular gameobjects
	Player* GetPlayer() const { return player_; }
	Ball* GetBall() const { return ball_; }

	// gameplay info getters
	int GetLevel() const { return level_; }
	const float& GetLevelVelocity() const { return level_velocity_; }
	int GetPlayerLives() const { return current_lives_; }
	void ReducePlayerLives() { current_lives_--; }

	// cleaner
	void CleanUp();
private:
	// typedefs
	typedef std::vector<PlayerMovementComponent> vector_of_player_movement_components;
	typedef std::vector<BallMovementComponent> vector_of_ball_movement_components;
	typedef std::vector<PlayerCollisionComponent> vector_of_player_collision_components;
	typedef std::vector<BallCollisionComponent> vector_of_ball_collision_components;
	typedef std::vector<BrickCollisionComponent> vector_of_brick_collision_components;

	// init
	void InitLevel();

	// creation
	void CreatePlayer();
	void CreateBall();
	void CreateBricks();
	void MakeBricks(BrickFlyWeight::EBrickType brick_type, int i);

	// level
	int level_;
	float level_velocity_;

	// player
	GameObjectSpawner<Player> player_spawner_;
	const int TOTAL_PLAYER_LIVES = 3;
	Player* player_;
	int current_lives_;

	// ball
	GameObjectSpawner<Ball> ball_spawner_;
	Ball* ball_;

	// bricks
	BrickFactory brick_factory_;

	// Components
	// ENSURES CONTIGUOUS DATA LOCALITY //
	vector_of_ptrs_movement_components movement_components_;
	vector_of_player_movement_components player_movement_components_;
	vector_of_ball_movement_components ball_movement_components_;
	
	vector_of_ptrs_collision_components collision_components_;
	vector_of_player_collision_components player_collision_components_;
	vector_of_ball_collision_components ball_collision_components_;
	vector_of_brick_collision_components brick_collision_components_;
	// ENSURES CONTIGUOUS DATA LOCALITY //
};

#endif

