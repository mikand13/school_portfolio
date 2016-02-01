// arduino libs
#include <Wire.h>
#include <SPI.h>
#include <SoftwareSerial.h>
#include <RTClib.h>
#include <Adafruit_GFX.h>
#include <Adafruit_ST7735.h>
#include <IRLib.h>

// own libs
#include "utils.h"
#include "clock.h"
#include "alarm.h"
#include "screen.h"
#include "accelerometer.h"
#include "humanDetector.h"
#include "wifi.h"
#include "remoteController.h"
#include "state.h"

// thresholds
#define DIM_SCREEN_THRESHOLD 10000
#define ALARM_OFF_THRESHOLD 300

// components
Clock rtcClock;
Alarm alarm;
Screen screen;
Accelerometer accelerometer;
HumanDetector humanDetector;
RemoteController remoteController;
Wifi wifi;

void setup() {
  rtcClock.startClock();
  screen.startScreen();
  accelerometer.startAccelerometer();
  remoteController.startController();

  if (connectWifi()) wifi.checkTimeSync(rtcClock, screen);
}

bool connectWifi() {
  screen.writeWifiConnect();
  bool result = wifi.startWifi();
  screen.clearScreen();

  return result;
}

void loop() {
  unsigned long current = millis();

  newTime = rtcClock.updateTime();
  if (newTime) alarmTriggered = checkAlarmOnClock(*rtcClock.getCurrentDateTime());

  switch (state) {
    case RUNNING:
      runningClock(current);
      break;
    case ALARM_SET:
      alarmSet(current);
      break;
    case ALARM_PLAYING:
      alarmPlaying();
      break;
    case RESTING:  
      restMode(current);
      break;
  }
  
  checkRemoteController(current);
  screen.updateDim(current);
  newTime = alarmTriggered = false;
}

inline void runningClock(const unsigned long &current) {
  if (newTime) {
    accelerometer.checkAndApplyRotation(screen);

    if (alarmTriggered) {
      screen.resetBackground();
      transitionToAlarm();
    } else {
      printNormalViewToScreen();
    }
  }

  checkForHumanAndRestMode(current);
}

inline void alarmSet(const unsigned long &current) {
  if (newTime) {
    bool rotated = accelerometer.checkAndApplyRotation(screen);
    if (rotated && !changedAlarm) changedAlarm = rotated;
  }

  if (changedAlarm) {
    screen.writeAlarmSetScreen(alarm.getAlarmAsString());
    changedAlarm = false;
  }

  checkForHumanAndRestMode(current);
}

inline void alarmPlaying() {
  if (newTime) {
    accelerometer.checkAndApplyRotation(screen);
    printAlarmViewToScreen();
    screen.invert(alarmFlash = !alarmFlash);

    if (alarmOnTimer < ALARM_OFF_THRESHOLD) {
      alarm.playAlarmTone();
      alarmOnTimer++;
    } else if (alarmOnTimer >= ALARM_OFF_THRESHOLD) {
      resetToRunningFromAlarm();
    }
  }
}

inline void restMode(const unsigned long &current) {
  if (!screen.isDimming() && humanDetector.checkForAvailableHuman(current)) resetScreenToRunning();

  if (alarmTriggered) {
    transitionToAlarm();
    screen.dimScreen(true);
  }

  if (state == RESTING) wifi.checkTimeSync(rtcClock, screen);
}

inline void checkForHumanAndRestMode(const unsigned long &current) {
  if (!screen.isDimming()) humanDetector.checkForAvailableHuman(current);
  checkForRestMode();
}

inline void checkForRestMode() {
  if (humanDetector.getHumanUnavailableCount() >= DIM_SCREEN_THRESHOLD) {
    if (state == ALARM_SET) screen.clearScreen();

    screen.dimScreen(false);
    state = RESTING;
    humanDetector.setHumanAvailable();
  }
}

inline void printAlarmViewToScreen() {
  screen.writeTimePackToScreen(rtcClock.getCurrentTime(), "", true);
}

inline void printNormalViewToScreen() {
  screen.writeTimePackToScreen(rtcClock.getCurrentTime(), alarm.getAlarmAsString());
}

inline void transitionToAlarm() {
  printAlarmViewToScreen();
  state = ALARM_PLAYING;
}

inline void resetToRunningFromAlarm() {
  setRunningView();
  state = RUNNING;
}

inline void resetToRunningFromAlarmSet() {
  screen.clearScreen();
  screen.writeTimePackToScreen(rtcClock.getCurrentTime(), alarm.getAlarmAsString());
  state = RUNNING;
}

inline void resetScreenToRunning() {
  accelerometer.checkAndApplyRotation(screen);
  setRunningView();
  screen.dimScreen(true);
  state = RUNNING;
}

inline void setRunningView() {
  if (state == ALARM_PLAYING || state == ALARM_SET) screen.clearScreen();
  screen.writeTimePackToScreen(rtcClock.getCurrentTime(), alarm.getAlarmAsString());
}

inline bool checkAlarmOnClock(const DateTime &currentDateTime) {
  return alarm.isSet() && alarm.checkAlarm(currentDateTime);
}

inline void checkRemoteController(const unsigned long &current) {
  byte command = remoteController.getCommand(current, state);

  if (command != NO_COMMAND) {
    humanDetector.setHumanAvailable();

    switch (command) {
      case ALARM_SET_COMMAND:
        screen.clearScreen();
        if (!alarm.isSet()) alarm.setAlarm(DateTime(rtcClock.getCurrentDateTime()->unixtime()));
        screen.writeAlarmSetScreen(alarm.getAlarmAsString());
        state = ALARM_SET;
        break;
      case RESET_TO_RUNNING_COMMAND:
        resetScreenToRunning();
        break;
      case RESET_TO_RUNNING_FROM_ALARM_COMMAND:
        resetToRunningFromAlarm();
        alarmOnTimer = 0;
        break;
      case CANCEL_FROM_RUNNING_COMMAND:
        alarm.clearAlarm();
        screen.clearAlarm(alarm.getAlarmAsString());
        break;
      case ALARM_SET_UP_COMMAND:
        alarm.adjustAlarm(current, true);
        changedAlarm = true;
        break;
      case ALARM_SET_DOWN_COMMAND:
        alarm.adjustAlarm(current, false);
        changedAlarm = true;
        break;
      case ALARM_SET_OK_COMMAND:
        resetToRunningFromAlarmSet();
        break;
      case ALARM_SET_CANCEL_COMMAND:
        alarm.clearAlarm();
        resetToRunningFromAlarmSet();
        break;
    }
  }
}