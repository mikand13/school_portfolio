#pragma once
#ifndef BREAKOUT_COMPONENTS_PLAYER_COLLISION_
#define BREAKOUT_COMPONENTS_PLAYER_COLLISION_

// local headers
#include "Component.h"
#include "Player.h"

/**
 * This header file declares the component used for handling collisions in the Player GameObject.
 * It is updated every frame and is called by HandleCollision if involved in a collision.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class PlayerCollisionComponent :
	public CollisionComponent {
public:
	explicit PlayerCollisionComponent(GameObject* const go) :
			CollisionComponent(go), player_(dynamic_cast<Player*>(go)) {}
	~PlayerCollisionComponent();

	// update every frame
	void Update() override;

	// collision mechanics
	void HandleCollision(const GameObject& collidee) override;
protected:
	// cast to owner on creation
	Player* player_;
};

#endif

