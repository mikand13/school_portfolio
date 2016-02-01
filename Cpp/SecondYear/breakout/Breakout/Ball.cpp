#include "Ball.h"

// C libraries
#include <SDL_image.h>

// C++ libraries
#include <ctime>

// local headers
#include "Debug.h"
#include "GameManager.h"
#include "Player.h" // needed in activateball

Ball::Ball() {
	ball_surface_ = IMG_Load("..//Resources//images//player_ball.png");

	srand(static_cast<unsigned int>(time(nullptr)));

	GetCoords()->h = ball_surface_->h;
	GetCoords()->w = ball_surface_->w;

	InitializeBall();
}

Ball::~Ball() {
	DEBUG_DESTRUCTOR("Ball being destroyed..." << GetName());
}

inline void Ball::InitializeBall() {
	active = false;
	hit_player_ = true;

	ballY_ = -1.f;
	ballX_ = static_cast<float>(rand() % 2);

	if (ballX_ == 0.f) {
		ballX_ = -1.f;
	} else {
		ballX_ = 1.f;
	}
}

void Ball::ActivateBall() {
	auto player_coords = GameManager::Instance().GetPlayer()->GetCoords();

	auto calculated_center_of_player =
		player_coords.x +
		player_coords.w / 2 -
		GetCoords()->w / 2;

	if (calculated_center_of_player == GetCoords()->x) {
		active = true;
	}
}