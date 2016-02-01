//
// Created by Anders on 13.12.2015.
//

#ifndef INNLEVERING3_SCREEN_H
#define INNLEVERING3_SCREEN_H

// arduino libs
#include <Arduino.h>
#include <avr/pgmspace.h>
#include <Adafruit_ST7735.h>

// own libs
#include "Utils.h"
#include "GameManager.h"
#include "LevelManager.h"

class LevelManager;

// tetris
#define BUCKET_DISPLACEMENT 13

// pins
#define TFT_CS  10
#define TFT_DC   9
#define TFT_RST  8
#define TFT_BCL  5

// defaults
#define MAX_SCREEN_DIM 255
#define ROTATION_PORTRAIT 0
#define ROTATION_LANDSCAPE 1
#define ROTATION_PORTRAIT_INVERTED 2
#define ROTATION_LANDSCAPE_INVERTED 3

// font
#define FONT_LENGTH_MULTIPLER 5
#define FONT_HEIGHT_MULTIPLIER 8

#define BUCKET_WIDTH 10
#define BUCKET_HEIGHT 20

// time
#define SCREEN_DIM_INTERVALL 1

struct TetrisBucket;
struct ActiveBrick;

// strings for views

// intro
const char introHighScore[] PROGMEM = "Highscore: ";
const char introNoHighScore[] PROGMEM = "No highscore is set!";
const char introString[] PROGMEM = "Arduino-Tetris!";
const char introFlatFrame[] PROGMEM = "---------------";
const char introPushToPlayCommand[] PROGMEM = "Push to Play!";
const char introIngameCommands[] PROGMEM = "In-game:";
const char introPauseCommand[] PROGMEM = "Bang table to Pause!";

// gameoverwin
const char gameOverWin[] PROGMEM = "Game Over! You Won!";
const char gameOverWonFlatFrame[] PROGMEM = "-------------------";
const char playAgainCommand[] PROGMEM = "Push for main menu!";

// gameoverlost
const char gameOverLost[] PROGMEM = "Game Over! You Lost!";
const char gameOverLostFlatFrame[] PROGMEM = "--------------------";

// scorewindow
const char levelString[] PROGMEM = "Level:";
const char scoreWindowFlatFrame[] PROGMEM = "---------";
const char scoreWindowScore[] PROGMEM = "Score:";
const char sidesMoveCommand[] PROGMEM = "PS: Move";
const char upMoveCommand[] PROGMEM = "UP: Turn";
const char downMoveCommand[] PROGMEM = "DO: Soft";
const char pushMoveCommand[] PROGMEM = "PU: Hard";

// nextbrickwindow
const char nextBrickString[] PROGMEM = "  Next:  ";
const char nextBrickFilled[] PROGMEM = "#";
const char nextBrickNothing[] PROGMEM = "";
const char nextBrickEmpty[] PROGMEM = " ";

// gamepaused
#define PAUSED_WRITE_LEVEL 15
const char paused[] PROGMEM = "Paused!";

// defines for views

// menus
#define GAME_MENU_START_POINT 6
#define GAME_OVER_START_POINT 6

// score window
#define SCORE_WINDOW_START 0

// commands window
#define COMMANDS_WINDOW_START 7

// next brick window
#define NEXT_BRICK_WINDOW_START 12

class Screen {
public:
    Screen() : currentTextColor(ST7735_GREEN) {}

    bool startScreen() { return initialize(); }
    int getWidth() { return tft.width(); }
    int getHeight() { return tft.height(); }
    void clearScreen() { tft.fillScreen(ST7735_BLACK); }
    void dimDown() { digitalWrite(TFT_BCL, LOW); }
    void dimUp() { digitalWrite(TFT_BCL, HIGH); }
    void invert(const boolean on) { tft.invertDisplay(on); }

    // gamedisplays
    void updateBucketWindow(LevelManager& levelManager, const boolean complete = false);
    void updateScoreWindow(const int score, const byte level);
    void updateNextBrickWindow(const ActiveBrick& nextBrick);

    // brick
    void updateBrick(LevelManager& levelManager, const ActiveBrick& currentBrick,
                     const boolean complete = false);
    void removeBrick(LevelManager& levelManager, const ActiveBrick& currentBrick);

    // ui
    void printGameMenu(const char highScore[]);
    void printGame(LevelManager& levelManager, const int score, const byte level);
    void pauseGame();
    void unPauseGame(LevelManager& levelManager);
    void showGameOver(const int score, const byte level, const byte maxLevel);
private:
    Adafruit_ST7735 tft = Adafruit_ST7735(TFT_CS, TFT_DC, TFT_RST);
    int currentTextColor;
    const char SEPERATOR = '|';

    bool initialize();

    // views for specific elements
    void printScoreWindow(const int score, const byte level);
    void printScoreBox(const char string[], const byte start);
    void paddingForScoreWindow(const byte x, const byte y);
    void printCommands();
    void printNextBrickWindow(const ActiveBrick& nextBrick);
    void printTetrisBucket(LevelManager& levelManager, const ActiveBrick& currentBrick);
    void printBucketBottom(const LevelManager& levelManager,
                           const char rowStart[], const char rowEnd[]);
    void printBucketRow(const LevelManager& levelManager, const char output[], const byte y);

    // bucket update
    void bucketContainerWrite(const TetrisBucket& tetrisBucket, char output[],
                              const byte x, const byte y);

    // ui
    void printGameOverLost(const int score, const byte level);
    void printGameOverWon(const int score, const byte level);
    void gameStatsAndGoAgainCommand(const int score, const byte level);

    // color changer
    void setBrickColor(const byte color);

    // printers
    void writeGameText(const char output[], const byte x, const byte y,
                       const boolean convert = true);
    void writeString(const char output[], const byte x = 0, const byte y = 0,
                     const byte fontSize = 1);
    void writeCharArray(const char output[], const byte x = 0, const byte y = 0,
                        const byte fontSize = 1);
    void writeGameObjects(const char output[], const byte x = 0, const byte y = 0);
};

#endif
