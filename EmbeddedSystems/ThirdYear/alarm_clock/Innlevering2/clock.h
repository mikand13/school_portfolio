#ifndef INNLEVERING2_CLOCK_H
#define INNLEVERING2_CLOCK_H

// arduino libs
#include <Arduino.h>
#include <RTClib.h>
#include <avr/pgmspace.h>

// own libs
#include "utils.h"

// NIST TIME FORMAT: JJJJJ YR-MO-DA HH:MM:SS TT L H msADV UTC(NIST) OTM
#define YEAR_START 0
#define YEAR_END 2
#define MONTH_START 3
#define MONTH_END 5
#define DAY_START 6
#define DAY_END 8
#define HOUR_START 9
#define HOUR_END 11
#define MINUTE_START 12
#define MINUTE_END 14
#define SECOND_START 15
#define SECOND_END 17
#define MIN_SIZE 23

// locale adjustment
#define LOCALE_ADJUST 1

// TimePack abstracts DateTime
typedef struct TimePack {
  String dateNow;
  String dayNow;
  String timeNow;
  int hours;
  int minutes;
  int seconds;

  TimePack(String *dateNow, String *dayNow, String *timeNow, int hours, int minutes, int seconds) :
      dateNow(*dateNow), dayNow(*dayNow), timeNow(*timeNow),
      hours(hours), minutes(minutes), seconds(seconds) {}
};


const char SUNDAY[] PROGMEM = "Sunday";
const char MONDAY[] PROGMEM = "Monday";
const char TUESDAY[] PROGMEM = "Tuesday";
const char WEDNESDAY[] PROGMEM = "Wednesday";
const char THURSDAY[] PROGMEM = "Thursday";
const char FRIDAY[] PROGMEM = "Friday";
const char SATURDAY[] PROGMEM = "Saturday";
const char *const daysOfTheWeek[] PROGMEM =
        { SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY};

/**
 * This header declares the logic for DS1307 rtc.
 *
 * @author Anders Mikkelsen
 * @version 14.11.2015
 */
class Clock {
  public:
    Clock() {}

    bool startClock();
    void adjustFromInternet(String timeAsString);
    bool updateTime();
    TimePack getCurrentTime();
    const DateTime* getCurrentDateTime() const { return &oldNow; }
  private:
    RTC_DS1307 rtc;
    DateTime oldNow;
    char weekDayBuffer[10];

    bool initialize() { return rtc.begin(); }
    bool checkRunning() { return rtc.isrunning(); }
    const DateTime* getNow() const { return &oldNow; }
    bool isDST(int day, int month) const;
};

#endif
