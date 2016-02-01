#ifndef INNLEVERING2_REMOTE_CONTROLLER_CPP
#define INNLEVERING2_REMOTE_CONTROLLER_CPP

/**
 * Class definitions for the remote controller.
 *
 * @author Anders Mikkelsen
 * @version 14.11.2015
 */

#include "remoteController.h"

void RemoteController::startController() {
  pinMode(CONTROLLER_PIN, INPUT);
  receiver.enableIRIn();
}

byte RemoteController::getCommand(const unsigned long &current, const State &state) {
  unsigned long result = getValue();

  if(result > 0) {
    if (result == REPEAT_VALUE && previousRemoteValue != OK) result = previousRemoteValue;
    previousRemoteValue = result;

    switch (state) {
      case RUNNING:
      case RESTING:
      case ALARM_PLAYING:
        return handleValue(result, state);
      case ALARM_SET:
        return handleAlarmValue(current, result);
    }
  }

  return NO_COMMAND;
}

byte RemoteController::handleValue(const unsigned long &value, const State &state) {
  switch (value) {
    case OK:
      switch (state) {
        case RUNNING:
          return ALARM_SET_COMMAND;
        case RESTING:
          return RESET_TO_RUNNING_COMMAND;
        case ALARM_PLAYING:
          return RESET_TO_RUNNING_FROM_ALARM_COMMAND;
        default:
          return NO_COMMAND;
      }
    case CANCEL:
      if (state == RUNNING) {
        return CANCEL_FROM_RUNNING_COMMAND;
      } else {
        return NO_COMMAND;
      }
    default:
      return NO_COMMAND;
  }
}

byte RemoteController::handleAlarmValue(const unsigned long &current, const unsigned long &value) {
  switch (value) {
    case UP:
      if (current - remoteControllerTimer > CHECK_FOR_REMOTE_INTERVAL) {
        remoteControllerTimer = current;
        return ALARM_SET_UP_COMMAND;
      } else {
        return NO_COMMAND;
      }
    case DOWN:
      if (current - remoteControllerTimer > CHECK_FOR_REMOTE_INTERVAL) {
        remoteControllerTimer = current;
        return ALARM_SET_DOWN_COMMAND;
      } else {
        return NO_COMMAND;
      }
    case OK:
      return ALARM_SET_OK_COMMAND;
    case CANCEL:
      return ALARM_SET_CANCEL_COMMAND;
    default:
      return NO_COMMAND;
  }
}

inline unsigned long RemoteController::getValue() {
  if (receiver.GetResults(&decoder)) {
    decoder.decode();
    receiver.resume();

    return decoder.value;
  }

  return 0;
}

#endif
