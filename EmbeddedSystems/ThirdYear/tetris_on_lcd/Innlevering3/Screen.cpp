//
// Created by Anders on 13.12.2015.
//

#include "Screen.h"

bool Screen::initialize() {
    tft.initR(INITR_BLACKTAB);
    tft.fillScreen(ST7735_BLACK);
    tft.setRotation(ROTATION_PORTRAIT_INVERTED);
    pinMode(TFT_BCL, OUTPUT);

    return true;
}

// updates the tetrisbucket and brick, is only called when canvas is changed
void Screen::updateBucketWindow(LevelManager& levelManager, const boolean complete) {
    TetrisBucket* tetrisBucket = levelManager.getTetrisBucket();
    char output[2] = { ' ', '\0' };
    boolean noBrickOnLine = true;

    for (int i = BUCKET_HEIGHT - 3; i >= levelManager.getHighestFilledRow(); i--) {
        for (byte j = 0; j < levelManager.getWidth(); j++) {
            if (tetrisBucket->bucket[i][j].value == FILLED_VALUE) {
                noBrickOnLine = false;
            }

            if (complete || tetrisBucket->bucket[i][j].value == FILLED_VALUE) {
                setBrickColor(tetrisBucket->bucket[i][j].color);
                output[0] = tetrisBucket->bucket[i][j].value;
                writeGameObjects(output, j, i);
            }
        }

        if (noBrickOnLine) break;
    }
}

// updates the score window
void Screen::updateScoreWindow(const int score, const byte level) {
    char valueArray[6];

    currentTextColor = ST7735_GREEN;
    writeCharArray(Utils::getCharArrayFromInteger(valueArray, level),
                   FONT_LENGTH_MULTIPLER,
                   (SCORE_WINDOW_START + 2) * FONT_HEIGHT_MULTIPLIER);
    writeCharArray(Utils::getCharArrayFromInteger(valueArray, score),
                   FONT_LENGTH_MULTIPLER,
                   (SCORE_WINDOW_START + 4) * FONT_HEIGHT_MULTIPLIER);
}

// updates the nextBrick window
void Screen::updateNextBrickWindow(const ActiveBrick& nextBrick) {
    setBrickColor(nextBrick.color);

    for (byte i = 0; i < BRICK_SIZE; i++) {
        for (byte j = 0; j < BRICK_SIZE; j++) {
            if (nextBrick.visualRepresentation[i][j].value == 1) {
                writeGameText(nextBrickFilled, 3 + j, NEXT_BRICK_WINDOW_START + 2 + i);
            } else {
                writeGameText(nextBrickEmpty, 3 + j, NEXT_BRICK_WINDOW_START + 2 + i);
            }
        }
    }
}

// called when brick has moved or rotated, so as not to update the entire canvas
void Screen::updateBrick(LevelManager& levelManager,
                         const ActiveBrick& currentBrick, const boolean complete) {
    TetrisBucket* tetrisBucket = levelManager.getTetrisBucket();
    char output[2] = { FILLED_VALUE, '\0' };
    byte y, x;

    for (byte k = 0; k < BRICK_SIZE; k++) {
        y = currentBrick.visualRepresentation[k][0].y;
        if (y >= 0 && y < levelManager.getHeight()) {
            for (byte l = 0; l < BRICK_SIZE; l++) {
                x = currentBrick.visualRepresentation[k][l].x;
                if (x >= 0 && x < levelManager.getWidth()) {
                    if (currentBrick.visualRepresentation[k][l].value == 1) {
                        setBrickColor(currentBrick.color);
                        output[0] = FILLED_VALUE;
                        writeGameObjects(output, x, y);

                        // writes trailing char in width if full brick
                        if (l == BRICK_SIZE - 1 && x < levelManager.getWidth() - 1) {
                            bucketContainerWrite(*tetrisBucket, output, x + 1, y);
                        } else if (l == 0 && x > 0) {
                            bucketContainerWrite(*tetrisBucket, output, x - 1, y);
                        }

                        if (!complete) {
                            switch (levelManager.getBrickDirection()) {
                                case Normal:
                                    if (currentBrick.visualRepresentation[k - 1][l].value != 1) {
                                        bucketContainerWrite(*tetrisBucket, output, x, y - 1);
                                    }
                                    break;
                                case Left:
                                    if (currentBrick.visualRepresentation[k][l + 1].value != 1) {
                                        bucketContainerWrite(*tetrisBucket, output, x + 1, y);
                                    }
                                    break;
                                case Right:
                                    if (currentBrick.visualRepresentation[k][l - 1].value != 1) {
                                        bucketContainerWrite(*tetrisBucket, output, x - 1, y);
                                    }
                                    break;
                            }
                        }
                    } else if (complete) {
                        bucketContainerWrite(*tetrisBucket, output, x, y);
                    }
                }

                // writes trailing char in height if full brick
                if (k == 0 ) {
                    if (currentBrick.visualRepresentation[k][l].value == 1 && y < BUCKET_HEIGHT) {
                        if (y - 1 < BUCKET_HEIGHT) {
                            byte tempY = currentBrick.visualRepresentation[k][l].y - 1;

                            setBrickColor(tetrisBucket->bucket[tempY][x].color);
                            output[0] = tetrisBucket->bucket[tempY][x].value;
                            writeGameObjects(output, x, y - 1);
                        }
                    }
                }
            }
        }
    }
}

void Screen::removeBrick(LevelManager& levelManager, const ActiveBrick& currentBrick) {
    TetrisBucket* tetrisBucket = levelManager.getTetrisBucket();
    char output[2] = { FILLED_VALUE, '\0' };

    byte y, x;
    for (byte k = 0; k < BRICK_SIZE; k++) {
        y = currentBrick.visualRepresentation[k][0].y;
        if (y < levelManager.getHeight()) {
            for (byte l = 0; l < BRICK_SIZE; l++) {
                x = currentBrick.visualRepresentation[k][l].x;
                if (x >= 0 && x < levelManager.getWidth()) {
                    if (currentBrick.visualRepresentation[k][l].value == 1) {
                        output[0] = tetrisBucket->bucket[y][x].value;
                        bucketContainerWrite(*tetrisBucket, output, x, y);
                    }
                }
            }
        }
    }
}

void Screen::bucketContainerWrite(const TetrisBucket& tetrisBucket, char output[],
                                  const byte x, const byte y) {
    if (x < BUCKET_WIDTH && x >= 0 && y < BUCKET_HEIGHT && y >= 0) {
        setBrickColor(tetrisBucket.bucket[y][x].color);
        output[0] = tetrisBucket.bucket[y][x].value;
        writeGameObjects(output, x, y);
    }
}

void Screen::printGameMenu(const char highScore[]) {
    if (atoi(highScore) > 0) {
      writeGameText(introHighScore, 4, GAME_MENU_START_POINT);
      writeCharArray(highScore,
                     17 * FONT_LENGTH_MULTIPLER,
                     GAME_MENU_START_POINT * FONT_HEIGHT_MULTIPLIER);
    } else {
      writeGameText(introNoHighScore, 1, GAME_MENU_START_POINT);
    }
    
    writeGameText(introString, 4, GAME_MENU_START_POINT + 2);
    writeGameText(introFlatFrame, 4, GAME_MENU_START_POINT + 3);
    writeGameText(introPushToPlayCommand, 5, GAME_MENU_START_POINT + 4);
    writeGameText(introIngameCommands, 9, GAME_MENU_START_POINT + 6);
    writeGameText(introPauseCommand, 1, GAME_MENU_START_POINT + 8);
}

void Screen::printGame(LevelManager& levelManager, const int score, const byte level) {
    currentTextColor = ST7735_GREEN;
    printScoreWindow(score, level);
    printCommands();
    printNextBrickWindow(*levelManager.getNextBrick());
    printTetrisBucket(levelManager, *levelManager.getBrick());
}

// displays score and commands left of bucket
void Screen::printScoreWindow(const int score, const byte level) {
    writeGameText(scoreWindowFlatFrame, 0, SCORE_WINDOW_START);

    printScoreBox(levelString, 1);
    printScoreBox(scoreWindowScore, 3);
    updateScoreWindow(score, level);

    writeGameText(scoreWindowFlatFrame, 0, SCORE_WINDOW_START + 5);
}

void Screen::printScoreBox(const char string[], const byte start) {
    const char seperator[2] = { SEPERATOR, '\0' };

    writeGameText(seperator, 0, SCORE_WINDOW_START + start, false);
    writeGameText(string, 1, SCORE_WINDOW_START + start);
    paddingForScoreWindow(10, SCORE_WINDOW_START + start);
    writeGameText(seperator, 0, SCORE_WINDOW_START + start + 1, false);
    paddingForScoreWindow(10, SCORE_WINDOW_START + start + 1);
}

void Screen::paddingForScoreWindow(const byte x, const byte y) {
    const char temp[2] = { SEPERATOR, '\0' };
    writeCharArray(temp, x * FONT_LENGTH_MULTIPLER, y * FONT_HEIGHT_MULTIPLIER);
}

void Screen::printCommands() {
    writeGameText(sidesMoveCommand, 0, COMMANDS_WINDOW_START);
    writeGameText(upMoveCommand, 0, COMMANDS_WINDOW_START + 1);
    writeGameText(downMoveCommand, 0, COMMANDS_WINDOW_START + 2);
    writeGameText(pushMoveCommand, 0, COMMANDS_WINDOW_START + 3);
}

// displays the upcoming brick right of the bucket
void Screen::printNextBrickWindow(const ActiveBrick& nextBrick) {
    const char seperator[2] = { SEPERATOR, '\0' };

    writeGameText(nextBrickString, 0, NEXT_BRICK_WINDOW_START);
    writeGameText(scoreWindowFlatFrame, 0, NEXT_BRICK_WINDOW_START + 1);

    for (byte i = 0; i < BRICK_SIZE; i++) {
        currentTextColor = ST7735_GREEN;
        writeGameText(seperator, 0, NEXT_BRICK_WINDOW_START + 2 + i, false);
        writeGameText(nextBrickNothing, 1, NEXT_BRICK_WINDOW_START + 2 + i);
        paddingForScoreWindow(10, NEXT_BRICK_WINDOW_START + 2 + i);
    }

    updateNextBrickWindow(nextBrick);

    currentTextColor = ST7735_GREEN;
    writeGameText(scoreWindowFlatFrame, 0, NEXT_BRICK_WINDOW_START + 7);
}

// prints the static canvas and the floating brick dynamically
void Screen::printTetrisBucket(LevelManager& levelManager, const ActiveBrick& currentBrick) {
    const char rowStart[3] = { '<', '!', '\0' };
    const char rowEnd[3] = { '!', '>', '\0' };
    char output[2] = { ' ', '\0' };

    for (byte i = 0; i < levelManager.getHeight(); i++) {
        writeGameObjects(rowStart, -2, i);
        writeGameObjects(rowEnd, levelManager.getWidth(), i);

        for (byte j = 0; j < levelManager.getWidth(); j++) {
            output[0] = levelManager.getTetrisBucket()->bucket[i][j].value;
            writeGameObjects(output, j, i);
        }
    }

    printBucketBottom(levelManager, rowStart, rowEnd);

    updateBrick(levelManager, currentBrick);
}

void Screen::printBucketBottom(const LevelManager& levelManager,
                               const char rowStart[], const char rowEnd[]) {
    writeGameObjects(rowStart, -2, levelManager.getHeight());
    char output[2] = { '=', '\0' };

    printBucketRow(levelManager, output, levelManager.getHeight());
    writeGameObjects(rowEnd, levelManager.getWidth(), levelManager.getHeight());

    output[0] = '^';
    printBucketRow(levelManager, output, levelManager.getHeight() + 1);
}

void Screen::printBucketRow(const LevelManager& levelManager, const char output[], const byte y) {
    for (byte i = 0; i < levelManager.getWidth(); i++) {
        writeGameObjects(output, i, y);
    }
}

void Screen::pauseGame() {
    currentTextColor = ST7735_RED;

    char outputArray[strlen_P(paused) + 1];
    writeGameObjects(Utils::getCharArrayFromProgmem(outputArray, paused), 1, PAUSED_WRITE_LEVEL);
}

void Screen::unPauseGame(LevelManager& levelManager) {
    TetrisBucket* tetrisBucket = levelManager.getTetrisBucket();
    char output[2] = { ' ', '\0' };

    for (byte j = 0; j < levelManager.getWidth(); j++) {
        setBrickColor(tetrisBucket->bucket[PAUSED_WRITE_LEVEL][j].color);
        output[0] = tetrisBucket->bucket[PAUSED_WRITE_LEVEL][j].value;
        writeGameObjects(output, j, PAUSED_WRITE_LEVEL);
    }
}

// outputs game over menu
void Screen::showGameOver(const int score, const byte level, const byte maxLevel) {
    if (level >= maxLevel) {
        printGameOverWon(score, level);
    } else {
        printGameOverLost(score, level);
    }
}
// three simple printmethods, no windowsinitialization necessary
void Screen::printGameOverLost(const int score, const byte level) {
    writeGameText(gameOverLost, 1, GAME_OVER_START_POINT);
    writeGameText(gameOverLostFlatFrame, 1, GAME_OVER_START_POINT + 1);

    gameStatsAndGoAgainCommand(score, level);
}

void Screen::printGameOverWon(const int score, const byte level) {
    writeGameText(gameOverWin, 2, GAME_OVER_START_POINT);
    writeGameText(gameOverWonFlatFrame, 2, GAME_OVER_START_POINT + 1);

    gameStatsAndGoAgainCommand(score, level);
}

void Screen::gameStatsAndGoAgainCommand(const int score, const byte level) {
    char valueArray[6];

    writeGameText(levelString, 2, GAME_OVER_START_POINT + 2);
    writeCharArray(Utils::getCharArrayFromInteger(
                           valueArray, level),
                   10 * FONT_LENGTH_MULTIPLER,
                   (GAME_OVER_START_POINT + 2) * FONT_HEIGHT_MULTIPLIER);

    writeGameText(scoreWindowScore, 12, GAME_OVER_START_POINT + 2);
    writeCharArray(Utils::getCharArrayFromInteger(
                           valueArray, score),
                   20 * FONT_LENGTH_MULTIPLER,
                   (GAME_OVER_START_POINT + 2) * FONT_HEIGHT_MULTIPLIER);

    writeGameText(playAgainCommand, 2, GAME_OVER_START_POINT + 4);
}

void Screen::setBrickColor(const byte color) {
    switch (color) {
        case ST7735_PINK_COLOR:
            currentTextColor = 0xF850;
            break;
        case ST7735_LOW_BLUE_COLOR:
            currentTextColor = 0x0FFF;
            break;
        case ST7735_BLUE_COLOR:
            currentTextColor = ST7735_BLUE;
            break;
        case ST7735_MAGENTA_COLOR:
            currentTextColor = ST7735_MAGENTA;
            break;
        case ST7735_RED_COLOR:
            currentTextColor = ST7735_RED;
            break;
        case ST7735_WHITE_COLOR:
            currentTextColor = ST7735_WHITE;
            break;
        case ST7735_YELLOW_COLOR:
            currentTextColor = ST7735_YELLOW;
            break;
        case ST7735_GREEN_COLOR:
            currentTextColor = ST7735_GREEN;
        default:
            currentTextColor = ST7735_GREEN;
            break;
    }
}

void Screen::writeString(const char output[], const byte x, const byte y, const byte fontSize) {
    tft.setCursor(x, y);
    tft.setTextSize(fontSize);
    tft.setTextColor(currentTextColor, ST7735_BLACK);

    char outputArray[strlen_P(output) + 1];
    tft.print(Utils::getCharArrayFromProgmem(outputArray, output));
}

void Screen::writeCharArray(const char output[], const byte x, const byte y, const byte fontSize) {
    tft.setCursor(x, y);
    tft.setTextSize(fontSize);
    tft.setTextColor(currentTextColor, ST7735_BLACK);
    tft.print(output);
}

void Screen::writeGameObjects(const char output[], const byte x, const byte y) {
    int newX = (x * FONT_LENGTH_MULTIPLER) + (BUCKET_DISPLACEMENT * FONT_LENGTH_MULTIPLER);

    writeCharArray(output, newX, y * FONT_HEIGHT_MULTIPLIER);
}

void Screen::writeGameText(const char output[], const byte x, const byte y, const boolean convert) {
    if (convert) {
        writeString(output, x * FONT_LENGTH_MULTIPLER, y * FONT_HEIGHT_MULTIPLIER);
    } else {
        writeCharArray(output, x * FONT_LENGTH_MULTIPLER, y * FONT_HEIGHT_MULTIPLIER);
    }
}
