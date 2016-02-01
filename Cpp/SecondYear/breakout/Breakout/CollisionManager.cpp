#include "CollisionManager.h"

// local headers
#include "Debug.h"
#include "Collision.h"
#include "GameObject.h"
#include "SDLvideo.h"

CollisionManager::~CollisionManager() {
	DEBUG_DESTRUCTOR("CollisionManager has been destroyed...");
}

void CollisionManager::AddGameObjectToCollisionGrid(GameObject& go) {
	std::vector<std::unordered_map<int, GameObject*>*> game_object_reference;
	go.GetCollisionGridPosition()->clear();

	// go coords
	auto go_coords = go.GetCoords();
	
	// calculate gameobject sides
	auto left = go_coords->x;
	auto right = go_coords->x + go_coords->w;
	auto top = go_coords->y;
	auto bottom = go_coords->y + go_coords->h;

	// calculate placement in grid
	auto calculated_top = top * 100 / SDLvideo::GetScreenHeight() / GRID_HEIGHT;
	auto calculated_bottom = bottom * 100 / SDLvideo::GetScreenHeight() / GRID_HEIGHT;
	auto calculated_left = left * 100 / SDLvideo::GetScreenWidth() / GRID_WIDTH;
	auto calculated_right = right * 100 / SDLvideo::GetScreenWidth() / GRID_WIDTH;

	// place object in grids
	PlaceGameObjectOnGrid(calculated_top, calculated_left, go, game_object_reference);
	PlaceGameObjectOnGrid(calculated_top, calculated_right, go, game_object_reference);
	PlaceGameObjectOnGrid(calculated_bottom, calculated_left, go, game_object_reference);
	PlaceGameObjectOnGrid(calculated_bottom, calculated_right, go, game_object_reference);

	// adding centers of large gameobjects to grid
	ApplyAdditionalCalculationsForLargeGameObjects(
		calculated_left, 
		calculated_right, 
		calculated_top, 
		calculated_bottom,
		go, game_object_reference);

	go.SetCollisionGridPosition(game_object_reference);
}

void CollisionManager::ApplyAdditionalCalculationsForLargeGameObjects(
	int left, int right, 
	int top, int bottom, 
	GameObject& go, 
	std::vector<std::unordered_map<int, GameObject*>*>& game_object_reference) {
	if (left < right - 1) {
		InsertWidthCentersOfLargeGameObjects(
			bottom,
			left,
			right,
			go, game_object_reference);
	}

	if (top < bottom - 1) {
		InsertHeightCentersOfLargeGameObjects(
			left,
			top,
			right,
			bottom,
			go, game_object_reference);
	}
}

void CollisionManager::InsertWidthCentersOfLargeGameObjects(
	int height_marker, int less, int more, 
	GameObject& go, 
	std::vector<std::unordered_map<int, GameObject*>*>& game_object_reference) {
	for (auto i = 1; i <= more - less; ++i) {
		PlaceGameObjectOnGrid(height_marker, less + 1, go, game_object_reference);
	}
}

void CollisionManager::InsertHeightCentersOfLargeGameObjects(
	int left, int right, int less, int more, 
	GameObject& go, 
	std::vector<std::unordered_map<int, GameObject*>*>& game_object_reference) {
	for (auto i = 0; i < more - less; ++i) {
		PlaceGameObjectOnGrid(less + 1, left, go, game_object_reference);
		PlaceGameObjectOnGrid(less + 1, right, go, game_object_reference);

		if (left < right - 1) {
			InsertWidthCentersOfLargeGameObjects(
				less + 1,
				left,
				right,
				go, game_object_reference);
		}
	}
}

inline void CollisionManager::PlaceGameObjectOnGrid(
	int grid_height, int grid_width, 
	GameObject& go, 
	std::vector<std::unordered_map<int, GameObject*>*>& game_object_reference) {
	if (!collision_grid_[grid_height][grid_width].count(go.GetName())) {
		collision_grid_[grid_height][grid_width][go.GetName()] = &go;
		game_object_reference.push_back(&collision_grid_[grid_height][grid_width]);
	}
}

void CollisionManager::RemoveGameObjectFromCollisionGrid(GameObject& go) {
	for (auto const& cell : *go.GetCollisionGridPosition()) {
		for (auto it = cell->begin(); it != cell->end(); ++it) {
			if (go == *it->second) {
				cell->erase(it);
				break;
			}
		}
	}
}

void CollisionManager::UpdateGameObjectPosition(GameObject& go) {
	RemoveGameObjectFromCollisionGrid(go);
	AddGameObjectToCollisionGrid(go);
}

std::unique_ptr<std::vector<std::shared_ptr<Collision>>> CollisionManager::CheckForCollisions(
	const GameObject& go) {
	auto collisions = std::make_unique<std::vector<std::shared_ptr<Collision>>>();
	auto cells = go.GetCollisionGridPosition();
	std::vector<GameObject*> possible_collidables;

	// find possible collidables
	for (auto const& cell : cells) {
		for (auto it = cell->begin(); it != cell->end(); ++it) {
			if (!(find(
				possible_collidables.begin(),
				possible_collidables.end(),
				it->second) != possible_collidables.end())) {
				if (*it->second != go) {
					possible_collidables.push_back(it->second);
				}
			}
		}
	}

	// make collisions with collidables that collide on boundingbox check
	for (auto const& go_inner : possible_collidables) {
		if (CheckBoundingBoxCollision(go, *go_inner)) {
			collisions->push_back(std::move(std::make_shared<Collision>(go, *go_inner)));
		}
	}

	return move(collisions);
}

// I suppose inlining this will improve the performance, but im letting the compiler decide.
inline bool CollisionManager::CheckBoundingBoxCollision(
		const GameObject& one, const GameObject& two) {
	auto one_coords = one.GetCoords();
	auto two_coords = two.GetCoords();
	
	// bounding box check
	if (one_coords.y + one_coords.h <= two_coords.y ||
		one_coords.y >= two_coords.y + two_coords.h ||
		one_coords.x + one_coords.w <= two_coords.x ||
		one_coords.x >= two_coords.x + two_coords.w) {
		return false;
	}

	return true;
}

void CollisionManager::CleanUp() {
	for (size_t i = 0; i < collision_grid_.size(); ++i) {
		for (size_t j = 0; j < collision_grid_[i].size(); ++j) {
			for (auto
				it = collision_grid_[i][j].begin();
				it != collision_grid_[i][j].end();
			++it) {
				it->second->GetCollisionGridPosition()->clear();
			}

			collision_grid_[i][j].clear();
		}
	}
}