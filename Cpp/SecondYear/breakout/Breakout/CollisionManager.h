#pragma once
#ifndef BREAKOUT_COLLISION_MANAGER_
#define BREAKOUT_COLLISION_MANAGER_

// C++ libraries
#include <vector>
#include <array>
#include <memory>

// local headers
#include "SDLvideo.h"

// forward declarations
class Collision;
class GameObject;

/**
 * This header file declares the CollisionManager for my game. It uses a spatial partitioning grid
 * and checks single objects against it. It's is designed to only be sent movables every frame.
 * That compared with a spatial partioning grid and a simple BoundingBox collision detection
 * gives great performance!
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class CollisionManager {
public:
	// typedefs
	typedef std::unique_ptr<std::vector<std::shared_ptr<Collision>>> vector_of_collisions;

	CollisionManager() {}
	~CollisionManager();

	// grid operations
	void AddGameObjectToCollisionGrid(GameObject& go);
	void UpdateGameObjectPosition(GameObject& go);
	void RemoveGameObjectFromCollisionGrid(GameObject& go);

	// collisionmanagement
	vector_of_collisions CheckForCollisions(const GameObject& go);

	// cleaner
	void CleanUp();
private:
	// typedefs
	typedef std::unique_ptr<std::vector<std::shared_ptr<Collision>>> vector_of_collisions;
	typedef std::vector<std::unordered_map<int, GameObject*>*> vector_of_go_maps;
	typedef std::array<std::array<std::unordered_map<int, GameObject*>, 11>, 11> two_d_arr_go_maps;

	const int GRID_HEIGHT = 10;
	const int GRID_WIDTH = 10;

	// collision operations
	bool CheckBoundingBoxCollision(const GameObject& one, const GameObject& two);

	// grid operations
	void PlaceGameObjectOnGrid(
		int grid_height, int grid_width,
		GameObject& go,
		vector_of_go_maps& game_object_reference);

	// operations for goæs spanning more than two grids in either direction
	void ApplyAdditionalCalculationsForLargeGameObjects(
		int left, int right, int top, int bottom, 
		GameObject& go, 
		vector_of_go_maps& game_object_reference);

	void InsertWidthCentersOfLargeGameObjects(
		int height_marker, int less, int more, 
		GameObject& go, 
		vector_of_go_maps& game_object_reference);

	void InsertHeightCentersOfLargeGameObjects(
		int left, int right, int less, int more, 
		GameObject& go, 
		vector_of_go_maps& game_object_reference);

	// ARRAY BOUNDS MUST ALWAYS BE 1 HIGHER THAN GRID VALUES
	two_d_arr_go_maps collision_grid_;
};

#endif

