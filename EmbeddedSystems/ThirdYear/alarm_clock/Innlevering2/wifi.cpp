#ifndef INNLEVERING2_WIFI_CPP
#define INNLEVERING2_WIFI_CPP

/**
 * Class definitions for the ESP8266 wifi module.
 *
 * @author Anders Mikkelsen
 * @version 14.11.2015
 */

#include "wifi.h"

bool Wifi::startWifi() {
  String ssid = Utils::buildStringFromProgMem(LOCAL_SSID);
  String pass = Utils::buildStringFromProgMem(LOCAL_PASS);

  return connectToSSID(ssid, pass);
}

void Wifi::checkTimeSync(Clock& rtcClock, const Screen& screen) {
  if (rtcClock.getCurrentDateTime()->hour() == TIME_SYNCHRONIZATION_HOUR &&
      rtcClock.getCurrentDateTime()->minute() == TIME_SYNCHRONIZATION_MINUTE &&
      rtcClock.getCurrentDateTime()->second() == 0 && screen.isDimmed()) {
    if (isConnected() || startWifi()) rtcClock.adjustFromInternet(fetchTime());
  }
}

bool Wifi::connectToSSID(const String &ssid, const String &pass) {
  prepareDevice();
  if (checkForAlreadyConnected()) return true;
  String wifiDisconnectCommand = Utils::buildStringFromProgMem(WIFI_DISCONNECT_COMMAND);
  String wifiConnectCommand = Utils::buildStringFromProgMem(AT_COMMAND) +
                              createConnectionString(ssid, pass);

  doCommand(COMMAND_TIMEOUT, wifiDisconnectCommand);
  wifiSerial.println(wifiConnectCommand);

  return waitForTimeout(AP_CONNECT_COMMAND_TIMEOUT);
}

void Wifi::prepareDevice() {
  // these are not necessary on an initialized card but a precaution for corret performance
  String modeSelect = Utils::buildStringFromProgMem(MODE_SELECT_COMMAND);
  String muxSelect = Utils::buildStringFromProgMem(CONNECTION_MUX_COMMAND);
  String tcpTransmissionMode = Utils::buildStringFromProgMem(TCP_TRANSMISSION_MODE);

  doCommand(COMMAND_TIMEOUT, modeSelect);
  doCommand(COMMAND_TIMEOUT, muxSelect);
  doCommand(COMMAND_TIMEOUT, tcpTransmissionMode);
}

bool Wifi::doCommand(int timeout, const String &command) {
  String atCommand = Utils::buildStringFromProgMem(AT_COMMAND) + command;

  wifiSerial.println(atCommand);

  return waitForTimeout(timeout);
}

bool Wifi::checkForAlreadyConnected() {
  String connectCommand = Utils::buildStringFromProgMem(AT_COMMAND) +
                          Utils::buildStringFromProgMem(IS_WIFI_CONNECTED_COMMAND);
  wifiSerial.println(connectCommand);

  return waitForTimeout(COMMAND_TIMEOUT, Utils::buildStringFromProgMem(LOCAL_SSID));
}

String Wifi::createConnectionString(const String &ssid, const String &pass) {
  String temp = Utils::buildStringFromProgMem(WIFI_CONNECT_COMMAND);

  return temp + "\"" + ssid + "\",\"" + pass + "\"";
}

bool Wifi::waitForTimeout(int timeout, const String &command) {
  String response = "";
  bool success = false;
  unsigned long timeoutThreshold = millis() + timeout;
  
  while (wifiSerial.available() || millis() < timeoutThreshold) {
    if (wifiSerial.available()) {
      char c = (char) wifiSerial.read();
      response = response + c;
    }

    if (!success && response.indexOf(command) != -1) success = true;
  }

  return success;
}

String Wifi::fetchTime() {
  String time = "";
  String timeCommand = Utils::buildStringFromProgMem(AT_COMMAND) +
                       Utils::buildStringFromProgMem(NTP_TCP_CONNECTION);
  unsigned long timer = millis() + AP_CONNECT_COMMAND_TIMEOUT;

  wifiSerial.println(timeCommand);

  while (millis() < timer) {
    if (wifiSerial.available()) {
      char c = (char) wifiSerial.read();
      time = time + c;
    }
  }

  int firstDateSeperator = time.indexOf('-');
  return time.substring((unsigned int) (firstDateSeperator - 2),
                        (unsigned int) (time.indexOf("CLOSED") - 2));
}

#endif
