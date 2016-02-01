//
// Created by Anders on 14.12.2015.
//

#ifndef INNLEVERING3_UTILS_H
#define INNLEVERING3_UTILS_H

// arduino libs
#include <Arduino.h>

class Utils {
public:
    static char* getCharArrayFromProgmem(char array[], const char progMemArray[]) {
      byte len = strlen_P(progMemArray);

      for (byte i = 0; i < len; i++) {
        array[i] = (char) pgm_read_byte_near(progMemArray + i);
      }

      array[len] = '\0';

      return array;
    }

    static char* getCharArrayFromInteger(char array[], int value) {
      itoa(value, array, 10);

      return array;
    }
};

#endif
