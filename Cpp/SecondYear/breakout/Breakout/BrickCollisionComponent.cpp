#include "BrickCollisionComponent.h"

// local headers
#include "Debug.h"
#include "GameManager.h"
#include "Ball.h"

BrickCollisionComponent::~BrickCollisionComponent() {
	DEBUG_DESTRUCTOR("BrickCollisionComponent being destroyed..." << GetOwner()->GetName());
}

void BrickCollisionComponent::HandleCollision(const GameObject& collidee) {
	auto owner_coords = brick_->GetCoords();
	auto collidee_coords = collidee.GetCoords();

	auto x_collidee_center = collidee_coords.x + collidee_coords.w / 2;
	auto y_collidee_center = collidee_coords.y + collidee_coords.h / 2;

	auto owner_right = owner_coords->x + owner_coords->w;
	auto owner_bottom = owner_coords->y + owner_coords->h;

	// does nothing if collider is not in direct contact with an edge.
	if (x_collidee_center < owner_right &&
		x_collidee_center > owner_coords->x) {
		HandleCollisionEffect(collidee);
	} else if (y_collidee_center < owner_bottom &&
		y_collidee_center > owner_coords->y) {
		HandleCollisionEffect(collidee);
	}
}

void BrickCollisionComponent::HandleCollisionEffect(const GameObject& collidee) {
	if (dynamic_cast<Ball*>(const_cast<GameObject*>(&collidee))) {
		DEBUG("Brick " << brick_->GetName() << " collided with ball " << collidee.GetName());

		GameManager::Instance().KillGameObject(*brick_);
	}
}
