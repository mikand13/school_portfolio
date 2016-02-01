#ifndef INNLEVERING2_WIFI_H
#define INNLEVERING2_WIFI_H

// arduino libs
#include <Arduino.h>
#include <avr/pgmspace.h>
#include <SoftwareSerial.h>

// own libs
#include "utils.h"
#include "clock.h"
#include "screen.h"

// pins
#define RX_PIN 7
#define TX_PIN 2

// esp8266 config
#define ESP8266_BAUD_RATE 57600
#define COMMAND_TIMEOUT 1000
#define AP_CONNECT_COMMAND_TIMEOUT 5000

// time sync
#define TIME_SYNCHRONIZATION_HOUR 4
#define TIME_SYNCHRONIZATION_MINUTE 0

// esp8266 commands
const char LOCAL_SSID[] PROGMEM  = { "yourssidhere" };
const char LOCAL_PASS[] PROGMEM  = { "yourpasshere" };
const char AT_COMMAND[] PROGMEM = { "AT+" };
const char MODE_SELECT_COMMAND[] PROGMEM  = { "CWMODE=1" };
const char CONNECTION_MUX_COMMAND[] PROGMEM  = { "CIPMUX=0" };
const char TCP_TRANSMISSION_MODE[] PROGMEM  = { "CIPMODE=1" };
const char IS_WIFI_CONNECTED_COMMAND[] PROGMEM  = { "CWJAP?" };
const char WIFI_DISCONNECT_COMMAND[] PROGMEM  = { "CWQAP" };
const char WIFI_CONNECT_COMMAND[] PROGMEM  = { "CWJAP=" };
const char NTP_TCP_CONNECTION[] PROGMEM = { "CIPSTART=\"TCP\",\"129.6.15.30\",13" };

/**
 * This header declares the logic for the ESP8266 for wifi connection and internet communication.
 *
 * @author Anders Mikkelsen
 * @version 14.11.2015
 */
class Wifi {
  public:
    Wifi() : wifiSerial(SoftwareSerial(RX_PIN, TX_PIN)) { wifiSerial.begin(ESP8266_BAUD_RATE); }
    bool startWifi();
    bool isConnected() { return checkForAlreadyConnected(); }
    void checkTimeSync(Clock& rtcClock, const Screen& screen);
  private:
    SoftwareSerial wifiSerial;

    bool connectToSSID(const String &ssid, const String &pass);
    void prepareDevice();
    bool checkForAlreadyConnected();
    String createConnectionString(const String &ssid, const String &pass);
    bool waitForTimeout(int timeout, const String &command = "OK");
    bool doCommand(int timeout, const String &command);

    // time sync
    String fetchTime();
};

#endif
