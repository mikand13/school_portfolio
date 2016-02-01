#ifndef INNLEVERING2_UTILS_H
#define INNLEVERING2_UTILS_H

// arduino libs
#include <Arduino.h>

/**
 * This header declares any util functions that dont fit anywhere else.
 *
 * @author Anders Mikkelsen
 * @version 14.11.2015
 */
class Utils {
  public:
    static String buildStringFromProgMem(const char stringFromProgMem[]) {
      String temp = "";
            
      for (unsigned int i = 0; i < strlen_P(stringFromProgMem); i++) {
        temp += (char) pgm_read_byte_near(stringFromProgMem + i);
      }

      return temp;
    }

    static String formatTime(const int originalTime) {
      String zero = "0";
      return originalTime < 10 ? String(zero + originalTime) : String(originalTime);
    }
};

#endif
