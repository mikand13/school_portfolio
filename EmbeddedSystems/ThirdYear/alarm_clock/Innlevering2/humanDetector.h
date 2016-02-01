#ifndef INNLEVERING2_HUMAN_DETECTOR_H
#define INNLEVERING2_HUMAN_DETECTOR_H

// arduino libs
#include <Arduino.h>

// pins
#define TRIGGER_PIN 6
#define ECHO_PIN A3

// detector boundaries
#define SEARCH_MIN 10
#define SEARCH_MAX 500
#define SEARCH_TIMEOUT 5000

// checking intervals
#define CHECK_FOR_HUMAN_INTERVAL 100

/**
 * This header declares the logic for HC-SR04 ultrasound proximity sensor..
 *
 * @author Anders Mikkelsen
 * @version 14.11.2015
 */
class HumanDetector {
  public:
    HumanDetector();

    bool checkForAvailableHuman(const unsigned long &current);
    void setHumanAvailable() { humanUnavailableCount = 0; }
    unsigned long getHumanUnavailableCount() const { return humanUnavailableCount; }
  private:
    unsigned long humanCheckTimer;
    unsigned long humanUnavailableCount;

    bool checkForHumanInterval(const unsigned long &current);
    bool checkForHuman();
    void pulseTrigger();
    double calculateDistance(long timeToBounce) { return (timeToBounce / 2.9) / 2; }
};

#endif
