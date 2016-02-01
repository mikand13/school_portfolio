#ifndef INNLEVERING2_HUMAN_DETECTOR_CPP
#define INNLEVERING2_HUMAN_DETECTOR_CPP

/**
 * Class definitions for the ultrasound proximity sensor.
 *
 * @author Anders Mikkelsen
 * @version 14.11.2015
 */

#include "humanDetector.h"

HumanDetector::HumanDetector() :
        humanCheckTimer(0),
        humanUnavailableCount(0) {
  pinMode(TRIGGER_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT); 
}

bool HumanDetector::checkForHuman() {
  pulseTrigger();
  double distance = calculateDistance(pulseIn(ECHO_PIN, HIGH, SEARCH_TIMEOUT));
  return distance > SEARCH_MIN && distance < SEARCH_MAX;
}

void HumanDetector::pulseTrigger() {
  digitalWrite(TRIGGER_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIGGER_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIGGER_PIN, LOW);
}

bool HumanDetector::checkForHumanInterval(const unsigned long &current) {
  if (current - humanCheckTimer > CHECK_FOR_HUMAN_INTERVAL) {
    humanCheckTimer = current;

    return true;
  }

  return false;
}

bool HumanDetector::checkForAvailableHuman(const unsigned long &current) {
  if (checkForHumanInterval(current)) {
    if (checkForHuman()) {
      humanUnavailableCount = 0;
      return true;
    } else {
      humanUnavailableCount += CHECK_FOR_HUMAN_INTERVAL;
    }
  }

  return false;
}

#endif
