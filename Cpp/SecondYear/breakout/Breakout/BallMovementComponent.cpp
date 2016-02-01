#include "BallMovementComponent.h"

// local headers
#include "Debug.h"
#include "GameManager.h"
#include "Player.h"

BallMovementComponent::~BallMovementComponent() {
	DEBUG_DESTRUCTOR("BallMovementComponent being destroyed..." << GetOwner()->GetName());
}

void BallMovementComponent::Update() {
	auto owner_coords = GetOwner()->GetCoords();

	auto player = GameManager::Instance().GetPlayer();
	auto player_coords = player->GetCoords();
	auto game_state = GameManager::Instance().GetGameState();
	auto level_velocity = GameManager::Instance().GetLevelVelocity();
	auto delta_time = GameManager::Instance().GetDeltaTime();

	if (!ball_->active) {
		// float on player if reset
		auto calculated_center_of_player =
			player_coords.x +
			player_coords.w / 2 -
			owner_coords->w / 2;;

		owner_coords->x = calculated_center_of_player;
		owner_coords->y = player_coords.y - owner_coords->h;
	} else if (game_state == RUNNING) {
		// move according to level velocity and deltatime / ballX / ballY

		owner_coords->y += static_cast<int>(level_velocity * ball_->ballY_ * delta_time);
		owner_coords->x += static_cast<int>(level_velocity * ball_->ballX_ * delta_time);

		DEBUG("Ball Y: " << owner_coords->y << " Ball X: " << owner_coords->x);

		GameManager::Instance().GameObjectMoved(*ball_);
	}
}