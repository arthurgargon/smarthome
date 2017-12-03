#ifndef NEO_PIXELS_h
#define NEO_PIXELS_h

#include <Adafruit_NeoPixel.h>

#define LED_PIN 5
#define NUMPIXELS 6

void neopixels_clear(Adafruit_NeoPixel neopixels_strip){
  for (int i=0; i<neopixels_strip.numPixels(); i++){
    neopixels_strip.setPixelColor(i+1, 0);
  }
  neopixels_strip.show();
}

// Input a value 0 to 255 to get a color value.
// The colours are a transition r - g - b - back to r.
uint32_t Wheel(Adafruit_NeoPixel neopixels_strip, byte WheelPos) {
  WheelPos = 255 - WheelPos;
  if(WheelPos < 85) {
    return neopixels_strip.Color(255 - WheelPos * 3, 0, WheelPos * 3);
  }
  if(WheelPos < 170) {
    WheelPos -= 85;
    return neopixels_strip.Color(0, WheelPos * 3, 255 - WheelPos * 3);
  }
  WheelPos -= 170;
  return neopixels_strip.Color(WheelPos * 3, 255 - WheelPos * 3, 0);
}

//Theatre-style crawling lights with rainbow effect
void theaterChaseRainbow(Adafruit_NeoPixel neopixels_strip, uint8_t wait) {
  for (int j=0; j < 256; j++) {     // cycle all 256 colors in the wheel
    for (int q=0; q < 3; q++) {
      for (uint16_t i=0; i < neopixels_strip.numPixels(); i=i+3) {
        neopixels_strip.setPixelColor(i+q, Wheel(neopixels_strip, (i+j) % 255));    //turn every third pixel on
      }
      neopixels_strip.show();

      delay(wait);

      for (uint16_t i=0; i < neopixels_strip.numPixels(); i=i+3) {
        neopixels_strip.setPixelColor(i+q, 0);        //turn every third pixel off
      }
    }
  }
}


// Slightly different, this makes the rainbow equally distributed throughout
void rainbowCycle(Adafruit_NeoPixel neopixels_strip, uint8_t wait) {
  uint16_t i, j;

  for(j=0; j<256*5; j++) { // 5 cycles of all colors on wheel
    for(i=0; i< neopixels_strip.numPixels(); i++) {
      neopixels_strip.setPixelColor(i, Wheel(neopixels_strip, ((i * 256 / neopixels_strip.numPixels()) + j) & 255));
    }
    neopixels_strip.show();
    
    delay(wait);
  }
}

#endif
