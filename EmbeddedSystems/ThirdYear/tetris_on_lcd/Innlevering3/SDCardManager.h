//
// Created by Anders on 14.12.2015.
//

#ifndef INNLEVERING3_SDCARDMANAGER_H
#define INNLEVERING3_SDCARDMANAGER_H

// arduino libs
#include <Arduino.h>
#include <avr/pgmspace.h>
#include <SD.h>

// own libs
#include "Utils.h"

// Chip select
#define CHIP_SELECT 4

const static char highScoreTxtFile[] PROGMEM = "hs.txt";

class SDCardManager {
public:
    SDCardManager();
    char* getHighScore(char highScoreArray[], const byte len);
    void setHighScore(const int highScore);
private:
    File highScoreTxt;

    void openFile(const boolean write = false);
};

#endif