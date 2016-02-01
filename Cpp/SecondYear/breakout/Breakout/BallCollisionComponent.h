#pragma once
#ifndef BREAKOUT_COMPONENTS_BALL_COLLISION_
#define BREAKOUT_COMPONENTS_BALL_COLLISION_

// local libraries
#include "Component.h"
#include "Ball.h"

// forward declarations
class Player;
class Brick;

/**
 * This header file declares the component used for handling collisions in the Ball GameObject.
 * It is updated every frame and is called by HandleCollision if involved in a collision.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class BallCollisionComponent :
	public CollisionComponent {
public:
	explicit BallCollisionComponent(GameObject* const go) :
			CollisionComponent(go), ball_(dynamic_cast<Ball*>(go)) {}
	~BallCollisionComponent();
	
	// update every frame
	void Update() override;

	// collision mechanics
	void HandleCollision(const GameObject& collidee) override;
protected:
	// cast to owner on creation
	Ball* ball_;

	// handle player collison
	void HandlePlayerCollision(const Player& collidee);

	// handle brick collision
	void HandleBrickCollision(const Brick& collidee);

	// check for wall collision
	bool CheckForWallCollision();
};

#endif
