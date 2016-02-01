#include "Player.h"

// C libraries
#include <SDL_image.h>

// local headers
#include "Debug.h"

Player::~Player() {
	DEBUG_DESTRUCTOR("Player being destroyed..." << GetName());
}

Player::Player() {
	player_surface_ = IMG_Load("..//Resources//images//player_bar.png");

	GetCoords()->h = player_surface_->h;
	GetCoords()->w = player_surface_->w;
}
