#include "BrickFactory.h"

// C libraries
#include <SDL.h>

// C++ libraries
#include <memory>
#include <thread>

// local headers
#include "Debug.h"
#include "Brick.h"

BrickFactory::~BrickFactory() {
	DEBUG_DESTRUCTOR("BrickFactory has been destroyed...");
}

std::shared_ptr<Brick> BrickFactory::GetBrick(BrickFlyWeight::EBrickType const brick_type) {
	if (brick_map_.count(brick_type)) {
		DEBUG("Returning already existing flyweight");
		try {
			return std::make_shared<Brick>(brick_map_[brick_type]);
		} catch (std::bad_alloc ba) {
			DEBUG("BrickFactory could not create smart pointer: " << ba.what());

			return nullptr;
		}
	}

	SDL_Surface* brick_surface;

	switch (brick_type) {
	case BrickFlyWeight::RED:
		brick_surface = SDL_LoadBMP("..//Resources//images//red_brick.bmp");
		break;
	case BrickFlyWeight::BLUE:
		brick_surface = SDL_LoadBMP("..//Resources//images//blue_brick.bmp");
		break;
	case BrickFlyWeight::ORANGE: 
		brick_surface = SDL_LoadBMP("..//Resources//images//orange_brick.bmp");
		break;
	case BrickFlyWeight::YELLOW: 
		brick_surface = SDL_LoadBMP("..//Resources//images//yellow_brick.bmp");
		break;
	case BrickFlyWeight::BROWN: 
		brick_surface = SDL_LoadBMP("..//Resources//images//brown_brick.bmp");
		break;
	case BrickFlyWeight::GREEN: 
		brick_surface = SDL_LoadBMP("..//Resources//images//green_brick.bmp");
		break;
	default: 
		return nullptr;
	}

	DEBUG("Creating new Flyweight");

	brick_map_[brick_type] = BrickFlyWeight(brick_surface, brick_type);

	DEBUG("Returning new Brick");

	auto brick = std::make_shared<Brick>(brick_map_[brick_type]);
	auto brick_coords = brick->GetCoords();

	brick_coords->h = brick_surface->h;
	brick_coords->w = brick_surface->w;

	return brick;
}

void BrickFactory::CleanUp() {
	brick_map_.clear();
}

void BrickFactory::FreeAllSurfaces() {
	for (auto it = brick_map_.begin(); it != brick_map_.end(); ++it) {
		// safe to const cast as surface is not changed
		// SDL_FreeSurface is not const enabled
		// Nothing is relying on these anymore so they can be freed concurrently
		std::thread free_thread(
				SDL_FreeSurface, const_cast<SDL_Surface*>(it->second.GetBrickSurface()));
		free_thread.detach();
	}
}