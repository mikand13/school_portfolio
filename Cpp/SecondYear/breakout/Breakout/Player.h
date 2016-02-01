#pragma once
#ifndef BREAKOUT_PLAYER_
#define BREAKOUT_PLAYER_

// C libraries
#include <SDL.h>

// local headers
#include "GameObject.h"

/**
 * This header file declares the Player subclass of GameObject.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class Player :
	public GameObject {
public:
	Player();
	~Player();

	// getters
	SDL_Surface* GetSurface() const { return player_surface_; }
private:
	SDL_Surface* player_surface_;
};

#endif

