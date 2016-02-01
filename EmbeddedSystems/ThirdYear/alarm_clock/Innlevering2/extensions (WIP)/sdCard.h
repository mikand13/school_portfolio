#ifndef INNLEVERING2_SD_CARD_H
#define INNLEVERING2_SD_CARD_H

// include the SD library:
#include <SPI.h>
#include <SD.h>

// own libs
#include "utils.h"

#define SD_CS 4

class SdCard {
  public:
    SdCard() {}
    SdFile fetchFile(String fileName);
};

#endif

