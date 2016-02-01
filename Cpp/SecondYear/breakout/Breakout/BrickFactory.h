#pragma once
#ifndef BREAKOUT_BRICK_FACTORY_
#define BREAKOUT_BRICK_FACTORY_

// C++ libraries
#include <unordered_map>
#include <memory>

// local headers
#include "BrickFlyWeight.h"

// forward declarations
class Brick;

/**
 * Standard factory pattern for producing bricks with flyweights, tightly coupled
 * with LevelManager and SDLvideo for producing flyweight textures.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class BrickFactory {
public:
	// typedefs
	typedef std::shared_ptr<Brick> shared_ptr_brick;

	BrickFactory() {}
	~BrickFactory();

	// returns a new brick with approriate Flywieght
	// creates new flyweight if none availible
	shared_ptr_brick GetBrick(BrickFlyWeight::EBrickType const brick_type);

	// cleaners
	void FreeAllSurfaces();
	void CleanUp();
private:
	// typedefs
	typedef std::unordered_map<BrickFlyWeight::EBrickType, BrickFlyWeight> map_of_flyweights;

	// contains all produced brickflyweights
	map_of_flyweights brick_map_;
};

#endif