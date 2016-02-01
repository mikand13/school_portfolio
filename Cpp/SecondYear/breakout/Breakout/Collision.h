#pragma once
#ifndef BREAKOUT_COLLISION_
#define BREAKOUT_COLLISION_

// forward declarations
class GameObject;

/**
 * This header file declares the Collision VO. It holds a pointer to
 * two GameObject's in a collision scenario.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class Collision {
public:
	Collision(GameObject const& one, GameObject const& two);
	~Collision();

	// getters
	const GameObject* GetOne() const { return one_; }
	const GameObject* GetTwo() const { return two_; }
private:
	const GameObject* one_;
	const GameObject* two_;
};

#endif

