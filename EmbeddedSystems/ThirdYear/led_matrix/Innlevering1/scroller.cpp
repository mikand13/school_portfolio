#include "scroller.h"
#include "hcUtilities.h"
#include "maxUtilities.h"
#include "mode.h"

void Scroller::startScroller() {
  timeSinceLastScroll = millis();  
}

void Scroller::doScroll(long currentTime) {
  //analogWrite(shiftRegisterOutputPin, 0); // attempt at solving red spam issue
  
  scrollSongText();
  timeSinceLastScroll = currentTime;

  //analogWrite(shiftRegisterOutputPin, 255); // attempt at solving red spam issue
}

const int Scroller::getTimeSinceLastScroll() const {
  return timeSinceLastScroll;  
}
          
void Scroller::updateRedColumn() {
  redCounter *= 2;

  if (redCounter > 128) {
    redCounter = 1;
  }
}

void Scroller::writeRedColumns() {  
  writeToShiftRegister(redCounter ^ 255); 
}
        
void Scroller::checkIfPastCurrentCharacter() {
  if (curCharBit > pgm_read_byte_near(lentbl_S + (curChar - 32))) {
    curCharBit = 0;
    curCharIx += 1;

    if (curCharIx + 1 > msgSize) {
      reverse = !reverse;
      curCharIx = 0;
    } 
    
    curChar = msgFromSong[curCharIx];
  }  
}

void Scroller::advanceBitAndValidate() {
  curCharBit++;
  checkIfPastCurrentCharacter();   
}

void Scroller::shift(int row, int column, byte* outputByte) {
  byte currentCharBits = pgm_read_byte_near(Font + (((curChar - 32) * 8) + row));

  if (currentCharBits & (1 << curCharBit)) *outputByte |= (1 << column);
  advanceBitAndValidate();  
}

void Scroller::shiftLeft(int row, byte* outputByte) {
  for (int k = 0; k <= ledColumns - 1; k++) {
    shift(row, k, outputByte);
  }
}

void Scroller::shiftRight(int row, byte* outputByte) {
  for (int k = ledColumns - 1; k >= 0; k--) {
    shift(row, k, outputByte);
  }  
}

void Scroller::setCharacter() {
  curChar = msgFromSong[curCharIx];
}

void Scroller::scrollSongText() {
  curCharIxSave2 = curCharIx;
  curCharBitSave2 = curCharBit;

  writeRedColumns();

  for (int j = 0; j < ledRows; j++) {     
    byte outputByte = 0;
  
    setCharacter();
  
    curCharIxSave = curCharIx;
    curCharBitSave = curCharBit;

    if (reverse) {
      shiftLeft(j, &outputByte);
    } else {
      shiftRight(j, &outputByte);
    }

    sendDoubleByteToMax(j + 1, outputByte);
    
    curCharIx = curCharIxSave;
    curCharBit = curCharBitSave;
  }

  curCharIx = curCharIxSave2;
  curCharBit = curCharBitSave2;

  setCharacter();
  advanceBitAndValidate();

  updateRedColumn();
}
