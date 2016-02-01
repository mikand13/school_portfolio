//
// Created by Anders on 13.12.2015.
//

#ifndef INNLEVERING3_BRICKFACTORY_H
#define INNLEVERING3_BRICKFACTORY_H

// arduino libs
#include <Arduino.h>

// own libs
#include "LevelManager.h"

struct ActiveBrick;
struct Coordinate;

// colors
#define ST7735_PINK_COLOR 0
#define ST7735_MAGENTA_COLOR 1
#define ST7735_YELLOW_COLOR 2
#define ST7735_BLUE_COLOR 3
#define ST7735_WHITE_COLOR 4
#define ST7735_RED_COLOR 5
#define ST7735_LOW_BLUE_COLOR 6
#define ST7735_GREEN_COLOR 7

// bricks
typedef struct Brick {
    byte brickID;
} Brick;

#define BRICK_SIZE 5
#define BRICK_PACK_SIZE 7

// contains 7 randomized brick configurations to comply with "no triple brick within 7 bricks" rule
typedef struct BrickPack {
    Brick bricks[7];
    byte size;
} BrickPack;

class BrickFactory {
    public:
        void newRandomBrick(ActiveBrick& activeBrick);
        void buildBrickPack();
    private:
        BrickPack brickPack;

        Brick createBrick(byte currentLength);
        void setVisualRepresentation(ActiveBrick& brickToSet, byte color,
                                     unsigned long representation) const;
};


#endif
