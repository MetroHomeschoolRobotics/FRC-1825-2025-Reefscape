// © 2025 SC5K Systems

#include "src/perry_loader.h"
#include "src/globals.h"
#include "src/matrix_config.h"
#include <string.h>
#include <Arduino.h>

// initPerryLoader: measure line lengths, reset indices, set timing, center text and clear screen
void initPerryLoader() {
  // reset lengths and decryption state
  for (uint8_t i = 0; i < perryLineCount4; i++) {
    perryLens4[i]     = strlen(perryLines4[i]);
    perryDone[i]      = 0;
    perryWriteIdx4[i] = 0;
    for (uint8_t j = 0; j < perryLens4[i]; j++) {
      perryDec[i][j] = false;
    }
  }
  p_writing     = true;
  p_decrypting  = false;
  p_linesDone   = 0;

  // initialize timing
  unsigned long now = millis();
  p_lastUpdate = now;
  p_lastObf    = now;
  p_startObf   = now + IPAU;
  p_finalHold  = 0;

  // compute vertical centering
  p_lineGap = 4;
  int sh     = matrix.height();
  int totalH = perryLineCount4 * CHAR_H
             + (perryLineCount4 - 1) * p_lineGap;
  p_yStart   = (sh - totalH) / 2;

  matrix.fillScreen(0);
}

// getRandomCharacterP: return a random printable ASCII char (33–126)
char getRandomCharacterP() {
  return (char)random(33, 127);
}

// writeEncryptedText4: type lines one char at a time as random glyphs
void writeEncryptedText4() {
  unsigned long t = millis();
  if (t - p_lastUpdate < WDEL) return;

  int sw = matrix.width();
  // find next char slot
  for (uint8_t i = 0; i < perryLineCount4; i++) {
    if (perryWriteIdx4[i] < perryLens4[i]) {
      int x = (sw - perryLens4[i] * CHAR_W) / 2
            + perryWriteIdx4[i] * CHAR_W;
      int y = p_yStart + i * (CHAR_H + p_lineGap);
      matrix.setCursor(x, y);
      matrix.setTextColor(matrix.color565(255, 215, 0));
      matrix.print(getRandomCharacterP());
      perryWriteIdx4[i]++;
      matrix.show();
      p_lastUpdate = t;
      return;
    }
  }
  // all lines written
  p_writing = false;
}

// displayRandomText4: draw lines mixing decrypted chars (blue) and random glyphs (gold)
void displayRandomText4() {
  matrix.fillScreen(0);
  int sw = matrix.width();

  for (uint8_t i = 0; i < perryLineCount4; i++) {
    int x = (sw - perryLens4[i] * CHAR_W) / 2;
    int y = p_yStart + i * (CHAR_H + p_lineGap);
    matrix.setCursor(x, y);

    for (uint8_t j = 0; j < perryLens4[i]; j++) {
      if (!perryDec[i][j]) {
        matrix.setTextColor(matrix.color565(255, 215, 0));
        matrix.print(getRandomCharacterP());
      } else {
        matrix.setTextColor(matrix.color565(100, 149, 237));
        matrix.print(perryLines4[i][j]);
      }
    }
  }

  matrix.show();
}

// updateDecryption4: reveal one random char per call and refresh display
void updateDecryption4() {
  for (uint8_t i = 0; i < perryLineCount4; i++) {
    if (perryDone[i] < perryLens4[i]) {
      uint8_t idx;
      do {
        idx = random(0, perryLens4[i]);
      } while (perryDec[i][idx]);
      perryDec[i][idx] = true;
      perryDone[i]++;
      if (perryDone[i] == perryLens4[i]) {
        p_linesDone++;
      }
    }
  }
  displayRandomText4();
  p_lastUpdate = millis();
}
