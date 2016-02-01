//
// Created by Anders on 14.12.2015.
//

#include "SDCardManager.h"

SDCardManager::SDCardManager() {
    if (!SD.begin(CHIP_SELECT)) {
        return;
    }
}

char* SDCardManager::getHighScore(char highScoreArray[], const byte len) {
    openFile();

    if (highScoreTxt) {
        for (byte i = 0; i < len; i++) {
            highScoreArray[i] = (char) highScoreTxt.read();
        }

        highScoreTxt.close();
    } else {
      for (byte i = 0; i < len; i++) {
        highScoreArray[i] = '0';
      }
    }

    highScoreArray[len] = '\0';

    return highScoreArray;
}

void SDCardManager::setHighScore(const int highScore) {
    openFile(true);

    char valueArray[6];
    if (highScoreTxt) {
        highScoreTxt.write(Utils::getCharArrayFromInteger(valueArray, highScore), 6);
        highScoreTxt.close();
    }
}

void SDCardManager::openFile(const boolean write) {
    char txtFileArray[7];
    Utils::getCharArrayFromProgmem(txtFileArray, highScoreTxtFile);

    if (write) {
        SD.remove(txtFileArray);
        highScoreTxt = SD.open(txtFileArray, FILE_WRITE);
    } else {
        highScoreTxt = SD.open(txtFileArray);
    }
}
