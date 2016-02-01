// arduino libs
#include <Arduino.h>
#include <Adafruit_ST7735.h>
#include <ADXL335.h>
#include <SD.h>

// own libs
#include "GameManager.h"

GameManager gameManager;
LevelManager levelManager;
BrickFactory brickFactory;
Screen screen;
InputManager inputManager;
SDCardManager sdCardManager;
MusicPlayer musicPlayer;
Accelerometer accelerometer;

void setup() {
    levelManager.setBrickFactory(brickFactory);
    gameManager.setManagers(levelManager, screen, accelerometer, sdCardManager, inputManager);
    gameManager.setMusicPlayer(musicPlayer);
    gameManager.initializeNewGameState();
}

// set in game manager
ISR(TIMER1_COMPA_vect) {
    gameManager.updateMusic();
}

void loop() {
    while (*gameManager.getGameState() == GameInit) {
        if (gameManager.checkPushInput()) {
            gameManager.startGame();
            break;
        }
    }

    if (*gameManager.getGameState() != GameOver) {
        gameManager.doGameLoop();
    }

    while (*gameManager.getGameState() == GameOver) {
        if (gameManager.checkPushInput()) {
            gameManager.resetToNewGame();
        }
    }
}