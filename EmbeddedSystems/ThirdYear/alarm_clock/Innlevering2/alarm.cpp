#ifndef INNLEVERING2_ALARM_CPP
#define INNLEVERING2_ALARM_CPP

/**
 * Class definitions for the alarm.
 *
 * @author Anders Mikkelsen
 * @version 14.11.2015
 */

#include "alarm.h"

bool Alarm::checkAlarm(const DateTime &currentTime) const {
  return currentTime.hour() == alarm->hour() &&
         currentTime.minute() == alarm->minute() &&
         currentTime.second() == alarm->second();
}

void Alarm::setAlarm(const DateTime &newAlarm) {
  if (alarm != NULL) delete alarm;
  alarm = new DateTime(newAlarm.year(), newAlarm.month(), newAlarm.day(),
                       newAlarm.hour(), newAlarm.minute(), 0);
}

void Alarm::clearAlarm() {
  delete alarm; 
  alarm = NULL;
}

void Alarm::playAlarmTone() const {
  noTone(BUZZER_PIN);
  tone(BUZZER_PIN, ALARM_FREQUENCY, ALARM_TONE_DURATION);
}

String Alarm::getAlarmAsString() const {
  return isSet() ? Utils::formatTime(alarm->hour()) + ":" + Utils::formatTime(alarm->minute()) : "";
}

void Alarm::adjustAlarm(const unsigned long &current, bool addition) {
  if (addition) {
    setAlarm(*alarm + TimeSpan(0, 0, 1, 0));
  } else {
    setAlarm(*alarm - TimeSpan(0, 0, 1, 0));
  }
}

#endif
