#pragma once
#ifndef BREAKOUT_INPUT_MANAGER_
#define BREAKOUT_INPUT_MANAGER_

// C libraries
#include <SDL.h>

// C++ libraries
#include <memory>

/**
 * This header file declares the InputManager. It is a trimmed down and slightly optimized version
 * that has been converted to smart pointers.
 *
 * @author Anders Mikkelsen
 * @version 1.0
 * @date 13.04.2015
 */
class InputManager {
public:
	InputManager();
	~InputManager();

	// updates key state and pumps events
	void Update();

	// halts inputmanager for 500 millis, hack to avoid skiping menus
	void Clear();

	// checks status of individual keycodes
	bool KeyDown(int key) { return current_keys_[key] && !old_keys_.get()[key]; };
	bool KeyStillDown(int key) { return current_keys_[key] && old_keys_.get()[key]; };
	bool KeyUp(int key) { return !current_keys_[key] && old_keys_.get()[key]; };
	bool KeyStillUp(int key) { return !current_keys_[key] && !old_keys_.get()[key]; };
private:
	// variables for holding keys
	int key_count_;
	const Uint8* current_keys_;
	std::unique_ptr<Uint8> old_keys_;
};

#endif

