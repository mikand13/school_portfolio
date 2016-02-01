#include "animator.h"

void Animator::lcdRamp (int value) {  
  lcd.clear();  

  if (value > 16) {
    value = 16;
  } else if (value < 0) {
    value = 0;
  }

  for (int i = 0; i < value; i++){
    lcd.setCursor(i, 1);
    
    if (i == 0) {
      lcd.write(i);
    } else if (i > 8) {
      lcd.write(7);

      lcd.setCursor(i, 0);
      lcd.write(i - 9);
    } else {
      lcd.write(i - 1);
    }
  }
} 

void Animator::makeLcdChars(byte array[8][8]) {
  for (int i = 0; i < 8; i++) {
    lcd.createChar(i, array[i]);
  }
}

void Animator::setUpLcd() {
  lcd.begin(16, 2);
  lcd.init();
  lcd.backlight();
  
  // set up ramp
  makeLcdChars(ramp); 
}
