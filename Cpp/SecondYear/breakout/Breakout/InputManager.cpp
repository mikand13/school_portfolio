#include "InputManager.h"

// local headers
#include "Debug.h"

InputManager::InputManager() {
	int numKeys;
	current_keys_ = SDL_GetKeyboardState(&numKeys);
	old_keys_ = std::unique_ptr<Uint8>(new Uint8[numKeys]);
}

InputManager::~InputManager() {
	DEBUG_DESTRUCTOR("InputManager has been destroyed...");
}

void InputManager::Update() {
	memcpy(old_keys_.get(), current_keys_, key_count_ * sizeof(Uint8));

	SDL_PumpEvents();
}

void InputManager::Clear() {
	SDL_Delay(500); // hack for not jumping straight to init or straight to exit
}
