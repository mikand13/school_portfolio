//
// Created by Anders on 15.12.2015.
//

// arduino libs
#include <Arduino.h>

#include "InputManager.h"

byte InputManager::checkUserInput() {
    int y = analogRead(Y_PIN);
    int x = analogRead(X_PIN);
    int push = analogRead(PUSH_PIN);

    byte inputType = 0;

    if ((y < MOVEMENT_THRESHOLD_MIN || y > MOVEMENT_THRESHOLD_MAX) ||
            (x < MOVEMENT_THRESHOLD_MIN || x > MOVEMENT_THRESHOLD_MAX) ||
            push == LOW) {
        if (y < MOVEMENT_THRESHOLD_MIN) {
            inputType = DOWN_PS_STICK;
        } else if (y > MOVEMENT_THRESHOLD_MAX) {
            inputType = UP_PS_STICK;
        } else if (x < MOVEMENT_THRESHOLD_MIN) {
            inputType = RIGHT_PS_STICK;
        } else if (x > MOVEMENT_THRESHOLD_MAX) {
            inputType = LEFT_PS_STICK;
        } else if (push == LOW) {
            inputType = PUSH_PS_STICK;
        }
    } else {
        inputType = -1;
    }

    return inputType;
}