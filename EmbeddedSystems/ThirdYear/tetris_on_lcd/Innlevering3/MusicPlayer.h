//
// Created by Anders on 13.12.2015.
//

#ifndef INNLEVERING3_MUSICPLAYER_H
#define INNLEVERING3_MUSICPLAYER_H

// arduino libs
#include <Arduino.h>

// own libs
#include "Tetris.h"

#define SPEAKER_PIN 2

// beats
// ((1000 / duration) * 1.30) * (15624 / 1000)
// 15624 ~ one second on interrupt timer
#define FOUR_BEAT 5077
#define EIGHT_BEAT 2539
#define SIXTEEN_BEAT 1269

class MusicPlayer {
public:
  MusicPlayer();

  void doTone();
  void stopMusic();
private:
  int melodyCounter, tempoCounter;
  const int melodySize = (sizeof(melody) / sizeof(int));
  const int tempoSize = (sizeof(tempo) / sizeof(int));

  void playNote(int note, int duration) const;
};

#endif
