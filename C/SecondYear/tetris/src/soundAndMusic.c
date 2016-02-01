/*
  This source file contains the functions i pass to threads.
  I don't check return values here because a lack of sound doesnt break the game
 */

// loops game over music
void (*playMusic(void* arg)) {
  Mix_PlayMusic(arg, -1);
  return 0;
}

void (*playSound(void *arg)) {
  Mix_PlayChannel(-1, arg, 0);
  return 0;
}