#pragma once
#ifndef BREAKOUT_BRICK_FLYWEIGHT_
#define BREAKOUT_BRICK_FLYWEIGHT_

// C libraries
#include <SDL.h>

// enumerations
enum EBrickType;

/**
 * This header file declares a simple flyweight for different coloured bricks.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class BrickFlyWeight {
public:
	// enumerations
	enum EBrickType { RED, ORANGE, YELLOW, BROWN, BLUE, GREEN };

	BrickFlyWeight() {}
	BrickFlyWeight(SDL_Surface* const brick_bmp, EBrickType const brick_type);
	~BrickFlyWeight();

	// getters
	const SDL_Surface* GetBrickSurface() const { return brick_surface_; }
	EBrickType GetBrickType() const { return brick_type_; }
private:
	const SDL_Surface* brick_surface_;
	EBrickType brick_type_;
};

#endif
