//
// Created by Anders on 15.12.2015.
//

#ifndef INNLEVERING3_INPUTMANAGER_H
#define INNLEVERING3_INPUTMANAGER_H

// arduino libs
#include <Arduino.h>

#define Y_PIN A3
#define X_PIN A4
#define PUSH_PIN A5

#define MOVEMENT_THRESHOLD_MIN 250
#define MOVEMENT_THRESHOLD_MAX 900

#define LEFT_PS_STICK 0
#define RIGHT_PS_STICK 1
#define UP_PS_STICK 2
#define DOWN_PS_STICK 3
#define PUSH_PS_STICK 5

class InputManager {
public:
    byte checkUserInput();
    boolean checkForPush() {
        delay(50);

        // hack to prevent unintentional push response
        if (analogRead(PUSH_PIN) == LOW) {
            delay(50);
            return analogRead(PUSH_PIN) == LOW;
        }
    }
};

#endif
