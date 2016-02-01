#ifndef INNLEVERING2_CLOCK_CPP
#define INNLEVERING2_CLOCK_CPP

/**
 * Class definitions for the clock.
 *
 * @author Anders Mikkelsen
 * @version 14.11.2015
 */

#include "clock.h"

bool Clock::startClock() {
  return initialize() && checkRunning();
}

void Clock::adjustFromInternet(String timeAsString) {
  if (timeAsString.length() >= MIN_SIZE) {
    int year = (int) timeAsString.substring(YEAR_START, YEAR_END).toInt() + 2000;
    int month = (int) timeAsString.substring(MONTH_START, MONTH_END).toInt();
    int day = (int) timeAsString.substring(DAY_START, DAY_END).toInt();

    int hour = (int) timeAsString.substring(HOUR_START, HOUR_END).toInt() + LOCALE_ADJUST;
    int minute = (int) timeAsString.substring(MINUTE_START, MINUTE_END).toInt();
    int second = (int) timeAsString.substring(SECOND_START, SECOND_END).toInt();

    if (isDST(day, month)) hour++;

    rtc.adjust(DateTime(year, month, day, hour, minute, second));
  }
}

// http://stackoverflow.com/questions/5590429/calculating-daylight-savings-time-from-only-date
// adjusted for norwegian time
inline bool Clock::isDST(int day, int month) const {
  return !(month < 3 || month > 10) && (month > 3 && month < 10 || day >= 25);
}

bool Clock::updateTime() {
  if (rtc.now().unixtime() != getNow()->unixtime()) {
    oldNow = rtc.now();
    
    return true;
  }
  
  return false;
}

TimePack Clock::getCurrentTime() {
  const DateTime* currentNow = getNow();
  String date = "";
  String timeNow = "";

  date = date + currentNow->year() + "/" +
         Utils::formatTime(currentNow->month()) + "/" +
         Utils::formatTime(currentNow->day());
  timeNow = Utils::formatTime(currentNow->hour()) + ":" +
            Utils::formatTime(currentNow->minute()) + ":" +
            Utils::formatTime(currentNow->second());

  strcpy_P(weekDayBuffer, (char*) pgm_read_word(&(daysOfTheWeek[currentNow->dayOfTheWeek()])));
  String weekDay = String(weekDayBuffer);

  return TimePack(&date, &weekDay, &timeNow,
                  currentNow->hour(), currentNow->minute(), currentNow->second());
}

#endif
