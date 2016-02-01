#ifndef INNLEVERING1_ANIMATOR_H
#define INNLEVERING1_ANIMATOR_H

#include <Arduino.h>
#include <LiquidCrystal_I2C.h>

/**
 * This class defines the ramp animator for music. It initializes the LiquidCrystal_I2C lib
 * and accepts commands for ramping from 0 to 16. We default any values outside 0 - 16 to
 * 0 or 16 respectively.
 * 
 * @author Anders Mikkelsen
 * @version 04.10.2015
 */
class Animator {
  public:
    void setUpLcd();
    void lcdRamp (int value);
  private:
    void makeLcdChars(byte array[8][8]);

    LiquidCrystal_I2C lcd = LiquidCrystal_I2C(0x3F, 16, 2);

    byte ramp[8][8] = { 
      { 0b00000, 0b00000, 0b00000, 0b00000, 0b00000, 0b00000, 0b00000, 0b11111 }, 
      { 0b00000, 0b00000, 0b00000, 0b00000, 0b00000, 0b00000, 0b11111, 0b11111 }, 
      { 0b00000, 0b00000, 0b00000, 0b00000, 0b00000, 0b11111, 0b11111, 0b11111 }, 
      { 0b00000, 0b00000, 0b00000, 0b00000, 0b11111, 0b11111, 0b11111, 0b11111 }, 
      { 0b00000, 0b00000, 0b00000, 0b11111, 0b11111, 0b11111, 0b11111, 0b11111 }, 
      { 0b00000, 0b00000, 0b11111, 0b11111, 0b11111, 0b11111, 0b11111, 0b11111 }, 
      { 0b00000, 0b11111, 0b11111, 0b11111, 0b11111, 0b11111, 0b11111, 0b11111 }, 
      { 0b11111, 0b11111, 0b11111, 0b11111, 0b11111, 0b11111, 0b11111, 0b11111 } 
    };
};

#endif
