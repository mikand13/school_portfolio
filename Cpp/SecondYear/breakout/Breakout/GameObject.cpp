#include "GameObject.h"

// local headers
#include "Debug.h"

GameObject::GameObject() {
	instance_name_ = game_object_name_++;
}

GameObject::~GameObject() {
	DEBUG_DESTRUCTOR("GameObject has been destroyed...");
}

CollisionComponent* GameObject::GetCollider() const {
	for (auto comp : components_) {
		if (auto collider = dynamic_cast<CollisionComponent*>(comp)) {
			return collider;
		}
	}

	return nullptr;
}
