#pragma once
#ifndef BREAKOUT_COMPONENTS_
#define BREAKOUT_COMPONENTS_

// forward declarations
class GameObject;

/**
 * This header file declares the different abstract and concrete interfaces I use for components.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class Updateable {
public:
	virtual ~Updateable() {}
	virtual void Update() = 0;
};

class Component : 
	public Updateable {
public:
	explicit Component(GameObject* const go) : owner(go) {}
	virtual ~Component() {}

	GameObject* GetOwner() { return owner; }
protected:
	GameObject* owner;
};

class MovementComponent : 
	public Component {
public:
	explicit MovementComponent(GameObject* const go) : Component(go) {}
	virtual ~MovementComponent() {}

	virtual void Update() override {}
};

class CollisionComponent :
	public Component {
public:
	explicit CollisionComponent(GameObject* const go) : Component(go) {}
	virtual ~CollisionComponent() {}

	virtual void Update() override {}
	virtual void HandleCollision(const GameObject&) {}
};

#endif