#include "BallCollisionComponent.h"

// local headers
#include "Debug.h"
#include "GameManager.h"
#include "Player.h"
#include "Brick.h"

BallCollisionComponent::~BallCollisionComponent() {
	DEBUG_DESTRUCTOR("BallCollisionComponent being destroyed..." << GetOwner()->GetName());
}

void BallCollisionComponent::Update() {
	if (GameManager::Instance().GetGameState() == RUNNING) {
		if (!CheckForWallCollision()) {
			GameManager::Instance().CheckForCollisions(*this, *GetOwner());
		}
	}
}

inline bool BallCollisionComponent::CheckForWallCollision() {
	auto owner_coords = GetOwner()->GetCoords();
	auto wall_collision = false;

	// right wall
	if (owner_coords->x > SDLvideo::GetScreenWidth() - owner_coords->w) {
		ball_->ballX_ = -1.f;
		wall_collision = true;
		// ceiling
	} else if (owner_coords->x <= 0) {
		ball_->ballX_ = 1.f;
		wall_collision = true;
		// left wall
	} else if (owner_coords->y <= SDLvideo::GetGUIArea() && ball_->ballY_ < 0.f) {
		ball_->ballY_ *= -1.0f;
		wall_collision = true;
		// floor (death)
	} else if (owner_coords->y > SDLvideo::GetScreenHeight()) {
		ball_->InitializeBall();
		GameManager::Instance().BallDeath();
		wall_collision = true;
	}

	if (wall_collision) {
		ball_->hit_player_ = false;

		return true;
	}

	return false;
}

void BallCollisionComponent::HandleCollision(const GameObject& collidee) {
	if (auto player = dynamic_cast<const Player*>(&collidee)) {
		if (!ball_->hit_player_) {
			HandlePlayerCollision(*player);

			ball_->hit_player_ = true;
		}
	} else if (auto brick = dynamic_cast<const Brick*>(&collidee)) {
		HandleBrickCollision(*brick);

		ball_->hit_player_ = false;
	}
}

inline void BallCollisionComponent::HandlePlayerCollision(const Player& collidee) {
	DEBUG("Ball " << ball_->GetName() << " collided with player " << collidee.GetName());

	GameManager::Instance().PlayerHitSound();

	auto ball_coords = ball_->GetCoords();
	auto collidee_coords = collidee.GetCoords();

	auto one_third_of_bar = collidee_coords.w / 3;
	auto one_eights_of_bar = collidee_coords.w / 8;
	auto center_of_ball = ball_coords->x + ball_coords->w / 2;

	auto ball_displacement = center_of_ball - collidee_coords.x;
	auto percentage_of_bar = static_cast<float>(ball_displacement) /
		static_cast<float>(collidee_coords.w);

	DEBUG("%: " << percentage_of_bar);

	// calculate collision point on player bar
	if (percentage_of_bar <= 0.5) {
		ball_->ballY_ = 0.5f + percentage_of_bar;
	} else {
		ball_->ballY_ = 1.0f - (percentage_of_bar - 0.5f);
	}

	if (center_of_ball <= collidee_coords.x + one_third_of_bar) {
		DEBUG("Hit left side of player...");

		ball_->ballX_ = -1.f;
	} else if (center_of_ball >= collidee_coords.x + one_third_of_bar * 2) {
		DEBUG("Hit right side of player...");

		ball_->ballX_ = 1.f;
	} else {
		DEBUG("Hit center of player...");
	}

	// snap move
	if (center_of_ball <= collidee_coords.x + one_eights_of_bar ||
		center_of_ball >= collidee_coords.x + one_eights_of_bar * 7 ||
		ball_->ballY_ < 0.4f) {
		DEBUG("Player performed snapmove...");

		ball_->ballY_ = 0.4f;
	}

	ball_->ballY_ *= -1.f;

	DEBUG("BallY: " << ball_->ballY_ << " BallX: " << ball_->ballX_);
}

inline void BallCollisionComponent::HandleBrickCollision(const Brick& collidee) {
	DEBUG("Ball " << ball_->GetName() << " collided with brick " << collidee.GetName());
	
	auto ball_coords = ball_->GetCoords();
	auto collidee_coords = collidee.GetCoords();

	auto x_ball_center = ball_coords->x + ball_coords->w / 2;
	auto y_ball_center = ball_coords->y + ball_coords->h / 2;

	auto x_right_collidee = collidee_coords.x + collidee_coords.w;
	auto y_bottom_collidee = collidee_coords.y + collidee_coords.h;

	// Precision collision detection for correct behavious on bricks
	// does nothing if collider is not in direct contact with an edge.
	// Added a precision "hack" that checks current direction of ball.
	if (x_ball_center < x_right_collidee && x_ball_center > collidee_coords.x) {
		// long edge
		if (ball_coords->y + ball_coords->h > y_bottom_collidee && ball_->ballY_ < 0 ||
			ball_coords->y < collidee_coords.y && ball_->ballY_ > 0) {
			ball_->ballY_ *= -1.f;
		}
	} else if (y_ball_center < y_bottom_collidee && y_ball_center > collidee_coords.y) {
		// short edge
		if (ball_coords->x + ball_coords->w > x_right_collidee && ball_->ballX_ < 0 || 
			ball_coords->x < collidee_coords.x && ball_->ballX_ > 0) {
			ball_->ballX_ *= -1.f;
		}
	}

	DEBUG("BallY: " << ball_->ballY_ << " BallX: " << ball_->ballX_);
}
