#pragma once
#ifndef BREAKOUT_LEVELMANAGER_GAMEOBJECT_SPAWNER_
#define BREAKOUT_LEVELMANAGER_GAMEOBJECT_SPAWNER_

/**
 * This header file declares my generic GameObject spawner. It is mainly to abstract away the
 * ugly initilization of the object, and to should I know the general idea of how templates
 * are meant to work, although this is an extremely simple template.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
template <class T>
class GameObjectSpawner {
public:
	GameObjectSpawner() {}
	~GameObjectSpawner() {}

	std::shared_ptr<T> Spawn() { return std::move(std::make_shared<T>(T())); }
};

#endif
