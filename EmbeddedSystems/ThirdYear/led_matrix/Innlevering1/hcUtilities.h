#ifndef INNLEVERING1_HCUTILITIES_H
#define INNLEVERING1_HCUTILITIES_H

/**
 * This header defines all pins and actions for the 74HC595. Methods will be called often and are small so are 
 * left in a header for inlining.
 * 
 * @author Anders Mikkelsen
 * @version 04.10.2015
 */
 
// DS of 74HC595
static int shiftRegisterDataPin = 10;
// ST_CP of 74HC595
static int shiftRegisterLatchPin = 11;
// SH_CP of 74HC595
static int shiftRegisterClockPin = 12;
// OE of 74HC595
static int shiftRegisterOutputPin = 5;
// MR of 74HC595
static int shiftRegisterMasterResetPin = 6;

static void setUpShiftRegister() {
  pinMode(shiftRegisterLatchPin, OUTPUT);
  pinMode(shiftRegisterClockPin, OUTPUT);
  pinMode(shiftRegisterDataPin, OUTPUT);
  pinMode(shiftRegisterOutputPin, OUTPUT); 
  pinMode(shiftRegisterMasterResetPin, OUTPUT); 

  analogWrite(shiftRegisterOutputPin, 255); 
  analogWrite(shiftRegisterMasterResetPin, 255);  
}

static void writeToShiftRegister(byte value) {
  digitalWrite(shiftRegisterLatchPin, LOW);
  shiftOut(shiftRegisterDataPin, shiftRegisterClockPin, MSBFIRST, value);
  digitalWrite(shiftRegisterLatchPin, HIGH);  
}

#endif
