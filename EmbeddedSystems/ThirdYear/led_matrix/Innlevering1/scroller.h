#ifndef INNLEVERING1_SCROLLER_H
#define INNLEVERING1_SCROLLER_H

#include "alphabet.h"

/**
 * This class defines the scroller which handles all scrolling on the led matrix.
 * It controls both green and red columns, as well as rows.
 * 
 * Uses the 74HC595 and the MAX7219.
 * 
 * @author Anders Mikkelsen
 * @version 04.10.2015
 */
class Scroller {
  public:
    void startScroller();  
    void doScroll(long currentTime);   
    const int getTimeSinceLastScroll() const;
  private:
    const int ledRows = 8;
    const int ledColumns = 8;

    int timeSinceLastScroll = 0;
    
    int scrollSpeed = 50;
    boolean reverse = false;
    
    int curCharIx = 0;
    int curCharBit = 0;
    int curCharIxSave = 0;
    int curCharBitSave = 0;
    int curCharIxSave2 = 0;
    int curCharBitSave2 = 0;
    char curChar;
    
    int redCounter = 1;
    
    void updateRedColumn();
    void writeRedColumns();      
    void checkIfPastCurrentCharacter();
    void advanceBitAndValidate();
    void setCharacter();
    void shift(int row, int column, byte* outputByte);
    void shiftLeft(int row, byte* outputByte);
    void shiftRight(int row, byte* outputByte);
    void scrollSongText();
};

#endif
