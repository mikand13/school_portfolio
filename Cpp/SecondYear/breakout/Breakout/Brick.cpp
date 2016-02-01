#include "Brick.h"

// local headers
#include "Debug.h"

Brick::~Brick() {
	DEBUG_DESTRUCTOR("Brick being destroyed..." << GetName());
}

Brick::Brick(BrickFlyWeight brick_fly_weight_) : 
	surface_container_(brick_fly_weight_) {
	GetCoords()->h = GetBrickFlyWeight()->GetBrickSurface()->h;
	GetCoords()->w = GetBrickFlyWeight()->GetBrickSurface()->w;
}


