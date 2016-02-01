#pragma once
#ifndef BREAKOUT_COMPONENTS_PLAYER_MOVEMENT_
#define BREAKOUT_COMPONENTS_PLAYER_MOVEMENT_

// local headers
#include "Component.h"

/**
 * This header file declares the component used for handling movement in the Player GameObject.
 * It is updated every frame.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class PlayerMovementComponent :
	public MovementComponent {
public:
	explicit PlayerMovementComponent(GameObject* const go) : MovementComponent(go) {}
	~PlayerMovementComponent();
	
	// update every frame
	void Update() override;
private:
	// move operations
	void MovePlayerLeft(float delta_time);
	void MovePlayerRight(float delta_time);
	void MoveX(float delta_time);
};

#endif

