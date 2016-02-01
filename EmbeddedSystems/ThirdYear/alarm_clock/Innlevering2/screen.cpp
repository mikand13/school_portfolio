#ifndef INNLEVERING2_SCREEN_CPP
#define INNLEVERING2_SCREEN_CPP

/**
 * Class definitions for the ST7735 TFT screen.
 *
 * @author Anders Mikkelsen
 * @version 14.11.2015
 */

#include "screen.h"

void Screen::dimScreen(bool on) { 
  dimming = true;
  dimUp = on;
  dimStart = on ? (currentBacklight != 0 ? currentBacklight : 0) : MAX_SCREEN_DIM;
  dimFinish = on ? MAX_SCREEN_DIM : 0;
}

inline void Screen::setBackground(int color) {
  if (color != currentBackground) {
    currentBackground = color;  
    fillBackground();
  }
}

void Screen::clearScreen() {
  tft.fillScreen(currentBackground);
  tft.invertDisplay(false);
}

void Screen::setRotation(int rotation) {
  clearScreen();
  tft.setRotation(currentRotation = rotation);
}

void Screen::updateDim(const unsigned long &current) {
  if (dimming && current - dimTimer > SCREEN_DIM_INTERVALL) {
    setScreenBacklight(dimUp ? dimStart++ : dimStart--);

    if ((dimUp && dimStart > dimFinish) || (!dimUp && dimStart < dimFinish)) {
      dimStart = dimFinish = 0;
      dimming = dimUp = false;
    }
  }
}

void Screen::writeTimePackToScreen(const TimePack &timePack, const String &alarm,
                                   bool alarmEnabled) {
  setBackground(getTimeAsColor(timePack.hours, 
                               timePack.minutes, 
                               timePack.seconds));
  writeString(timePack.dateNow, 0, 5, 2, true);
  writeString(timePack.dayNow, 0, 25, 2, true);
  writeString(timePack.timeNow, 0, 50, 3, true);

  if (alarmEnabled) {
    int temp = currentTextColor;
    currentTextColor = ST7735_RED;
    String showAlarm = Utils::buildStringFromProgMem(ALARM);

    writeString(showAlarm, 0, 80, ALARM_FONT_SIZE, true);
    currentTextColor = temp;
  } else if (alarm != "") {
    String alarmNotification = Utils::buildStringFromProgMem(ALARM_NOTIFICATION);
    String alarmConfigNotification = Utils::buildStringFromProgMem(ALARM_SET_REMOTE_NOTIFICATION);

    writeString(alarmNotification, 0, 90, ALARM_NOTIFICATION_FONT_SIZE, true);
    writeString(alarm, 0, 100, ALARM_NOTIFICATION_SHOW_ALARM_FONT_SIZE, true);
    writeString(alarmConfigNotification, 0, getHeight() - 10, ALARM_NOTIFICATION_FONT_SIZE, true);
  }
}

void Screen::writeWifiConnect() {
  String connectToWifi = Utils::buildStringFromProgMem(CONNECT_TO_WIFI);

  writeString(connectToWifi, 0, (int) (getHeight() / 2 - 2.5), WIFI_CONNECT_FONT_SIZE, true);
}

void Screen::writeAlarmSetScreen(const String &alarm) {
  String configOptions = Utils::buildStringFromProgMem(ALARM_SET_OR_CLEAR_NOTIFICATION);
  String alarmSetNotification = Utils::buildStringFromProgMem(ALARM_SET_NOTIFICATION);

  if (currentRotation != ROTATION_PORTRAIT && currentRotation != ROTATION_PORTRAIT_INVERTED) {
    writeString(configOptions, 0, 5, ALARM_SET_NOTIFICATION_FONT_SIZE, true);
  }

  writeString(alarmSetNotification, 0, getHeight() / 4, ALARM_SET_NOTIFICATION_FONT_SIZE, true);
  writeString(alarm, 0, getHeight() / 2, ALARM_SET_NOTIFICATION_FONT_SIZE, true);
}

void Screen::clearAlarm(const String &alarm) {
  clearRect(0, 80, tft.width(), tft.height());

  if (alarm != "") {
    String alarmNotification = Utils::buildStringFromProgMem(ALARM_NOTIFICATION);

    writeString(alarmNotification, 0, 90, ALARM_NOTIFICATION_FONT_SIZE, true);
    writeString(alarm, 0, 100, ALARM_NOTIFICATION_SHOW_ALARM_FONT_SIZE, true);
  }
}

void Screen::writeString(const String &output, int x, int y, int fontSize, bool centered) {
  int adjustedFontSize = !wrappingEnabled ? adjustFontSize(output, x, fontSize) : fontSize;
  int outputLength = outputLengthAsPixelLength(output, adjustedFontSize);
  int newX = centered ? centerizeX(outputLength, x) : x;

  tft.fillRect(0, y, newX, adjustedFontSize * FONT_HEIGHT_MULTIPLIER, currentBackground);
  tft.fillRect(getWidth() - (newX == x ? getWidth() - outputLength : newX), y,
               getWidth(), adjustedFontSize * FONT_HEIGHT_MULTIPLIER, currentBackground);
  
  tft.setCursor(newX, y);
  tft.setTextSize(adjustedFontSize);
  tft.setTextColor(currentTextColor, currentBackground);
  tft.print(output);
}

bool Screen::initialize() {
  tft.initR(INITR_BLACKTAB);
  tft.fillScreen(currentBackground);
  tft.setTextColor(ST7735_WHITE); 
  setRotation(ROTATION_LANDSCAPE);
  setLineWrap(false);
  pinMode(TFT_BCL, OUTPUT);
  setScreenBacklight(255);
  
  return true;
}

int Screen::getTimeAsColor(int hours, int minutes, int seconds) {
  int colorByHourOfDay =
          (int) map((hours * 3600L) + (minutes * 60) + seconds, 0, SECONDS_IN_A_DAY, 0, 255);
  
  return tft.Color565(colorByHourOfDay, calculateMinutesPulse(minutes), 255 - colorByHourOfDay);  
}

int Screen::calculateMinutesPulse(int minutes) const {
  if (minutes >= 30) {
    return (int) map(minutes, 30, 59, 0, MINUTES_PULSE_RATIO);
  } else {
    return (int) (MINUTES_PULSE_RATIO - map(minutes, 0, 30, 0, MINUTES_PULSE_RATIO));
  }
}

void Screen::fillBackground() {
  clearRect(0, 0, tft.width(), 5);
  clearRect(0, 20, tft.width(), 5);
  clearRect(0, 41, tft.width(), 9);
  clearRect(0,
            currentRotation == ROTATION_PORTRAIT_INVERTED || currentRotation == ROTATION_PORTRAIT ?
            66 : 74,
            tft.width(), tft.height());
}

byte Screen::adjustFontSize(const String &output, int x, int fontSize) {
  for (int i = fontSize; i > 0; i--) {
    if (x + outputLengthAsPixelLength(output, i) < getWidth()) return i;
  }

  return fontSize;
}

inline void Screen::clearRect(int upperX, int upperY, int lowerX, int lowerY) {
  tft.fillRect(upperX, upperY, lowerX, lowerY, currentBackground);
}

inline int Screen::centerizeX(int outputLength, int x) const {
  return ((getWidth() - x) / 2) - (outputLength / 2);
}

inline int Screen::outputLengthAsPixelLength(const String &output, int fontSize) const {
  return (output.length() * fontSize * FONT_LENGTH_MULTIPLER) + (output.length() * fontSize);
}

#endif
