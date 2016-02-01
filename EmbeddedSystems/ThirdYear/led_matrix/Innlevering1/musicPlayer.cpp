#include "musicPlayer.h"
#include "animator.h"

void MusicPlayer::setUpSpeaker() {
  pinMode(speakerPin, OUTPUT);
  pinMode(mutePin, INPUT);  

  timeSinceLastNote = millis();
}

void MusicPlayer::setAnimator(Animator* animator) {
  anim = animator;
}

void MusicPlayer::doTone() {
  if (melodyCounter >= melodySize || tempoCounter >= tempoSize) {
    melodyCounter = 0;
    tempoCounter = 0;
  }   

  noTone(speakerPin);
  noteDuration = playNote(pgm_read_word_near(melody + melodyCounter++), pgm_read_word_near(tempo + tempoCounter++));

  timeSinceLastNote = millis();  
}

void MusicPlayer::checkMuteButton(int currentTime) {
  int currentMuteButtonState = digitalRead(mutePin);

  if (currentMuteButtonState != lastMuteButtonState) {
    lastDebounceTime = currentTime;
  }

  if ((millis() - lastDebounceTime) > debounceDelay) {
    if (currentMuteButtonState != muteButtonState) {
      muteButtonState = currentMuteButtonState;

      if (muteButtonState == HIGH) {
        mute = !mute;
      }
    }
  }
  
  lastMuteButtonState = currentMuteButtonState;  
}

const int MusicPlayer::getTimeSinceLastNote() const {
  return timeSinceLastNote;  
}

const boolean MusicPlayer::getMute() const {
  return mute;  
}

const int MusicPlayer::getNoteDuration() const {
  return noteDuration;
}

void MusicPlayer::animateRamp(int note) const {
  if (anim != NULL) {
    anim->lcdRamp(map(note, 31, 4978, 0, 16 ));  
  }  
}

double MusicPlayer::playNote(int note, int duration) const {
  int noteDuration = 1000 / duration;
  animateRamp(note);
  tone(speakerPin, note, noteDuration);
  
  return noteDuration * 1.30;
}

MusicPlayer::~MusicPlayer() {
  free(anim);  
}
