// © 2025 SC5K Systems

#include "src/sponsor_scroller.h"
#include "src/globals.h"
#include "src/matrix_config.h"
#include "src/helpers.h"
#include <Arduino.h>
#include <string.h>

// drawStaticHeader: draw "SPON-" and "-SORS" header plus divider line
void drawStaticHeader() {
  uint16_t white = color565(255,255,255);
  const char *L1 = "SPON-", *L2 = "-SORS";
  int16_t x1 = (matrix.width() - strlen(L1) * CHAR_W) / 2;
  int16_t x2 = (matrix.width() - strlen(L2) * CHAR_W) / 2;
  matrix.setTextColor(white);
  matrix.setCursor(x1, 0);
  matrix.print(L1);
  matrix.setCursor(x2, CHAR_H);
  matrix.print(L2);
  for (int16_t x = 0; x < matrix.width(); x += 4) {
    matrix.drawFastHLine(x, SEP_Y, 2, white);
  }
}

// wheel: map 0–255 to an RGB565 rainbow colour
uint16_t wheel(uint8_t pos) {
  if (pos < 85) {
    return color565(255 - pos*3, 0, pos*3);
  } else if (pos < 170) {
    pos -= 85;
    return color565(0, pos*3, 255 - pos*3);
  } else {
    pos -= 170;
    return color565(pos*3, 255 - pos*3, 0);
  }
}

// initSponsorScroller: seed random, pick sponsor, random x & hue, start below screen
void initSponsorScroller() {
  randomSeed(analogRead(A0));
  currentSponsor = 0;
  sponsorX       = random(0, matrix.width() - CHAR_W + 1);
  hueOffset      = random(0, 256);
  yOffset        = matrix.height();
}

// runSponsorScroller: scroll current sponsor upward with colour cycle then advance
void runSponsorScroller() {
  matrix.fillScreen(0);
  const char* txt = sponsors[currentSponsor];
  uint8_t len = strlen(txt);
  uint16_t col = wheel(hueOffset);
  for (uint8_t i = 0; i < len; i++) {
    matrix.drawChar(
      sponsorX,
      yOffset + i*CHAR_H,
      txt[i], col, 0, 1
    );
  }
  matrix.fillRect(0, 0, matrix.width(), SEP_Y+1, 0);
  drawStaticHeader();
  matrix.show();

  yOffset   -= SCROLL_SPEED;
  hueOffset += HUE_DELTA;
  if (yOffset + len*CHAR_H < 0) {
    currentSponsor = (currentSponsor + 1) % sponsorCount;
    sponsorX       = random(0, matrix.width() - CHAR_W + 1);
    hueOffset      = random(0, 256);
    yOffset        = matrix.height();
  }
  delay(FRAME_DELAY);
}
