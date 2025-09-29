// © 2025 SC5K Systems

#include "src/autonomous.h"
#include "src/globals.h"
#include "src/matrix_config.h"
#include <Arduino.h>
#include <math.h>

// reset star: center star and randomize direction & speed
void resetStar(Star &s) {
  int16_t cx = matrix.width()/2;
  int16_t cy = matrix.height()/2;
  s.xInt = cx;
  s.yInt = cy;
  s.accX = s.accY = 0.0f;
  float ang   = random(0,360) * (PI/180.0f);
  float speed = random(8,15) / 10.0f;
  s.stepX = cos(ang) * speed;
  s.stepY = sin(ang) * speed;
}

// init autonomous: clear state, seed stars and circles, compute auto‑lock box coords
void initAutonomous() {
  autoActive   = true;
  autoStarPrev = millis();
  autoTextPrev = millis();
  autoState    = false;

  matrix.fillScreen(0);

  // seed stars and dynamic circles
  for (int i = 0; i < MAX_STARS; i++) resetStar(stars[i]);
  for (int i = 0; i < DYN_CIRCLES; i++) dynRadius[i] = 1;

  // calculate center-based coords for "AUTO LOCK" box
  int16_t cx = matrix.width()/2;
  int16_t cy = matrix.height()/2;
  tX1 = cx - 2*CHAR_W;
  tY1 = cy - 10;
  tX2 = cx - 2*CHAR_W;
  tY2 = cy + 2;
  boxX = cx - 17;
  boxY = cy - 14;
  boxW = 34;
  boxH = 28;

  startTime = millis();
}

// run autonomous frame: blink text, draw circles and stars, and draw auto‑lock box each cycle
void runAutonomousFrame() {
  unsigned long now = millis();

  // toggle blink state for auto text
  if (now - autoTextPrev >= textInterval) {
    autoTextPrev = now;
    autoState = !autoState;
  }

  // update circles and stars each tick
  if (now - autoStarPrev >= starInterval) {
    autoStarPrev = now;
    matrix.fillScreen(matrix.color565(0,0,8));

    int16_t cx = matrix.width()/2;
    int16_t cy = matrix.height()/2;

    // animated circles
    uint16_t dcol = matrix.color565(0,0,28);
    for (int i = 0; i < DYN_CIRCLES; i++) {
      matrix.drawCircle(cx, cy, dynRadius[i], dcol);
      if (++dynRadius[i] > maxDynRadius) dynRadius[i] = 1;
    }

    // static outer circles and spokes
    uint16_t scol = matrix.color565(16,16,50);
    matrix.drawCircle(cx, cy, 50, scol);
    matrix.drawCircle(cx, cy, 15, scol);
    const float step = TWO_PI/16;
    for (int i = 0; i < 16; i++) {
      if (i % 4 == 0) continue;
      float a = i * step;
      int16_t x2 = cx + cos(a) * (50 + 6);
      int16_t y2 = cy + sin(a) * (50 + 6);
      matrix.drawLine(cx, cy, x2, y2, scol);
      matrix.fillCircle(x2, y2, 2, scol);
    }

    // update and draw stars
    for (int i = 0; i < MAX_STARS; i++) {
      Star &s = stars[i];
      s.accX += s.stepX;
      s.accY += s.stepY;
      int16_t dx = int(floor(s.accX));
      int16_t dy = int(floor(s.accY));
      if (dx) { s.xInt += dx; s.accX -= dx; }
      if (dy) { s.yInt += dy; s.accY -= dy; }
      if (s.xInt < 0 || s.xInt >= matrix.width() ||
          s.yInt < 0 || s.yInt >= matrix.height()) {
        resetStar(s);
      } else {
        matrix.drawPixel(s.xInt, s.yInt, matrix.color565(255,255,255));
      }
    }

    // draw the auto‑lock box with colours based on blink state
    uint16_t boxCol = autoState ?
      matrix.color565(255,255,0) : matrix.color565(255,0,0);
    uint16_t txtCol = autoState ?
      matrix.color565(255,0,0) : matrix.color565(255,215,0);

    matrix.fillRect(boxX, boxY, boxW, boxH, boxCol);
    matrix.setTextColor(txtCol);
    matrix.setCursor(tX1, tY1);
    matrix.print(line1);
    matrix.setCursor(tX2, tY2);
    matrix.print(line2);

    matrix.show();
  }
}
