/*
  This source file defines all the functions i use to control the game loop
 */

void checkRespawn(int respawn, GameState* gameState, int* score, int* level);
void checkScore(int* standardSpeed, int* level, int* score);
int checkForFullRows(int* score);
int checkUserInput(int *gameSpeed, int* standardSpeed,
        GameState* gameState,
        int* level, int* score, clock_t* before);

// continually checks input and acts accordingly
int checkUserInput(int *gameSpeed, int* standardSpeed,
        GameState* gameState,
        int* level, int* score, clock_t* before) {
  int c;
  int respawn = 0;

  if ((c = wgetch(stdscr)) != ERR) {
    {
      switch (c) {
        case KEY_LEFT:
          moveBrick(Left, score);
          return 1;
        case KEY_RIGHT:
          moveBrick(Right, score);
          return 1;
        case KEY_DOWN:
          *gameSpeed = 70;
          return 1;
        case KEY_UP:
          rotateBrick();
          return 1;
        case 32:
          // Hard Drop
          while (respawn == 0) { respawn = moveBrick(Normal, score); }
          updateBucketWindow();
          updateScoreWindow(score, level);

          checkRespawn(respawn, gameState, score, level);
          checkScore(standardSpeed, level, score);

          // resets clock for next brick, "resets" "fps counter"
          *before = clock();
          return 0;
        case 27:
          *gameState = GameOver;
          return 0;
        default:
          return 0;
      }
    }
  }
  return 0;
}

void checkRespawn(int respawn, GameState* gameState, int* score, int* level) {
  if (respawn == 1) {
    soundPlayer(landSound);

    spawnNewBrick(*gameState);
    updateNextBrickWindow();

    if (checkForFullRows(score)) {
      updateScoreWindow(score, level);
    }
  } else if (respawn == 2 || respawn == 11) {
    soundPlayer(landSound);

    updateScoreWindow(score, level);
    *gameState = GameOver;
  }
}

void checkScore(int* standardSpeed, int* level, int* score) {
  if (*score >= (*level * 1000)) {
    soundPlayer(levelUpSound);

    *level += 1;

    if (*standardSpeed > 200) {
      *standardSpeed = 1000 - (100 * (*level));
    }
    updateScoreWindow(score, level);
  }
}

// checks row by row if full, adds points and rebuilds bucket on full
int checkForFullRows(int* score) {
  int scoreRows = 0, scoreAccumulated = 0;
  int i, j, k, l, fullRow = 1;

  for (i = 1; i < tetrisBucket.height; i++) {
    for (j = 0; j < tetrisBucket.width; j++) {
      if (tetrisBucket.bucket[i][j].value != '#') {
        fullRow = 0;
      }
    }

    if (fullRow) {
      scoreRows++;
      *score += 100 * scoreRows;
      scoreAccumulated = 1;

      int brickRemnantOnLine = 0;
      for (k = i; k > 0; k--) {
        for (l = 0; l < tetrisBucket.width; l++) {
          tetrisBucket.bucket[k][l] = tetrisBucket.bucket[k - 1][l];
          if (tetrisBucket.bucket[k][l].value == '#') {
            brickRemnantOnLine = 1;
          }
        }
        if (brickRemnantOnLine == 0) {
          break;
        }
      }
    } else {
      fullRow = 1;
    }
  }

  if (scoreRows > 0 && scoreRows < 4) {
    updateBucketWindow();
    soundPlayer(clearLineSound);
  } else if (scoreRows == 4) {
    updateBucketWindow();
    soundPlayer(tetrisSound);
  }
  return scoreAccumulated;
}