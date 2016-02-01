#include "Collision.h"

// local headers
#include "Debug.h"
#include "GameObject.h"

Collision::~Collision() {
	DEBUG_DESTRUCTOR(
			"Collision being destroyed..." << GetOne()->GetName() << " " << GetTwo()->GetName());
}

Collision::Collision(GameObject const& one, GameObject const& two) :
	one_(&one),
	two_(&two) {}
