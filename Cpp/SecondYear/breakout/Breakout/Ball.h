#pragma once
#ifndef BREAKOUT_BALL_
#define BREAKOUT_BALL_

// local headers
#include "GameObject.h"

// forward declarations
class BallMovementComponent;

/**
 * This header file declares Ball subclass of GameObject.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class Ball :
	public GameObject {
public:
	Ball();
	~Ball();

	SDL_Surface* GetSurface() const { return ball_surface_; }

	// initialization/reset
	void InitializeBall();

	// initiate game
	void ActivateBall();
private:
	// gives components access to class
	friend class BallMovementComponent;
	friend class BallCollisionComponent;

	// ball stuck to player or not
	bool active;

	// needed by SDLvideo
	SDL_Surface* ball_surface_;

	// velocityratio in direction
	// velocity is controlled by level in game
	float ballY_;
	float ballX_;

	// collisions
	bool hit_player_; // prevents "move into the ball" collisions
};

#endif

