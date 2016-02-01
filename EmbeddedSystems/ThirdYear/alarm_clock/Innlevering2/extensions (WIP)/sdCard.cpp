#ifndef INNLEVERING2_SD_CARD_CPP
#define INNLEVERING2_SD_CARD_CPP

#include "sdCard.h"

SdFile SdCard::fetchFile(String fileName) {
  Sd2Card card;
  SdVolume volume;
  SdFile entry;

  if (card.init(SPI_HALF_SPEED, SD_CS) && volume.init(card)) {
    entry.open(&entry, Utils::stringToCharArray(&fileName), O_READ);
  }

  return entry;
}

#endif
