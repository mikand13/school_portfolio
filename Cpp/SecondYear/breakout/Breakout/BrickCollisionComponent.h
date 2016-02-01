#pragma once
#ifndef BREAKOUT_COMPONENTS_BRICK_COLLISION_
#define BREAKOUT_COMPONENTS_BRICK_COLLISION_

// local libraries
#include "Component.h"
#include "Brick.h"

/**
 * This header file declares the component used for handling collisions in the Brick GameObject.
 * It is updated every frame and is called by HandleCollision if involved in a collision.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class BrickCollisionComponent :
	public CollisionComponent {
public:
	explicit BrickCollisionComponent(GameObject* const go) :
			CollisionComponent(go), brick_(dynamic_cast<Brick*>(go)) {}
	~BrickCollisionComponent();

	// update every frame
	void Update() override { /* not moving gameobject, no need to check collisions */ };

	// collision mechanics
	void HandleCollision(const GameObject& collidee) override;
protected:
	// cast to owner on creation
	Brick* brick_;

	// handle based on what collided
	void HandleCollisionEffect(const GameObject& collidee);
};

#endif

