//
// Created by Anders on 17.12.2015.
//

#include "Accelerometer.h"

boolean Accelerometer::calculateKnock() {
    accel.update();

    return accel.getTheta() > THETA_KNOCK_THRESHOLD;
}
