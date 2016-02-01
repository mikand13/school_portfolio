/*
  This source file defines all functions i need for outputing to screen
  and updating it.
 */

void showStartMenu(pthread_t* musicThread, GameState* gameState);
void printTetrisBucket();
void printScoreWindow(int* score, int* level);
void printNextBrickWindow();
void showGame(int* score, int* level);
void updateScoreWindow(int* score, int* level);
void updateBrick();
void updateBucketWindow();
void updateNextBrickWindow();
void printGameOverLost(int* score);
void printGameOverWon(int* score);
void printGameMenu();
void showGameOver(GameState* gameState, int* score, int* level,
        const int* maxLevel);

/*
  In this source file I verify all ncurses calls except wattr and wprintw/printw
  because wattr only changes colors which isnt critical and wprintw/printw is
  called after other functions that would break first.
 */

// prints the static canvas and the floating brick dynamically
void printTetrisBucket() {
  int i, j, k, l, m, open = 1;

  verifyWClear(bucketWin);
  wprintw(bucketWin, "\n");

  for (i = 0; i < tetrisBucket.height; i++) {
    wprintw(bucketWin, "<!");
    for (j = 0; j < tetrisBucket.width; j++) {
      int valid = 0;

      for (m = 0; m < currentbrick->size; m++) {
        if (currentbrick->visualRepresentation[m][0].y == i) {
          valid = 1;
        }
      }

      if (valid) {
        for (k = 0; k < currentbrick->size; k++) {
          if (currentbrick->visualRepresentation[k][0].y == i) {
            for (l = 0; l < currentbrick->size; l++) {
              if (currentbrick->visualRepresentation[k][l].x == j &&
                      currentbrick->visualRepresentation[k][l].value == 1) {
                wattr_on(bucketWin, currentbrick->color, NULL);
                wprintw(bucketWin, "%c", '#');
                wattr_on(bucketWin, COLOR_PAIR(1), NULL);
                open = 0;
              }
            }
          }
        }
      }

      if (open) {
        wattr_on(bucketWin, tetrisBucket.bucket[i][j].color, NULL);
        wprintw(bucketWin, "%c", tetrisBucket.bucket[i][j]);
        wattr_on(bucketWin, COLOR_PAIR(1), NULL);
      } else {
        open = 1;
      }
    }
    wprintw(bucketWin, "!>\n");
  }

  wprintw(bucketWin, "<!");

  for (i = 0; i < tetrisBucket.width; i++) {
    wprintw(bucketWin, "=");
  }
  wprintw(bucketWin, "!>\n  ");

  for (i = 0; i < tetrisBucket.width; i++) {
    wprintw(bucketWin, "^");
  }
  wprintw(bucketWin, "\n");

  verifyWRefresh(bucketWin);
}

// displays score and commands left of bucket
void printScoreWindow(int* score, int* level) {
  verifyWClear(scoreWin);
  wprintw(scoreWin, "  %s%d\n", "Level: ", *level);
  wprintw(scoreWin, "\n");
  wprintw(scoreWin, "+ %s +\n", "-------------");
  wprintw(scoreWin, "| %s |\n", "             ");
  wprintw(scoreWin, "| %s%d \t|\n", "Score:  ", *score);
  wprintw(scoreWin, "| %s |\n", "             ");
  wprintw(scoreWin, "+ %s +\n\n\n\n", "-------------");
  wprintw(scoreWin, "%s\n", "Escape:     Quit");
  wprintw(scoreWin, "%s\n", "Sides:      Move");
  wprintw(scoreWin, "%s\n", "Up:         Rotate");
  wprintw(scoreWin, "%s\n", "Down:       Soft Drop");
  wprintw(scoreWin, "%s\n", "Space:      Hard Drop");
  verifyWRefresh(scoreWin);
}

// displays the upcoming brick right of the bucket
void printNextBrickWindow() {
  verifyWClear(nextBrickWin);
  int i, j;

  wprintw(nextBrickWin, "%s\n\n", " Next Brick:");
  wprintw(nextBrickWin, "+%s+\n", " --------- ");

  for (i = 0; i < nextBrick->size; i++) {
    wprintw(nextBrickWin, "|%s", "   ");
    for (j = 0; j < nextBrick->size; j++) {
      if (nextBrick->visualRepresentation[i][j].value == 1) {
        wattr_on(nextBrickWin, nextBrick->color, NULL);
        wprintw(nextBrickWin, "%c", '#');
        wattr_on(nextBrickWin, COLOR_PAIR(1), NULL);
      } else {
        wprintw(nextBrickWin, "%c", ' ');
      }
    }
    wprintw(nextBrickWin, "%s|\n", "   ");
  }
  wprintw(nextBrickWin, "+%s+\n", " --------- ");

  verifyWRefresh(nextBrickWin);
}

// three simple printmethods, no windowsinitialization necessary
void printGameOverLost(int* score) {
  printw("\n\n\n\n\n\n\n\n");
  printw("\t\t\t  Game Over! You Lost!\n");
  printw("\n");
  printw("\t\t\t       Score: %d\n", *score);
  printw("\n");
  printw("\t\t\t    Main Menu? (y/n)\n");
}

void printGameOverWon(int* score) {
  printw("\n\n\n\n\n\n\n\n");
  printw("\t\t\t  Game Over! You Won!\n");
  printw("\n");
  printw("\t\t\t       Score: %d\n", *score);
  printw("\n");
  printw("\t\t\t    Main Menu? (y/n)\n");
}

void printGameMenu() {
  printw("\n\n\n\n\n\n\n\n");
  printw("\t\t\t  Welcome to C-Tetris!\n\n");
  printw("\t\t\t  Press Enter to Play!\n\n");
  printw("\t\t\t  Press Escape to Quit\n\n");
}

// outputs original game screen
void showGame(int* score, int* level) {
  printScoreWindow(score, level);
  printTetrisBucket();
  printNextBrickWindow();
}

// updates the score window
void updateScoreWindow(int* score, int* level) {
  verifyWMove(scoreWin, 0, 0);
  wprintw(scoreWin, "  %s%d\n", "Level: ", *level);

  verifyWMove(scoreWin, 4, 10);
  wprintw(scoreWin, "%d", *score);
  verifyWRefresh(scoreWin);
}

// updates the tetrisbucket and brick, is only called when canvas is changed
void updateBucketWindow() {
  int i, j, m, k, l, open = 1;

  for (i = 0; i < tetrisBucket.height; i++) {
    verifyWMove(bucketWin, 1 + i, 2);
    for (j = 0; j < tetrisBucket.width; j++) {
      int valid = 0;

      for (m = 0; m < currentbrick->size; m++) {
        if (currentbrick->visualRepresentation[m][0].y == i) {
          valid = 1;
        }
      }

      if (valid) {
        for (k = 0; k < currentbrick->size; k++) {
          if (currentbrick->visualRepresentation[k][0].y == i) {
            for (l = 0; l < currentbrick->size; l++) {
              if (currentbrick->visualRepresentation[k][l].x == j &&
                      currentbrick->visualRepresentation[k][l].value == 1) {
                wattr_on(bucketWin, currentbrick->color, NULL);
                verifyWMove(bucketWin, 1 + i, 2 + j);
                wprintw(bucketWin, "%c", '#');
                wattr_on(bucketWin, COLOR_PAIR(1), NULL);
                open = 0;
                break;
              }
            }
          }

          if (!open) {
            break;
          }
        }
      }

      if (open) {
        wattr_on(bucketWin, tetrisBucket.bucket[i][j].color, NULL);
        verifyWMove(bucketWin, 1 + i, 2 + j);
        wprintw(bucketWin, "%c", tetrisBucket.bucket[i][j]);
        wattr_on(bucketWin, COLOR_PAIR(1), NULL);
      } else {
        open = 1;
      }
    }
  }
  verifyWRefresh(bucketWin);
}

// called when brick has moved or rotated, so as not to update the entire canvas
void updateBrick() {
  int k, l, y, x;

  for (k = 0; k < currentbrick->size; k++) {
    y = currentbrick->visualRepresentation[k][0].y;
    if (y >= 0 && y < tetrisBucket.height) {
      for (l = 0; l < currentbrick->size; l++) {
        x = currentbrick->visualRepresentation[k][l].x;
        if (x >= 0 && x < tetrisBucket.width) {
          verifyWMove(bucketWin, y + 1, x + 2);

          if (currentbrick->visualRepresentation[k][l].value == 1) {
            wattr_on(bucketWin, currentbrick->color, NULL);
            wprintw(bucketWin, "%c", '#');

            // writes trailing char in width if full brick
            if (l == currentbrick->size - 1 && x < tetrisBucket.width - 1) {
              wattr_on(bucketWin, tetrisBucket.bucket[y][x + 1].color, NULL);
              wprintw(bucketWin, "%c", tetrisBucket.bucket[y][x + 1]);
            } else if (l == 0 && x > 0) {
              verifyWMove(bucketWin, y + 1, x + 1);
              wattr_on(bucketWin, tetrisBucket.bucket[y][x - 1].color, NULL);
              wprintw(bucketWin, "%c", tetrisBucket.bucket[y][x - 1]);
            }
          } else {
            // writes canvas if value 0
            wattr_on(bucketWin, tetrisBucket.bucket[y][x].color, NULL);
            wprintw(bucketWin, "%c", tetrisBucket.bucket[y][x]);
          }
        }

        // writes trailing char in height if full brick
        if (k == 0 ) {
        if (currentbrick->visualRepresentation[k][l].value == 1 && y > 0) {
          int tempY = currentbrick->visualRepresentation[k][l].y - 1;
          verifyWMove(bucketWin, tempY + 1, x + 2);
            wattr_on(bucketWin, tetrisBucket.bucket[tempY][x].color, NULL);
            wprintw(bucketWin, "%c", tetrisBucket.bucket[tempY][x]);
          }
        }
      }
    }
  }
  wattr_on(bucketWin, COLOR_PAIR(1), NULL);
  verifyWRefresh(bucketWin);
}

// updates the nextBrick window
void updateNextBrickWindow() {
  int i, j;

  verifyWMove(nextBrickWin, 3, 0);
  for (i = 0; i < nextBrick->size; i++) {
    wprintw(nextBrickWin, "|%s", "   ");
    for (j = 0; j < nextBrick->size; j++) {
      if (nextBrick->visualRepresentation[i][j].value == 1) {
        wattr_on(nextBrickWin, nextBrick->color, NULL);
        wprintw(nextBrickWin, "%c", '#');
        wattr_on(nextBrickWin, COLOR_PAIR(1), NULL);
      } else {
        wprintw(nextBrickWin, "%c", ' ');
      }
    }
    wprintw(nextBrickWin, "%s|\n", "   ");
  }
  verifyWRefresh(nextBrickWin);
}

// outputs start menu and waits for selection
void showStartMenu(pthread_t* musicThread, GameState* gameState) {
  int c;

  while (*gameState == GameInit) {
    verifyClear();
    resizeTerminal(24, 73);
    printGameMenu();
    verifyRefresh();

    if ((c = wgetch(stdscr)) != ERR) {
      if (c == 10) {
        verifyClear();
        verifyRefresh();
        *gameState = GameRunning;
        break;
      } else if (c == 27) {
        *gameState = GameOver;
        break;
      }
    }
  }
}

// outputs game over menu and waits for selection
void showGameOver(GameState* gameState, int* score, int* level,
        const int* maxLevel) {
  verifyClear();
  resizeTerminal(24, 73);

  if (*level >= *maxLevel) {
    printGameOverWon(score);
  } else {
    printGameOverLost(score);
  }
  verifyRefresh();

  nodelay(stdscr, false);

  int c;

  while (*gameState == GameOver) {
    if ((c = wgetch(stdscr)) != ERR) {
      if (c == 'y') {
        // clear bucket
        int i;
        for (i = 0; i < tetrisBucket.height; i++) {
          free(tetrisBucket.bucket[i]);
        }
        free(tetrisBucket.bucket);

        int size = (sizeof(brickPack.bricks) / sizeof(brickPack.bricks[0]));
        // clear remaining bricks in current brickpack
        for (i = size -
                brickPack.size; i < size; i++) {
          free(brickPack.bricks[i]);
        }

        // clear active bricks
        free(currentbrick);
        free(nextBrick);

        delwin(scoreWin);
        delwin(bucketWin);
        delwin(nextBrickWin);

        *gameState = GameInit;
        break;
      } else if (c == 'n') {
        break;
      }
    }
  }
}
