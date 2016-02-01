//
// Created by Anders on 13.12.2015.
//

#ifndef INNLEVERING3_GAMEMANAGER_H
#define INNLEVERING3_GAMEMANAGER_H

// arduino libs
#include <Arduino.h>

typedef enum GameState {
    GameInit,
    GameRunning,
    GameOver
} GameState;

extern const unsigned char FILLED_VALUE;
extern const unsigned char UNFILLED_VALUE;
extern const unsigned char EMPTY_VALUE;

// own libs
#include "LevelManager.h"
#include "Screen.h"
#include "SDCardManager.h"
#include "InputManager.h"
#include "MusicPlayer.h"
#include "Accelerometer.h"

class LevelManager;
class Screen;
class SDCardManager;
class InputManager;
class MusicPlayer;
class Accelerometer;

#define MAX_LEVEL 10
#define MOVE_MAX 220

class GameManager {
public:
    GameManager();
    void setManagers(LevelManager& levelManager, Screen& screen, Accelerometer& accelerometer,
                     SDCardManager& sdCardManager, InputManager& inputManager);
    void setMusicPlayer(MusicPlayer& musicPlayer);

    void initializeNewGameState();
    void startGame();
    void resetToNewGame();
    void pauseGame();

    void doGameLoop();
    void updateMusic();

    GameState* getGameState() { return &gameState; }
    boolean checkUserInput();
    boolean checkPushInput();
private:
    // managers
    LevelManager* levelManager;
    Screen* screen;
    SDCardManager* sdCardManager;
    InputManager* inputManager;
    MusicPlayer* musicPlayer;
    Accelerometer* accelerometer;

    // state variables
    GameState gameState;
    byte level;
    int score, standardSpeed, gameSpeed;
    boolean mayMoveSideWays;
    unsigned long before;

    // state functions
    void resetGameParams();
    void showGame();
    void setMusicTimer();
    void gameOver();

    // gameloop functions
    void checkRespawn(byte respawn);
    void checkScore();
    byte checkForFullRows();
};


#endif
