#include "PlayerCollisionComponent.h"

// local headers
#include "Debug.h"
#include "GameManager.h"
#include "GameObject.h"

PlayerCollisionComponent::~PlayerCollisionComponent() {
	DEBUG_DESTRUCTOR("PlayerCollisionComponent being destroyed..." << GetOwner()->GetName());
}

void PlayerCollisionComponent::Update() {
	if (GameManager::Instance().GetGameState() == RUNNING) {
		GameManager::Instance().CheckForCollisions(*this, *GetOwner());
	}
}

void PlayerCollisionComponent::HandleCollision(const GameObject& collidee) {
	DEBUG("Player " << GetOwner()->GetName() << " collided with " << collidee.GetName());
}
