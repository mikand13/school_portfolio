#pragma once
#ifndef BREAKOUT_GAME_OBJECT_
#define BREAKOUT_GAME_OBJECT_

// C libraries
#include <SDL.h>

// C++ libraries
#include <vector>
#include <unordered_map>

// local headers
#include "Component.h"

// statics (if I include this in-class it must be const, that is why it is out-of-class)
static int game_object_name_ = 0;

/**
 * This header file declares the base class for all GameObjects. It is designed to be be subclassed
 * for any objects visible to the player.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class GameObject {
public:
	// typedefs
	typedef std::vector<std::unordered_map<int, GameObject*>*> vector_of_maps_gos;
	typedef std::vector<Component*> vector_of_components;

	GameObject();
	virtual ~GameObject();
	
	// getters
	int GetName() const { return instance_name_; }
	SDL_Rect* GetCoords() { return &coords_; }
	SDL_Rect GetCoords() const { return coords_; }

	// component operations
	void AddComponent(Component& component) { components_.push_back(&component); }
	vector_of_components GetComponents() const { return components_; }
	CollisionComponent* GetCollider() const;

	// collision_grid set / get
	void SetCollisionGridPosition(const vector_of_maps_gos& grid_cell) {
		collision_grid_position_ = grid_cell;
	}
	vector_of_maps_gos* GetCollisionGridPosition() { return &collision_grid_position_; }
	vector_of_maps_gos GetCollisionGridPosition() const { return collision_grid_position_; }

	virtual void HandleCollision(const GameObject&) {}
protected:
	vector_of_components components_;
private:
	// id and world position
	int instance_name_;
	SDL_Rect coords_;

	// cell position in collision grid, used for updating only cells of movables
	vector_of_maps_gos collision_grid_position_;

	// equality operators, partly to show i can overload operators, but i also suppose this gives a
	// slightly quicker operation because normal equals calls compare as far as i know.
	friend bool operator==(const GameObject& lhs, const GameObject& rhs) {
		return lhs.instance_name_ == rhs.instance_name_;
	}

	friend bool operator!=(const GameObject& lhs, const GameObject& rhs) {
		return !(lhs == rhs);
	}
};

#endif

