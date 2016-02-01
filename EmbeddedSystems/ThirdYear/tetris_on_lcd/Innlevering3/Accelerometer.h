//
// Created by Anders on 17.12.2015.
//

#ifndef INNLEVERING3_ACCELEROMETER_H
#define INNLEVERING3_ACCELEROMETER_H

// arduino libs
#include <Arduino.h>
#include <ADXL335.h>

// analog pins for accelerometer
#define X_PIN 0
#define Y_PIN 1
#define Z_PIN 2
#define VOLT_REF 5

// thresholds
#define THETA_KNOCK_THRESHOLD 110

class Accelerometer {
public:
  boolean checkForKnock() { return calculateKnock(); }
private:
  ADXL335 accel = ADXL335(X_PIN, Y_PIN, Z_PIN, VOLT_REF);

  boolean calculateKnock();
};

#endif
