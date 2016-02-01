// local headers
#include "Debug.h"
#include "GameManager.h"

int main(int, char* []) {
	GameManager::Instance().RunGameLoop();

	DEBUG("Exiting...");
	return 0;
}
