#include "BrickFlyWeight.h"

// local headers
#include "Debug.h"

BrickFlyWeight::BrickFlyWeight(SDL_Surface* const brick_bmp, EBrickType const brick_type) : 
	brick_surface_(brick_bmp),
	brick_type_(brick_type) {}

BrickFlyWeight::~BrickFlyWeight() {
	DEBUG_DESTRUCTOR("BrickFlyWeight being destroyed..." << brick_type_);
}
