// arduino libs
#include <Arduino.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <avr/pgmspace.h>
#include "Binary.h"

// own headers/cpp
#include "config.h"

/**
 * This is an arduino sketch for scrolling a text, while playing a fitting
 * melody and showing a note bar on an lcd screen.
 * 
 * @author Anders Mikkelsen
 * @version 04.10.2015
 */

MusicPlayer musicPlayer;
Animator animator;
Scroller scroller;

void setup() { 
  Serial.begin(9600);
 
  setUpShiftRegister(); 
  setUpMatrix();  

  animator.setUpLcd();

  musicPlayer.setUpSpeaker();
  musicPlayer.setAnimator(&animator);
  
  scroller.startScroller();
}

void loop() {    
  int currentTime = millis();

  if (currentTime - scroller.getTimeSinceLastScroll() > 100) {
    scroller.doScroll(currentTime); 
  }

  if (currentTime - musicPlayer.getTimeSinceLastNote() > musicPlayer.getNoteDuration() && !musicPlayer.getMute()) {
    musicPlayer.doTone();
  } else if (musicPlayer.getMute()) {
    animator.lcdRamp(0);
  }

  musicPlayer.checkMuteButton(currentTime);
}
