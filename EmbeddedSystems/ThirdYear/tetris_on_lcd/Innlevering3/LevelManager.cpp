//
// Created by Anders on 13.12.2015.
//

#include "LevelManager.h"

LevelManager::LevelManager() :
        highestFilledRow(0) {}

// builds bucket and two first bricks, sets windows and gets the game ready
void LevelManager::initializeNewGame() {
    initializeTetrisBucket();
    brickFactory->buildBrickPack();

    brickFactory->newRandomBrick(currentBrick);
    brickFactory->newRandomBrick(nextBrick);

    // advance first brick
    for (byte i = 0; i < BRICK_SIZE; i++) {
        for (byte j = 0; j < BRICK_SIZE; j++) {
            currentBrick.visualRepresentation[i][j].y += BRICK_HEIGHT_OFFSET_AT_START;
        }
    }
}

void LevelManager::initializeTetrisBucket() {
    // fills bucket with empty and unfilled representations
    for (byte i = 0; i < BUCKET_HEIGHT; i++) {
        for (byte j = 0; j < BUCKET_WIDTH; j++) {
            tetrisBucket.bucket[i][j].color = ST7735_GREEN_COLOR;
            tetrisBucket.bucket[i][j].value = j % 2 == 0 ? UNFILLED_VALUE : EMPTY_VALUE;
        }
    }
}

void LevelManager::spawnNewBrick() {
    currentBrick = nextBrick;
    brickFactory->newRandomBrick(nextBrick);
}

byte LevelManager::moveBrick(BrickDirection brickDirection, int& score) {
    this->brickDirection = brickDirection;
    byte collision = 0, wallCollision = 0;
    byte respawn = 0;

    // check for collisions if below bucketroof
    if (checkEnteredBucket()) {
        for (byte i = 0; i < BRICK_SIZE; i++) {
            for (byte j = 0; j < BRICK_SIZE; j++) {
                // check for wall collision and bucket / other brick collision
                if (wallCollision || collision) {
                    break;
                } else {
                    checkForCollision(brickDirection, i, j, collision, wallCollision);
                }
            }
        }
    } else {
        // lock brick til entirely in bucket
        wallCollision = 1;
    }

    // handle collision or move if none
    if (collision) {
        // return endgame if collision at entrypoint
        if (currentBrick.visualRepresentation[0][0].y > BUCKET_HEIGHT) {
            respawn = 2;
        } else {
            // adds score for land
            score += 10;

            // insert brick into canvas
            Coordinate xyvalue;
            highestFilledRow = currentBrick.visualRepresentation[0][0].y;

            for (byte i = 0; i < BRICK_SIZE; i++) {
                for (byte j = 0; j < BRICK_SIZE; j++) {
                    xyvalue = currentBrick.visualRepresentation[i][j];

                    if (xyvalue.value == 1 && xyvalue.y >= 0) {
                        tetrisBucket.bucket[xyvalue.y][xyvalue.x].value = FILLED_VALUE;
                        tetrisBucket.bucket[xyvalue.y][xyvalue.x].color = currentBrick.color;
                    }
                }
            }

            respawn = 1;
        }
    } else {
        applyMovement(brickDirection, wallCollision);
    }

    return respawn;
}

boolean LevelManager::checkEnteredBucket() const {
    boolean enteredBucket = true;

    // this verifies wether or not the brick has completely entered the bucket
    for (byte i = 0; i < BRICK_SIZE; i++) {
        for (byte j = 0; j < BRICK_SIZE; j++) {
            if (currentBrick.visualRepresentation[i][j].value == 1 &&
                    currentBrick.visualRepresentation[i][j].y > BUCKET_HEIGHT + 1) {
                enteredBucket = false;
            }
        }
    }

    return enteredBucket;
}

void LevelManager::checkForCollision(BrickDirection brickDirection, byte i, byte j,
                                     byte& collision, byte& wallCollision) const {
    byte value = currentBrick.visualRepresentation[i][j].value;
    byte x = currentBrick.visualRepresentation[i][j].x;
    byte y = currentBrick.visualRepresentation[i][j].y;

    switch (brickDirection) {
        case Right:
            if (wallCollision == 0) {
                // added these variables for readability
                value = currentBrick.visualRepresentation[i][BRICK_SIZE - 1 - j].value;
                x = currentBrick.visualRepresentation[i][BRICK_SIZE - 1 - j].x;
                y = currentBrick.visualRepresentation[i][BRICK_SIZE - 1 - j].y;

                wallCollision = value == 1 &&
                        (x == BUCKET_WIDTH - 1 ||
                                tetrisBucket.bucket[y][x + 1].value == FILLED_VALUE);
            }
            return;
        case Left:
            if (wallCollision == 0) wallCollision = value == 1 &&
                        (x == 0 || tetrisBucket.bucket[y][x - 1].value == FILLED_VALUE);
            return;
        case Normal:
            if (collision == 0) {
                // added these variables for readability
                value = currentBrick.visualRepresentation[BRICK_SIZE - 1 - i][j].value;
                x = currentBrick.visualRepresentation[BRICK_SIZE - 1 - i][j].x;
                y = currentBrick.visualRepresentation[BRICK_SIZE - 1 - i][j].y;

                collision = value == 1 && (y + 1 == BUCKET_HEIGHT ||
                                           tetrisBucket.bucket[y + 1][x].value == FILLED_VALUE);
            }
            return;
    }
}

void LevelManager::applyMovement(BrickDirection brickDirection, byte& wallCollision) {
    for (byte i = 0; i < BRICK_SIZE; i++) {
        for (byte j = 0; j < BRICK_SIZE; j++) {
            switch (brickDirection) {
                case Right:
                case Left:
                    if (wallCollision == 0) {
                        currentBrick.visualRepresentation[i][j].x
                                += brickDirection == Right ? 1 : -1;
                    }
                    break;
                case Normal:
                    currentBrick.visualRepresentation[i][j].y++;
                    break;
                default:
                    break;
            }
        }
    }
}

// check legality of rotation and rotates brick around pivot.
void LevelManager::rotateBrick() {
    // hack to not rotate square, will only ever be one square
    if (currentBrick.color != ST7735_BLUE_COLOR) {
        if (checkEnteredBucket()) {
            boolean legalRotation = true;
            byte width = BUCKET_WIDTH;
            byte height = BUCKET_HEIGHT;

            // make and rotate test, then check legality
            ActiveBrick rotateTest;

            for (byte i = 0; i < BRICK_SIZE; i++) {
                for (byte j = 0; j < BRICK_SIZE; j++) {
                    // added these for readability of ifs 10 lines down
                    byte currentBrickX = BRICK_SIZE - 1 - i;
                    byte x = currentBrick.visualRepresentation[j][i].x;
                    byte y = currentBrick.visualRepresentation[j][i].y;

                    rotateTest.visualRepresentation[j][i].value =
                            currentBrick.visualRepresentation[currentBrickX][j].value;
                    byte value = rotateTest.visualRepresentation[j][i].value;

                    // only check if location in bucket is part of brick
                    if (value == 1) {
                        // verify inside bucket
                        if (!(x >= 0 && x < width && y < height && y >= 0 &&
                              // check for other bricks
                              tetrisBucket.bucket[y][x].value != FILLED_VALUE)) {
                            legalRotation = false;
                        }
                    }

                    if (!legalRotation) { break; }
                }
                if (!legalRotation) { break; }
            }

            // implement rotation
            if (legalRotation) {
                for (byte i = 0; i < BRICK_SIZE; i++) {
                    for (byte j = 0; j < BRICK_SIZE; j++) {
                        currentBrick.visualRepresentation[i][j].value
                                = rotateTest.visualRepresentation[i][j].value;
                    }
                }
            }
        }
    }
}