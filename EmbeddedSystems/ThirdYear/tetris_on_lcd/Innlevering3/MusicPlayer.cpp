#include "musicPlayer.h"

MusicPlayer::MusicPlayer() :
        melodyCounter(0),
        tempoCounter(0){
  pinMode(SPEAKER_PIN, OUTPUT);
}

void MusicPlayer::doTone() {
  if (melodyCounter >= melodySize || tempoCounter >= tempoSize) {
    melodyCounter = 0;
    tempoCounter = 0;
  }   

  noTone(SPEAKER_PIN);
  playNote(pgm_read_word_near(melody + melodyCounter++),
           pgm_read_word_near(tempo + tempoCounter++));
}

void MusicPlayer::playNote(int note, int duration) const {
  tone(SPEAKER_PIN, note, 1000 / duration);

  // set timer1 to correct update
  switch (duration) {
    case 4:
      OCR1A = FOUR_BEAT;
          break;
    case 8:
      OCR1A = EIGHT_BEAT;
          break;
    case 16:
      OCR1A = SIXTEEN_BEAT;
          break;
    default:
      break;
  }
}

void MusicPlayer::stopMusic() {
  noTone(SPEAKER_PIN);
  melodyCounter = tempoCounter = 0;
}