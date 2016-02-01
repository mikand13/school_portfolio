#pragma once
#ifndef BREAKOUT_COMPONENTS_BALL_MOVEMENT_
#define BREAKOUT_COMPONENTS_BALL_MOVEMENT_

// local libraries
#include "Component.h"
#include "Ball.h"

/**
 * This header file declares the component used for handling movement in the Ball GameObject.
 * It is updated every frame.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class BallMovementComponent :
	public MovementComponent {
public:
	explicit BallMovementComponent(GameObject* const go) :
			MovementComponent(go), ball_(dynamic_cast<Ball*>(go)) {}
	~BallMovementComponent();

	// update every frame
	void Update() override;
private:
	// cast to owner on creation
	Ball* ball_;
};

#endif
