#ifndef INNLEVERING1_MAXUTILITIES_H
#define INNLEVERING1_MAXUTILITIES_H

/**
 * This header defines all pins and actions for the MAX7219. Methods will be called often and are small so are 
 * left in a header for inlining.
 * 
 * @author Anders Mikkelsen
 * @version 04.10.2015
 */
 
// CS of 7912
static const int maxLoadPin = 9;
// CLK of 7912
static const int maxClockPin = 8;
// DIN of 7912
static const int maxDataPin = 7;

// from LedControl.cpp https://github.com/johnmccombs/arduino-libraries/blob/master/LedControl/LedControl.cpp

#define OP_DECODEMODE  9
#define OP_INTENSITY   10
#define OP_SCANLIMIT   11
#define OP_SHUTDOWN    12
#define OP_DISPLAYTEST 15

static void sendDoubleByteToMax(byte registerCode, byte value) {
  digitalWrite(maxLoadPin, LOW);
  shiftOut(maxDataPin, maxClockPin, MSBFIRST, registerCode);
  shiftOut(maxDataPin, maxClockPin, MSBFIRST, value);
  digitalWrite(maxLoadPin, HIGH);  
}

static void setMatrixOperational(byte value) {
  sendDoubleByteToMax(OP_DISPLAYTEST, 0);
  sendDoubleByteToMax(OP_SHUTDOWN, value);
}

static void setMatrixIntensity(byte value) {
  sendDoubleByteToMax(OP_INTENSITY, value);
}

static void setMatrixScanLimit(byte value) {
  sendDoubleByteToMax(OP_SCANLIMIT, value);
}

static void setMatrixDecodeMode(byte value) {
  sendDoubleByteToMax(OP_DECODEMODE, value);
}

static void clearMatrix() {
  for (int i = 1; i <= 8; i++) {
    sendDoubleByteToMax(i, 0);  
  }
}

static void setUpMatrix() {
  pinMode(maxLoadPin, OUTPUT);
  pinMode(maxClockPin, OUTPUT);
  pinMode(maxDataPin, OUTPUT);

  setMatrixOperational(1);
  setMatrixScanLimit(7);
  setMatrixDecodeMode(0);
  setMatrixIntensity(15);
  clearMatrix(); 
}

#endif
