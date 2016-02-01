#ifndef INNLEVERING1_MUSICPLAYER_H
#define INNLEVERING1_MUSICPLAYER_H

#include <Arduino.h>
#include "mode.h"
#include "animator.h"

const int speakerPin = 2;
const int mutePin = 3;

class Animator;

/**
 * This class defines the musicplayer which controls the playing of a melody on the speaker,
 * and controls toggling of the mute button.
 * 
 * @author Anders Mikkelsen
 * @version 04.10.2015
 */
class MusicPlayer {
  public:
    void setUpSpeaker();
    void setAnimator(Animator* animator);
    void doTone();
    void checkMuteButton(int currentTime);

    const int getTimeSinceLastNote() const;
    const boolean getMute() const;
    const int getNoteDuration() const;

    ~MusicPlayer();
  private:  
    Animator* anim = NULL;
  
    int timeSinceLastNote = 0;
  
    int melodyCounter = 0;
    const int melodySize = (sizeof(melody) / sizeof(int));
    int tempoCounter = 0;
    const int tempoSize = (sizeof(tempo) / sizeof(int));
    
    int noteDuration = 0; 
    
    //muting
    boolean mute = false;
    
    int muteButtonState;        
    int lastMuteButtonState = LOW;   
    
    long lastDebounceTime = 0;
    long debounceDelay = 50;

    void animateRamp(int note) const;
    double playNote(int note, int duration) const;
};

#endif
