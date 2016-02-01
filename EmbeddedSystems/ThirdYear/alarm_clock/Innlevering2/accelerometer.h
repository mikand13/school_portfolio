#ifndef INNLEVERING2_ACCELEROMETER_H
#define INNLEVERING2_ACCELEROMETER_H

// arduino libs
#include <Arduino.h>

// own libs
#include "screen.h"

// analog pins for accelerometer
#define X_PIN 0
#define Y_PIN 1
#define Z_PIN 2

// value calculation
#define NO_ROTATION_CHANGE -1

// values for computation
#define ZERO_G 512.0
#define SCALE 102.3

// tolerances for directions
#define X_TOLERANCE -1.4
#define Y_UPPER_TOLERANCE -1.8
#define Y_LOWER_TOLERANCE -1.2
#define Z_TOLERANCE -1.8

/**
 * This header declares the logic for the accelerometer attached to the ST7735.
 *
 * @author Anders Mikkelsen
 * @version 14.11.2015
 */
class Accelerometer {
  public:
    Accelerometer() : currentRotation(ROTATION_LANDSCAPE) {}

    bool startAccelerometer() const { return initialize(); }
    bool checkAndApplyRotation(Screen &screen);
  private:
    byte currentRotation;
    
    bool initialize() const {  return analogRead(X_PIN) && analogRead(Y_PIN) && analogRead(Z_PIN); }
    int updateRotation();
    byte getCurrentRotation() const { return currentRotation; }
    int calculateRotation(const double &x, const double &y, const double &z) const;
    float calculateAxisValue(int analogValue) const;
};

#endif
