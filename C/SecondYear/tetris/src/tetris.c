#include "globals.c"
#include "utils.c"
#include "soundAndMusic.c"
#include "brick.c"
#include "output.c"
#include "factory.c"
#include "gameloop.c"

/*
  This source file defines the main execution thread of my tetris game.
  This is the actual gameloop.
 */

int main(int argc, char* argv[]) {
  GameState gameState = GameInit;
  int level = 0;
  const int maxLevel = 10;
  int score = 0;
  int standardSpeed = 0;
  int gameSpeed = 0;

  // SDL init
  initializeMusic();

  // Init of shared thread for music
  pthread_t musicThread = NULL;

  // ncurses init
  initializeNcurses();

  while (gameState == GameInit) {
    // init game
    musicPlayer(&musicThread, introMusic);

    showStartMenu(&musicThread, &gameState);

    initializeNewGame(width, height, &level, &score);

    showGame(&score, &level);
    verifyRefresh();

    standardSpeed = 1100 - (100 * level);
    gameSpeed = standardSpeed;

    musicPlayer(&musicThread, korobeiniki);
    clock_t before = clock();

    if (gameState != GameOver) {
      // gameloop
      while (gameState == GameRunning) {
        if (checkUserInput(&gameSpeed, &standardSpeed,
                &gameState,
                &level, &score, &before)) {
          updateBrick();
          verifyRefresh();
        }

        clock_t current = clock();
        // updates "fps" based on level, starts at 900ms.
        if (((current - before) / 1000) > gameSpeed) {
          int respawn = moveBrick(Normal, &score);

          if (respawn != 0) {
            // handle collisionEvent
            checkRespawn(respawn, &gameState, &score, &level);

            //check score
            checkScore(&standardSpeed, &level, &score);

            updateScoreWindow(&score, &level);
            updateBucketWindow();
          } else {
            updateBrick();
          }

          if (level > maxLevel) {
            gameState = GameOver;
          }

          // reset updateTimer
          before = current;
        }

        verifyRefresh();
        // reset in case of DOWN speed
        gameSpeed = standardSpeed;
      }

      if (gameState == GameOver) {
        musicPlayer(&musicThread, gameOverMusic);

        showGameOver(&gameState, &score, &level, &maxLevel);
      }
    }
  }

  cleanUp(musicThread);
  return 0;
}