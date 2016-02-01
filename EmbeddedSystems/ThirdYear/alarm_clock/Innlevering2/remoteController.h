#ifndef INNLEVERING2_REMOTE_CONTROLLER_H
#define INNLEVERING2_REMOTE_CONTROLLER_H

// arduino libs
#include <Arduino.h>
#include <IRLib.h>

// pins
#define CONTROLLER_PIN 0

// Remote Controller Keys
#define UP 1587632295
#define DOWN 1587664935
//#define LEFT 1052880210
//#define RIGHT 1052863890
#define OK 1587641220
#define CANCEL 1587631530
//#define ONE 4269836917
//#define TWO 4269820597
//#define THREE 4269853237
//#define FOUR 4269812437
//#define FIVE 4269845077
//#define SIX 4269828757
//#define SEVEN 4269861397
//#define EIGHT 4269808357
//#define NINE 4269840997
//#define ZERO 4269824677
#define REPEAT_VALUE 4294967295

// checking interval
#define CHECK_FOR_REMOTE_INTERVAL 200

// remote controller commands
#define ALARM_SET_COMMAND 0
#define RESET_TO_RUNNING_COMMAND 1
#define RESET_TO_RUNNING_FROM_ALARM_COMMAND 2
#define CANCEL_FROM_RUNNING_COMMAND 3
#define ALARM_SET_UP_COMMAND 4
#define ALARM_SET_DOWN_COMMAND 5
#define ALARM_SET_OK_COMMAND 6
#define ALARM_SET_CANCEL_COMMAND 7
#define NO_COMMAND 100

// must be declared here as first header
enum State { RUNNING, ALARM_SET, ALARM_PLAYING, RESTING };

/**
 * This header declares the logic for the remote controller. It is a facade for the IRlib library.
 * It also converts RC values into commands for the program.
 *
 * @author Anders Mikkelsen
 * @version 14.11.2015
 */
class RemoteController {
  public:
    RemoteController() :
            receiver(IRrecv(CONTROLLER_PIN)),
            remoteControllerTimer(0),
            previousRemoteValue(0) {}
    
    void startController();
    byte getCommand(const unsigned long &current, const State &state);
  private:
    IRrecv receiver;
    IRdecodeNEC decoder;
    unsigned long remoteControllerTimer;
    unsigned long previousRemoteValue;

    unsigned long getValue();
    byte handleValue(const unsigned long &value, const State &state);
    byte handleAlarmValue(const unsigned long &current, const unsigned long &value);
};

#endif

/* THIS IS FOR THE ONE IN THE KIT, ITS CRAP
#define UP 16736925
#define DOWN 16754775
#define LEFT 16720605
#define RIGHT 16761405
#define OK 16712445
#define ONE 16738455
#define TWO 16750695
#define THREE 16756815
#define FOUR 16724175
#define FIVE 16718055
#define SIX 16743045
#define SEVEN 16716015
#define EIGHT 16726215
#define NINE 16734885
#define ZERO 16730805
#define STAR 16728765
#define SQUARE 16732845
*/
