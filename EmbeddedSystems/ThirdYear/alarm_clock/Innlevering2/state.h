#ifndef INNLEVERING2_STATE_H
#define INNLEVERING2_STATE_H


/**
 * This header declares the globals required to control state.
 *
 * @author Anders Mikkelsen
 * @version 14.11.2015
 */

// state
State state = RUNNING;

// state variables
bool newTime = false,
     alarmTriggered = false,
     changedAlarm = true,
     alarmFlash = false;

int alarmOnTimer = 0;

#endif
