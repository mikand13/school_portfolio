#ifndef INNLEVERING2_SCREEN_H
#define INNLEVERING2_SCREEN_H

// arduino libs
#include <Arduino.h>  
#include <Adafruit_ST7735.h> 

// own libs
#include "clock.h"
#include "utils.h"

// pins
#define TFT_CS  10
#define TFT_DC   9
#define TFT_RST  8
#define TFT_BCL  5

// defaults
#define MAX_SCREEN_DIM 255
#define ROTATION_PORTRAIT 0
#define ROTATION_LANDSCAPE 1
#define ROTATION_PORTRAIT_INVERTED 2
#define ROTATION_LANDSCAPE_INVERTED 3

// time
#define SECONDS_IN_A_DAY 86400L
#define MINUTES_PULSE_RATIO 64
#define SCREEN_DIM_INTERVALL 1

// font
#define FONT_LENGTH_MULTIPLER 5
#define FONT_HEIGHT_MULTIPLIER 8
#define ALARM_FONT_SIZE 5
#define ALARM_NOTIFICATION_FONT_SIZE 1
#define ALARM_NOTIFICATION_SHOW_ALARM_FONT_SIZE 2
#define ALARM_SET_NOTIFICATION_FONT_SIZE 2
#define WIFI_CONNECT_FONT_SIZE 1

// views
const char CONNECT_TO_WIFI[] PROGMEM  = { "Fetching time from NIST!" };
const char ALARM[] PROGMEM  = { "ALARM" };
const char ALARM_NOTIFICATION[] PROGMEM  = { "Alarm set:" };
const char ALARM_SET_NOTIFICATION[] PROGMEM  = { "Set Alarm:" };
const char ALARM_SET_REMOTE_NOTIFICATION[] PROGMEM  = { "Ok: Set alarm" };
const char ALARM_SET_OR_CLEAR_NOTIFICATION[] PROGMEM  = { "Ok: Set / Cancel: Clear" };

/**
 * This header declares the logic for ST7735. It is a facade for the screens options and also
 * directly implements the appropriate views, as they are tightly couply to tft logic.
 *
 * @author Anders Mikkelsen
 * @version 14.11.2015
 */
class Screen {
  public:
    Screen() :
      tft(Adafruit_ST7735(TFT_CS, TFT_DC, TFT_RST)),
      wrappingEnabled(false),
      currentBackground(ST7735_BLACK), currentTextColor(ST7735_WHITE),
      currentRotation(ROTATION_LANDSCAPE), currentBacklight(MAX_SCREEN_DIM),
      dimTimer(0), dimming(false), dimUp(false), dimStart(0), dimFinish(0) {}

    bool startScreen() {return initialize(); }
    const int getWidth() const { return tft.width(); }
    const int getHeight() const { return tft.height(); }
    void dimScreen(bool on);
    bool isDimmed() const { return currentBacklight == 0; }
    bool isDimming() const { return dimming; }
    void setBackground(int color);
    void resetBackground() { fillBackground(); }
    void clearScreen();
    void invert(bool invert) { tft.invertDisplay(invert); }
    void setLineWrap(bool enabled) { tft.setTextWrap(wrappingEnabled = enabled); }
    void setRotation(int rotation);
    void updateDim(const unsigned long &current);

    // displays
    void writeTimePackToScreen(
            const TimePack &timePack, const String &alarm, bool alarmEnabled = false);
    void writeWifiConnect();
    void writeAlarmSetScreen(const String &alarm);
    void clearAlarm(const String &alarm);
  private:
    Adafruit_ST7735 tft;
    int currentBackground;
    int currentTextColor;
    int currentRotation;
    bool wrappingEnabled;
    int currentBacklight;
    unsigned long dimTimer;
    bool dimming;
    bool dimUp;
    int dimStart;
    int dimFinish;
    
    bool initialize();
    int getTimeAsColor(int hours, int minutes, int seconds);
    int calculateMinutesPulse(int minutes) const;
    void fillBackground();
    void clearRect(int upperX, int upperY, int lowerX, int lowerY);
    void writeString(const String &output, int x = 0, int y = 0, int fontSize = 1,
                     bool centered = false);
    byte adjustFontSize(const String &output, int x, int fontSize);
    int centerizeX(int outputLength, int x) const;
    int outputLengthAsPixelLength(const String &output, int fontSize) const;
    void setScreenBacklight(int strength) { analogWrite(TFT_BCL, currentBacklight = strength); }
};

#endif
