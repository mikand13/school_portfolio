#ifndef INNLEVERING2_ACCELEROMETER_CPP
#define INNLEVERING2_ACCELEROMETER_CPP

/**
 * Class definitions for the accelerometer.
 *
 * @author Anders Mikkelsen
 * @version 14.11.2015
 */

#include "accelerometer.h"

bool Accelerometer::checkAndApplyRotation(Screen &screen) {
  if (updateRotation() != NO_ROTATION_CHANGE) {
    screen.setRotation(getCurrentRotation());

    return true;
  }

  return false;
}

int Accelerometer::updateRotation() {
  byte rotation = calculateRotation(calculateAxisValue(analogRead(X_PIN)),
                                    calculateAxisValue(analogRead(Y_PIN)),
                                    calculateAxisValue(analogRead(Z_PIN)));

  if (currentRotation != rotation) {
    return currentRotation = rotation;
  } else {
    return NO_ROTATION_CHANGE;
  }
}

int Accelerometer::calculateRotation(const double &x, const double &y, const double &z) const {
  if (x > X_TOLERANCE || (x > X_TOLERANCE && z < Z_TOLERANCE) &&
      (y > Y_UPPER_TOLERANCE && y < Y_LOWER_TOLERANCE)) {
    return ROTATION_LANDSCAPE_INVERTED;
  } else if (y < Y_UPPER_TOLERANCE) {
    return ROTATION_PORTRAIT;
  } else if (y > Y_LOWER_TOLERANCE) {
    return ROTATION_PORTRAIT_INVERTED;
  } else if (y > Y_UPPER_TOLERANCE && y < Y_LOWER_TOLERANCE) {
    return ROTATION_LANDSCAPE;
  }

  return NO_ROTATION_CHANGE;
}

inline float Accelerometer::calculateAxisValue(int analogValue) const {
  return ((float) analogValue - ZERO_G) / SCALE;
}

#endif
