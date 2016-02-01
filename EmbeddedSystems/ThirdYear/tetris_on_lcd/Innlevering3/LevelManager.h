//
// Created by Anders on 13.12.2015.
//

#ifndef INNLEVERING3_LEVELMANAGER_H
#define INNLEVERING3_LEVELMANAGER_H

// own libs
#include "BrickFactory.h"
#include "GameManager.h"

class BrickFactory;
struct Brick;

// brick structs
typedef struct Coordinate {
    byte value;
    byte x;
    byte y;
} Coordinate;

typedef struct ActiveBrick {
    Brick* brick;
    byte color;
    Coordinate visualRepresentation[5][5];
};

typedef enum BrickDirection {
    Right,
    Left,
    Normal
} BrickDirection;

// bucket size
#define BUCKET_WIDTH 10
#define BUCKET_HEIGHT 18

// game options
#define BRICK_HEIGHT_OFFSET_AT_START 3

// bucket structs
typedef struct BucketContainer {
    char value;
    byte color;
} BucketContainer;

typedef struct TetrisBucket {
    BucketContainer bucket[BUCKET_HEIGHT][BUCKET_WIDTH];
} TetrisBucket;

class LevelManager {
public:
    LevelManager();
    void setBrickFactory(BrickFactory& brickFactory) { this->brickFactory = &brickFactory; }
    void initializeNewGame();

    void spawnNewBrick();
    byte moveBrick(BrickDirection brickDirection, int& score);
    void rotateBrick();
    void resetHighestFilledRow() { highestFilledRow = 0; }

    TetrisBucket* getTetrisBucket() { return &tetrisBucket; }
    byte getWidth() const { return BUCKET_WIDTH; }
    byte getHeight() const { return BUCKET_HEIGHT; }
    byte getHighestFilledRow() const { return highestFilledRow; }
    BrickDirection getBrickDirection() const { return brickDirection; }
    ActiveBrick* getBrick() { return &currentBrick; }
    ActiveBrick* getNextBrick() { return &nextBrick; }
private:
    BrickFactory* brickFactory;
    TetrisBucket tetrisBucket;

    ActiveBrick currentBrick;
    ActiveBrick nextBrick;

    BrickDirection brickDirection;
    byte highestFilledRow;

    // initialization functions
    void initializeTetrisBucket();

    // gameloop functions
    boolean checkEnteredBucket() const;
    void checkForCollision(BrickDirection brickDirection, byte i, byte j,
                           byte& collision, byte& wallCollision) const;
    void applyMovement(BrickDirection brickDirection, byte& wallCollision);
};

#endif
