//
// Created by Anders on 13.12.2015.
//

#include "BrickFactory.h"

// generates a random brick from available bricks
void BrickFactory::newRandomBrick(ActiveBrick& activeBrick) {
    if (brickPack.size == 255) buildBrickPack();

    activeBrick.brick = &brickPack.bricks[brickPack.size--];

    // NOTICE: Pivotpoint is center, apply bricks accordingly if you
    // want them to pivot around their own center. They will always pivot on
    // rotate.

    unsigned long representation = 0;

    switch (activeBrick.brick->brickID) {
        case 1:
            setVisualRepresentation(activeBrick, ST7735_PINK_COLOR, representation = 15 << 10);
            break;
        case 2:
            // 255 << 6 is wrong form so had to do a little hack here to place bits correctly
            representation = 7 << 5;
            representation += 1;
            setVisualRepresentation(activeBrick, ST7735_MAGENTA_COLOR, representation <<= 6);
            break;
        case 3:
            setVisualRepresentation(activeBrick, ST7735_YELLOW_COLOR, representation = 57 << 8);
            break;
        case 4:
            setVisualRepresentation(activeBrick, ST7735_BLUE_COLOR, representation = 99 << 7);
            break;
        case 5:
            setVisualRepresentation(activeBrick, ST7735_WHITE_COLOR, representation = 51 << 7);
            break;
        case 6:
            setVisualRepresentation(activeBrick, ST7735_RED_COLOR, representation = 195 << 6);
            break;
        case 7:
            setVisualRepresentation(activeBrick, ST7735_LOW_BLUE_COLOR, representation = 113 << 7);
            break;
        default:
            break;
    }
}

void BrickFactory::buildBrickPack() {
    byte brickIds[] = { 1, 2, 3, 4, 5, 6, 7 };

    randomSeed(analogRead(A5));
    for (byte i = 0; i < BRICK_PACK_SIZE; i++) {
        byte r = random(0, BRICK_PACK_SIZE);

        byte t = brickIds[i];
        brickIds[i] = brickIds[r];
        brickIds[r] = t;
    }

    for (byte i = 0; i < BRICK_PACK_SIZE; i++) {
        brickPack.bricks[i] = createBrick(brickIds[i]);
    }

    brickPack.size = BRICK_PACK_SIZE - 1;
}

Brick BrickFactory::createBrick(byte currentLength) {
    Brick brickToCreate;
    brickToCreate.brickID = currentLength;

    return brickToCreate;
}

// builds visual array for brick and sets color
void BrickFactory::setVisualRepresentation(ActiveBrick& brickToSet, byte color,
                                           unsigned long representation) const {
    brickToSet.color = color;

    for (byte i = BRICK_SIZE - 1; i < 255; i--) {
        for (byte j = BRICK_SIZE - 1; j < 255; j--) {
            byte result = (representation & 0x01) != 0;
            representation >>= 1;

            // fetches a bit from the representation and inserts on value
            brickToSet.visualRepresentation[i][j].value = result;
            // center brick
            brickToSet.visualRepresentation[i][j].x = 3 + j;
            // set y-pos to max to displace rest of brick
            brickToSet.visualRepresentation[i][j].y = i - BRICK_SIZE + 1;
        }
    }
}