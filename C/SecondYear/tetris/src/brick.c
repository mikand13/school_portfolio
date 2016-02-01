/*
  This source file defines all brick manipulations.
 */

int moveBrick(BrickDirection brickDirection, int* score);
void checkForCollision(BrickDirection brickDirection,
        int i, int j,
        int* collision, int* wallCollision);
void applyMovement(BrickDirection brickDirection, int* wallCollision);
void rotateBrick();
int checkEnteredBucket();

// reacts to user input and regular update based on gamespeed
int moveBrick(BrickDirection brickDirection, int* score) {
  int i, j;
  int collision = 0, wallCollision = 0;
  int respawn = 0;
  int size = currentbrick->size;

  // check for collisions if below bucketroof
  if (checkEnteredBucket()) {
    for (i = 0; i < size; i++) {
      for (j = 0; j < size; j++) {
        // check for wall collision and bucket / other brick collision
        if (wallCollision == 0 || collision == 0) {
          checkForCollision(brickDirection, i, j, &collision, &wallCollision);
        } else {
          break;
        }
      }
    }
  } else {
    // lock brick til entirely in bucket
    wallCollision = 1;
  }

  // do move if no collision
  if (collision == 0) {
    applyMovement(brickDirection, &wallCollision);
  } else {
    // return endgame if collision at entrypoint
    if (currentbrick->visualRepresentation[0][0].y <= 0) {
      respawn = 2;
    } else {
      // adds score for land
      *score = *score + 10;

      // insert brick into canvas.
      // brick is freed in game loop
      for (i = 0; i < size; i++) {
        for (j = 0; j < size; j++) {
          // added these variables for readability
          int x = currentbrick->visualRepresentation[i][j].x;
          int y = currentbrick->visualRepresentation[i][j].y;

          if (currentbrick->visualRepresentation[i][j].value == 1 && y >= 0) {
            tetrisBucket.bucket[y][x].value = '#';
            tetrisBucket.bucket[y][x].color = currentbrick->color;
          }
        }
      }
      respawn = 1;
    }
  }

  collision = 0;
  wallCollision = 0;
  return respawn;
}

int checkEnteredBucket() {
  int i, j, enteredBucket = 1;
  // this verifies wether or not the brick has completely entered the bucket
  for (i = 0; i < currentbrick->size; i++) {
    for (j = 0; j < currentbrick->size; j++) {
      if (currentbrick->visualRepresentation[i][j].value == 1 &&
              currentbrick->visualRepresentation[i][j].y < 0) {
        enteredBucket = 0;
      }
    }
  }
  return enteredBucket;
}

void checkForCollision(BrickDirection brickDirection,
        int i, int j,
        int* collision, int* wallCollision) {
  int value = currentbrick->visualRepresentation[i][j].value;
  int x = currentbrick->visualRepresentation[i][j].x;
  int y = currentbrick->visualRepresentation[i][j].y;
  int testWidth = (int) tetrisBucket.width;

  switch (brickDirection) {
    case Right:
      if (*wallCollision == 0) {
        // added these variables for readability
        value = currentbrick->
                visualRepresentation[i][currentbrick->size - 1 - j].value;
        x = currentbrick->visualRepresentation[i][currentbrick->size - 1 - j].x;
        y = currentbrick->visualRepresentation[i][currentbrick->size - 1 - j].y;

        *wallCollision = value == 1 &&
                (x == testWidth - 1 ||
                        tetrisBucket.bucket[y][x + 1].value == '#');
      }
      return;
    case Left:
      if (*wallCollision == 0) {
        *wallCollision = value == 1 && (x == 0 ||
                tetrisBucket.bucket[y][x - 1].value == '#');
      }
      return;
    case Normal:
      if (*collision == 0) {
        // added these variables for readability
        value = currentbrick->
                visualRepresentation[currentbrick->size - 1 - i][j].value;
        x = currentbrick->visualRepresentation[currentbrick->size - 1 - i][j].x;
        y = currentbrick->visualRepresentation[currentbrick->size - 1 - i][j].y;

        *collision = value == 1 &&
                (y + 1 == tetrisBucket.height ||
                        tetrisBucket.bucket[y + 1][x].value == '#');
      }
      return;
  }
}

void applyMovement(BrickDirection brickDirection, int* wallCollision) {
  int i, j;
  for (i = 0; i < currentbrick->size; i++) {
    for (j = 0; j < currentbrick->size; j++) {
      switch (brickDirection) {
        case Right:
          if (*wallCollision == 0) {
            soundPlayer(moveSound);

            currentbrick->visualRepresentation[i][j].x++;
          }
          break;
        case Left:
          if (*wallCollision == 0) {
            soundPlayer(moveSound);

            currentbrick->visualRepresentation[i][j].x--;
          }
          break;
        case Normal:
          currentbrick->visualRepresentation[i][j].y++;
          break;
        default:
          break;
      }
    }
  }
}

// check legality of rotation and rotates brick around pivot.
void rotateBrick() {
  // hack to not rotate square, will only ever be one square
  if (currentbrick->color != COLOR_PAIR(5)) {
    int i, j;

    if (checkEnteredBucket()) {
      int legalRotation = 1;

      int width = (int) tetrisBucket.width;
      int height = (int) tetrisBucket.height;

      // make and rotate test, then check legality
      Brick *rotateTest = malloc(sizeof(Brick));

      for (i = 0; i < currentbrick->size; i++) {
        for (j = 0; j < currentbrick->size; j++) {
          // added these for readability of ifs 10 lines down
          int currentBrickX = currentbrick->size - 1 - i;
          int x = currentbrick->visualRepresentation[j][i].x;
          int y = currentbrick->visualRepresentation[j][i].y;

          rotateTest->visualRepresentation[j][i].value =
                  currentbrick->visualRepresentation[currentBrickX][j].value;
          int value = rotateTest->visualRepresentation[j][i].value;

          // only check if location in bucket is part of brick
          if (value == 1) {
            // verify inside bucket
            if (!(x >= 0 && x < width && y < height && y >= 0 &&
                    // check for other bricks
                            tetrisBucket.bucket[y][x].value != '#')) {
              legalRotation = 0;
            }
          }

          if (!legalRotation) {break;}
        }
        if (!legalRotation) {break;}
      }

      // implement rotation
      if (legalRotation == 1) {
        soundPlayer(rotateSound);

        for (i = 0; i < currentbrick->size; i++) {
          for (j = 0; j < currentbrick->size; j++) {
            currentbrick->visualRepresentation[i][j].value
                    = rotateTest->visualRepresentation[i][j].value;
          }
        }
      }

      free(rotateTest);
      rotateTest = NULL;
    }
  }
}