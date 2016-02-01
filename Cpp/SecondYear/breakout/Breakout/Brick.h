#pragma once
#ifndef BREAKOUT_BRICK_
#define BREAKOUT_BRICK_

// local libraries
#include "GameObject.h"
#include "BrickFlyWeight.h"

/**
 * This header file declares the Brick subclass of GameObject.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class Brick :
	public GameObject {
public:
	explicit Brick(BrickFlyWeight brick_fly_weight_);
	~Brick();

	// getters
	// made this return with pointer, its a flyweight, so it should avoid copy operations which can
	// be memoryintensive
	const BrickFlyWeight* GetBrickFlyWeight() const { return &surface_container_; }
private:
	// gives components access to class
	friend class BrickCollisionComponent;

	BrickFlyWeight surface_container_;
};

#endif