#ifndef INNLEVERING2_ALARM_H
#define INNLEVERING2_ALARM_H

// forward declarations
class DateTime;

// arduino libs
#include <Arduino.h>
#include <RTClib.h>

// own libs
#include "utils.h"

// pins
#define BUZZER_PIN 3

// alarm config
#define ALARM_FREQUENCY 2000
#define ALARM_TONE_DURATION 500

/**
 * This header declares the logic for the alarm. It operates with DateTime from the RTClib.
 *
 * @author Anders Mikkelsen
 * @version 14.11.2015
 */
class Alarm {
  public:
    Alarm() : alarm(NULL) { pinMode(BUZZER_PIN, OUTPUT); }
    ~Alarm() { if (alarm != NULL) { delete alarm; } }

    bool isSet() const { return alarm != NULL; }
    bool checkAlarm(const DateTime &currentTime) const;
    void setAlarm(const DateTime &alarm);
    void adjustAlarm(const unsigned long &current, bool addition);
    void clearAlarm();
    String getAlarmAsString() const;
    void playAlarmTone() const;
  private:
    DateTime *alarm;
};

#endif
