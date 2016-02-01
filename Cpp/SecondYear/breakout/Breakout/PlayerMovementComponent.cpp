#include "PlayerMovementComponent.h"

// local headers
#include "Debug.h"
#include "GameManager.h"
#include "GameObject.h"
#include "SDLvideo.h"

PlayerMovementComponent::~PlayerMovementComponent() {
	DEBUG_DESTRUCTOR("PlayerMovementComponent being destroyed..." << GetOwner()->GetName());
}

void PlayerMovementComponent::Update() {
	auto player_movement = GameManager::Instance().CheckPlayerMovement();
	auto delta_time = GameManager::Instance().GetDeltaTime();

	if (player_movement != 0) {
		switch (player_movement) {
		case SDL_SCANCODE_LEFT:
			MovePlayerLeft(delta_time);
			break;
		case SDL_SCANCODE_RIGHT:
			MovePlayerRight(delta_time);
			break;
		}
		
		GameManager::Instance().GameObjectMoved(*GetOwner());
	}
}

inline void PlayerMovementComponent::MovePlayerLeft(float delta_time) {
	auto owner_coords = GetOwner()->GetCoords();

	if (owner_coords->x > 0) {
		MoveX(-delta_time);
	} else {
		owner_coords->x = 0;
	}
}

inline void PlayerMovementComponent::MovePlayerRight(float delta_time) {
	auto owner_coords = GetOwner()->GetCoords();

	if (owner_coords->x < SDLvideo::GetScreenWidth() - owner_coords->w) {
		MoveX(delta_time);
	}
}

inline void PlayerMovementComponent::MoveX(float delta_time) {
	auto owner_coords = GetOwner()->GetCoords();

	owner_coords->x += static_cast<int>(0.75f * delta_time);

	// reset to min/max if overshot
	if (delta_time > 0 && owner_coords->x > SDLvideo::GetScreenWidth() - owner_coords->w) {
		owner_coords->x = SDLvideo::GetScreenWidth() - owner_coords->w;
	} else if (GetOwner()->GetCoords()->x < 0) {
		owner_coords->x = 0;
	}
}
