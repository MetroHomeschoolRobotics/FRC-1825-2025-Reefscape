// © 2025 SC5K Systems

#include "src/dynamic.h"
#include "src/matrix_config.h"
#include "src/globals.h"

#include <Arduino.h>
#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_LIS3DH.h>
#include <Fonts/TomThumb.h>
#include <math.h>
#include <string.h>       // for strlen(), snprintf()
#include <stdio.h>        // for snprintf()
#include "src/helpers.h"  // hsvToRgb helper for dynamic colours

// intake tube state (private to dynamic.cpp)
static bool tubeActive = false;
static bool tubeFinished = false;
static int16_t tubeX = 0, tubeY = 0, tubeW = 0, tubeBodyH = 0, tubeTargetY = 0;
static int8_t tubeSpeed = 2;

// animate increasing volatility for higher levels.
static bool dynScoreActive = false;                  // true when a scoring level is active
static uint8_t dynScoreLevel = 0;                    // 1–4 for levels, 0 for none
static const uint8_t kScoreBars = 4;                 // number of bars per side in the meter
static uint8_t scoreBarHeights[kScoreBars] = { 0 };  // current heights for each bar
static unsigned long scoreLastUpdate = 0;            // last update time for bar animation

// meter height in pixels; bars extend down from baseline by up to this amount
static const uint8_t scoreMeterHeight = 8;

static float nodePosX[12];
static float nodePosY[12];
static float nodeVelX[12];
static float nodeVelY[12];

// scoring number animation state for overlay; counts down from start to target level
static bool scoreNumberAnimating = false;
static unsigned long scoreNumberStart = 0;
// reverse counter duration (ms); counts down from startVal to level; shorter = faster
static const unsigned long scoreAnimDuration = 1000UL;
// starting value for reverse counter (user-tunable); smaller = shorter countdown
static int scoreNumberStartVal = 50;
static int scoreNumberTargetVal = 0;
// Track the last score level to detect changes and avoid restarting
static uint8_t lastScoreLevel = 0;

// climb state: 0=no climb, 1=ready, 2=attempt, 3=success
static uint8_t dynClimbState = 0;

// updateScoreBars: compute new bar heights based on current score level; simple random walk
static void updateScoreBars() {
  if (dynScoreLevel == 0) return;
  float baseFraction = (float)dynScoreLevel / 4.0f;
  int baseHeight = (int)roundf(baseFraction * (float)scoreMeterHeight);
  for (uint8_t i = 0; i < kScoreBars; i++) {
    int current = scoreBarHeights[i];
    // step by -1, 0, or +1
    int delta = (int)random(-1, 2);
    int candidate = current + delta;
    // clamp to [1, scoreMeterHeight]
    if (candidate < 1) candidate = 1;
    if (candidate > (int)scoreMeterHeight) candidate = scoreMeterHeight;
    // keep the value near the baseHeight ± level to prevent drift
    int lower = baseHeight - (int)dynScoreLevel;
    int upper = baseHeight + (int)dynScoreLevel;
    if (candidate < lower) candidate = lower;
    if (candidate > upper) candidate = upper;
    if (candidate < 1) candidate = 1;
    if (candidate > (int)scoreMeterHeight) candidate = scoreMeterHeight;
    scoreBarHeights[i] = (uint8_t)candidate;
  }
}

// initNet: seed node positions within mid segment and assign small random velocities
static void initNet() {
  const int16_t x_min = 0;
  const int16_t x_max = matrix.width();
  const int16_t y_min = SEG_TOP_H;
  const int16_t y_max = SEG_TOP_H + SEG_MID_H;

  for (uint8_t i = 0; i < NODE_COUNT; i++) {
    // random starting position within the network bounds
    nodes[i].x = random(x_min, x_max);
    nodes[i].y = random(y_min, y_max);
    // initialise float position mirrors
    nodePosX[i] = (float)nodes[i].x;
    nodePosY[i] = (float)nodes[i].y;
    float vx = 0.0f, vy = 0.0f;
    do {
      vx = (float)random(-100, 101) / 200.0f;  // [-0.5,0.5]
      vy = (float)random(-100, 101) / 200.0f;
    } while (fabsf(vx) < 0.05f && fabsf(vy) < 0.05f);
    // optional normalisation to limit overall speed
    float mag = sqrtf(vx * vx + vy * vy);
    float maxMag = 0.6f;
    if (mag > maxMag) {
      vx = vx * (maxMag / mag);
      vy = vy * (maxMag / mag);
    }
    nodeVelX[i] = vx;
    nodeVelY[i] = vy;
    // store a rough integer velocity in the Node for backward
    // compatibility (sign only).  This is no longer used for
    // movement but may be used in other code.
    nodes[i].dx = (int8_t)round(vx);
    nodes[i].dy = (int8_t)round(vy);
  }
}

// segIntersect: return true if two line segments intersect
static bool segIntersect(int x1, int y1, int x2, int y2,
                         int x3, int y3, int x4, int y4) {
  auto orient = [](long ax, long ay, long bx, long by, long cx, long cy) {
    long v = (bx - ax) * (cy - ay) - (by - ay) * (cx - ax);
    if (v > 0) return 1;
    if (v < 0) return -1;
    return 0;
  };
  int o1 = orient(x1, y1, x2, y2, x3, y3);
  int o2 = orient(x1, y1, x2, y2, x4, y4);
  int o3 = orient(x3, y3, x4, y4, x1, y1);
  int o4 = orient(x3, y3, x4, y4, x2, y2);
  if (o1 * o2 < 0 && o3 * o4 < 0) return true;
  return false;
}

// drawNet: update node positions, handle collisions, draw k-NN graph without crossings
static void drawNet() {
  // rail lines separating segments
  const uint16_t rail = matrix.color565(0, 255, 0);
  matrix.drawLine(0, SEG_TOP_H, matrix.width() - 1, SEG_TOP_H, rail);
  matrix.drawLine(0, SEG_TOP_H + SEG_MID_H, matrix.width() - 1,
                  SEG_TOP_H + SEG_MID_H, rail);

  static const uint8_t kUpdateDiv = 4;
  if (++netFrame >= kUpdateDiv) {
    netFrame = 0;

    const int16_t xmin = (matrix.width() - SEG_MID_H) / 2 + 2;
    const int16_t xmax = xmin + SEG_MID_H - 4;
    const int16_t ymin = SEG_TOP_H + 2;
    const int16_t ymax = SEG_TOP_H + SEG_MID_H - 2;

    // integrate + wall bounce using float velocities
    for (uint8_t i = 0; i < NODE_COUNT; i++) {
      const float netSpeed = 1.75f;
      nodePosX[i] += nodeVelX[i] * netSpeed;
      nodePosY[i] += nodeVelY[i] * netSpeed;
      // bounce on walls
      if (nodePosX[i] < xmin) {
        nodePosX[i] = xmin;
        nodeVelX[i] = -nodeVelX[i];
      }
      if (nodePosX[i] > xmax) {
        nodePosX[i] = xmax;
        nodeVelX[i] = -nodeVelX[i];
      }
      if (nodePosY[i] < ymin) {
        nodePosY[i] = ymin;
        nodeVelY[i] = -nodeVelY[i];
      }
      if (nodePosY[i] > ymax) {
        nodePosY[i] = ymax;
        nodeVelY[i] = -nodeVelY[i];
      }
      // rare micro-jitter to keep it organic (still very slow).  Jitter
      // introduces small random components to the velocity and
      // normalises the result to avoid runaway speeds.
      if (random(0, 1400) < 2) {
        float jx = (float)random(-100, 101) / 500.0f;  // [-0.2,0.2]
        float jy = (float)random(-100, 101) / 500.0f;
        // apply jitter only if not both near zero
        if (fabsf(jx) > 0.01f || fabsf(jy) > 0.01f) {
          nodeVelX[i] += jx;
          nodeVelY[i] += jy;
        }
        // limit speed
        float mag = sqrtf(nodeVelX[i] * nodeVelX[i] + nodeVelY[i] * nodeVelY[i]);
        float maxMag = 0.6f;
        if (mag > maxMag) {
          nodeVelX[i] *= maxMag / mag;
          nodeVelY[i] *= maxMag / mag;
        }
        // ensure velocity not too small
        if (fabsf(nodeVelX[i]) < 0.05f && fabsf(nodeVelY[i]) < 0.05f) {
          float vx = (float)random(-100, 101) / 200.0f;
          float vy = (float)random(-100, 101) / 200.0f;
          if (fabsf(vx) < 0.05f && fabsf(vy) < 0.05f) {
            vx = 0.1f;
            vy = 0.1f;
          }
          nodeVelX[i] = vx;
          nodeVelY[i] = vy;
        }
      }
      // update integer positions from floats
      nodes[i].x = (int16_t)roundf(nodePosX[i]);
      nodes[i].y = (int16_t)roundf(nodePosY[i]);
      // update int velocities for compatibility with other code (sign only)
      nodes[i].dx = (int8_t)roundf(nodeVelX[i]);
      nodes[i].dy = (int8_t)roundf(nodeVelY[i]);
    }

    const int collR2 = 9;  // ~3px radius
    for (uint8_t i = 0; i < NODE_COUNT; i++) {
      for (uint8_t j = i + 1; j < NODE_COUNT; j++) {
        int dxInt = nodes[i].x - nodes[j].x;
        int dyInt = nodes[i].y - nodes[j].y;
        if (dxInt * dxInt + dyInt * dyInt <= collR2) {
          // swap velocities
          float tvx = nodeVelX[i];
          nodeVelX[i] = nodeVelX[j];
          nodeVelX[j] = tvx;
          float tvy = nodeVelY[i];
          nodeVelY[i] = nodeVelY[j];
          nodeVelY[j] = tvy;
          // nudge integer positions apart
          int dx = dxInt;
          int dy = dyInt;
          if (dx == 0 && dy == 0) dx = 1;
          if (abs(dx) >= abs(dy)) {
            nodes[i].x += (dx >= 0) ? 1 : -1;
            nodes[j].x -= (dx >= 0) ? 1 : -1;
            nodePosX[i] = nodes[i].x;
            nodePosX[j] = nodes[j].x;
          } else {
            nodes[i].y += (dy >= 0) ? 1 : -1;
            nodes[j].y -= (dy >= 0) ? 1 : -1;
            nodePosY[i] = nodes[i].y;
            nodePosY[j] = nodes[j].y;
          }
        }
      }
    }
  }

  // build k-NN (k=2) graph with crossing suppression
  struct Edge {
    uint8_t a, b;
  };
  Edge cand[NODE_COUNT * 2];
  int ccount = 0;

  // for each node, pick 2 nearest neighbors
  for (uint8_t i = 0; i < NODE_COUNT; i++) {
    int best1 = -1, best2 = -1;
    long d1 = LONG_MAX, d2 = LONG_MAX;
    for (uint8_t j = 0; j < NODE_COUNT; j++) {
      if (j == i) continue;
      long dx = nodes[i].x - nodes[j].x;
      long dy = nodes[i].y - nodes[j].y;
      long d = dx * dx + dy * dy;
      if (d < d1) {
        d2 = d1;
        best2 = best1;
        d1 = d;
        best1 = j;
      } else if (d < d2) {
        d2 = d;
        best2 = j;
      }
    }
    if (best1 >= 0) {
      uint8_t a = min(i, (uint8_t)best1), b = max(i, (uint8_t)best1);
      cand[ccount++] = { a, b };
    }
    if (best2 >= 0) {
      uint8_t a = min(i, (uint8_t)best2), b = max(i, (uint8_t)best2);
      cand[ccount++] = { a, b };
    }
  }

  // dedupe candidates
  Edge uniq[NODE_COUNT * 2];
  int ucount = 0;
  for (int i = 0; i < ccount; i++) {
    bool seen = false;
    for (int k = 0; k < ucount; k++) {
      if (cand[i].a == uniq[k].a && cand[i].b == uniq[k].b) {
        seen = true;
        break;
      }
    }
    if (!seen) uniq[ucount++] = cand[i];
  }

  // accept edges while avoiding crossings
  Edge chosen[NODE_COUNT * 2];
  int ecount = 0;
  for (int i = 0; i < ucount; i++) {
    int ax = nodes[uniq[i].a].x, ay = nodes[uniq[i].a].y;
    int bx = nodes[uniq[i].b].x, by = nodes[uniq[i].b].y;
    bool cross = false;
    for (int k = 0; k < ecount; k++) {
      int cx = nodes[chosen[k].a].x, cy = nodes[chosen[k].a].y;
      int dx = nodes[chosen[k].b].x, dy = nodes[chosen[k].b].y;
      if ((ax == cx && ay == cy) || (ax == dx && ay == dy) || (bx == cx && by == cy) || (bx == dx && by == dy)) continue;  // share node ok
      if (segIntersect(ax, ay, bx, by, cx, cy, dx, dy)) {
        cross = true;
        break;
      }
    }
    if (!cross) chosen[ecount++] = uniq[i];
  }

  for (int i = 0; i < ecount; i++) {
    int ax = nodes[chosen[i].a].x, ay = nodes[chosen[i].a].y;
    int bx = nodes[chosen[i].b].x, by = nodes[chosen[i].b].y;
    // Compute a phase for this edge, varying with time and index.
    float phase = ((float)millis() / 120.0f) + (float)i * 0.3f;
    float t = (sinf(phase) + 1.0f) * 0.5f;  // 0→1
    // Blend between green (0,255,0) and white (255,255,255)
    uint8_t rE = (uint8_t)(t * 255.0f);
    uint8_t gE = 255;
    uint8_t bE = (uint8_t)(t * 255.0f);
    uint16_t lineCol = matrix.color565(rE, gE, bE);
    matrix.drawLine(ax, ay, bx, by, lineCol);
  }
  // draw nodes with a similar green→white blend and pulsing radius.
  for (uint8_t i = 0; i < NODE_COUNT; i++) {
    // Colour varies with time and node index.  The node pulses through
    // shades of green→white.
    float phaseC = ((float)millis() / 100.0f) + (float)i * 0.5f;
    float tc = (sinf(phaseC) + 1.0f) * 0.5f;
    uint8_t rN = (uint8_t)(tc * 255.0f);
    uint8_t gN = 255;
    uint8_t bN = (uint8_t)(tc * 255.0f);
    uint16_t colN = matrix.color565(rN, gN, bN);
    // Pulse radius between 1 and 2 pixels based on a sinusoid.  Each
    // node pulses at a slightly different phase by adding its index.
    float phaseR = ((float)millis() / 200.0f) + (float)i;
    float s = sinf(phaseR);
    uint8_t rad = (uint8_t)(1 + (s > 0.0f ? 1 : 0));
    matrix.fillCircle(nodes[i].x, nodes[i].y, rad, colN);
  }
}

// rotProj: rotate a 3D vector by roll/pitch and project to screen coords
static void rotProj(const float in[3], float pitch, float roll,
                    int16_t& sx, int16_t& sy,
                    int16_t cx, int16_t cy) {
  float x = in[0] * CUBE_SIZE;
  float y = in[1] * CUBE_SIZE;
  float z = in[2] * CUBE_SIZE;
  float cX = cosf(roll), sX = sinf(roll);
  float y1 = y * cX - z * sX;
  float z1 = y * sX + z * cX;
  float cY = cosf(pitch), sY = sinf(pitch);
  float x2 = x * cY + z1 * sY;
  float z2 = -x * sY + z1 * cY;
  float zcam = CAMERA_DIST + z2;
  float xp = (x2 * FOV) / zcam;
  float yp = (y1 * FOV) / zcam;
  sx = int16_t(xp + cx);
  sy = int16_t(cy - yp);
}

// drawTinyCube: draw small cube and rgb axes at a given origin
static void drawTinyCube(int16_t x0, int16_t y0) {
  int16_t cx = x0 + 12;
  int16_t cy = y0 + 24;
  int16_t vx[8], vy[8];
  for (uint8_t i = 0; i < 8; i++) {
    rotProj(VERTS[i], filtPitch, filtRoll, vx[i], vy[i], cx, cy);
  }
  const uint16_t white = matrix.color565(255, 255, 255);
  for (uint8_t i = 0; i < 12; i++) {
    matrix.drawLine(vx[EDGES[i][0]], vy[EDGES[i][0]],
                    vx[EDGES[i][1]], vy[EDGES[i][1]], white);
  }
  const uint16_t cols[3] = {
    matrix.color565(255, 0, 0),
    matrix.color565(0, 255, 0),
    matrix.color565(0, 0, 255)
  };
  int16_t axp, ayp;
  for (uint8_t a = 0; a < 3; a++) {
    rotProj(AXIS_V[a], filtPitch, filtRoll, axp, ayp, cx, cy);
    matrix.drawLine(cx, cy, axp, ayp, cols[a]);
  }
}

// initDynamic: init LIS3DH, seed net, reset filters and UI flags
void initDynamic() {
  Wire.begin();
  if (!lis.begin(0x18) && !lis.begin(0x19)) {
    Serial.println(F("LIS3DH not found at 0x18 or 0x19"));
  } else {
    lis.setRange(LIS3DH_RANGE_4_G);
  }
  initNet();

  filtRoll = 0.0f;
  filtPitch = 0.0f;
  accX = accY = accZ = 0.0f;

  showAccel = false;  // optional
  showCube = false;

  dynAiState = false;
  dynAiPrev = millis();
  dynReqState = false;
  dynReqPrev = millis();

  // intake flags
  dynHasPiece = false;
  dynHasBlink = false;
  dynHasPrev = millis();
  dynReqTextVisible = true;  // banners visible until tube completes
}

// updateDynamicFromPayload: parse req,intake,score,climb flags and update UI states
void updateDynamicFromPayload(const char* payload) {
  int v[4] = { 0, 0, 0, 0 };
  static bool lastHas = false;

  // track the last received payload string to avoid re-triggering
  // animations or resetting state when the same message arrives
  static char lastPayloadStr[32] = "";
  if (payload && payload[0] != '\0' && lastPayloadStr[0] != '\0') {
    if (strcmp(payload, lastPayloadStr) == 0) {
      // identical payload received again; do nothing to preserve
      // current state/animation
      return;
    }
  }
  // copy the new payload into lastPayloadStr for next comparison
  if (payload && payload[0] != '\0') {
    strncpy(lastPayloadStr, payload, sizeof(lastPayloadStr) - 1);
    lastPayloadStr[sizeof(lastPayloadStr) - 1] = '\0';
  }
  int count = 0;
  if (payload && *payload) {
    char tmp[24];
    strncpy(tmp, payload, sizeof(tmp) - 1);
    tmp[sizeof(tmp) - 1] = '\0';
    char* p = tmp;
    // parse up to 4 comma‑separated integers.  Track how many
    // successfully converted tokens we see to distinguish between
    // legacy 3‑flag messages and the new 4‑flag format.
    for (uint8_t i = 0; i < 4; i++) {
      v[i] = atoi(p);
      count++;
      char* c = strchr(p, ',');
      if (!c) break;
      p = c + 1;
    }
  }

  bool isNewFormat = (count > 3);

  if (isNewFormat) {
    // new format: request flag
    bool reqFlag = (v[0] != 0);
    int score = v[2];

    if (score > 0) {

      dynScoreActive = true;
      dynScoreLevel = (uint8_t)score;

      if (!scoreNumberAnimating || dynScoreLevel != lastScoreLevel) {
        scoreNumberAnimating = true;
        scoreNumberStart = millis();

        scoreNumberStartVal = 50;
        scoreNumberTargetVal = dynScoreLevel;
      }
      lastScoreLevel = dynScoreLevel;
      // disable request & intake UI
      dynReqPiece = false;
      dynHasPiece = false;
      // reset tube state
      tubeActive = false;
      tubeFinished = false;
      dynReqTextVisible = true;
      // reset intake edge detector state
      lastHas = false;
    } else {
      // not scoring → normal request/intake behaviour
      dynScoreActive = false;
      dynScoreLevel = 0;
      // stop number animation
      scoreNumberAnimating = false;
      // request flag
      dynReqPiece = reqFlag;
      // intake flag with edge-detection for tube animation
      bool newHas = (v[1] != 0);
      if (newHas && !lastHas) {
        // rising edge → start tube
        tubeActive = true;
        tubeFinished = false;
        dynReqTextVisible = true;  // visible during tube travel (then we hide)
        // tube geometry (aligned to top banner)
        const int16_t pad = 2;
        const int16_t h = CHAR_H + pad + 1;         // per-line height
        const int16_t wBox = 5 * CHAR_W + pad * 2;  // banner width
        tubeW = wBox;
        tubeX = (matrix.width() - tubeW) / 2;

        tubeBodyH = matrix.height() / 2;
        // Start fully above the visible area with a small margin
        tubeY = -tubeBodyH - 4;
        // Slower slide speed for readability
        tubeSpeed = 1;
        // tubeTargetY is unused with new sliding logic but kept for legacy
        tubeTargetY = 1;
      } else if (!newHas && lastHas) {
        // falling edge → stop tube & clear state
        tubeActive = false;
        tubeFinished = false;
        dynReqTextVisible = true;  // restore normal banners if request still on
      }
      lastHas = newHas;
      dynHasPiece = newHas;
    }
    // climb state (v[3])
    dynClimbState = (uint8_t)v[3];
  } else {
    // legacy format: treat v[0],v[1],v[2] as accel/ai/cube toggles
    dynScoreActive = false;
    dynScoreLevel = 0;
    // update optional flags; fallback retains prior behaviour (no fetch)
    // showAccel enables accelerometer strip rendering (not implemented)
    showAccel = (v[0] != 0);
    // showAI placeholder (unused)
    showAI = (v[1] != 0);
    // showCube toggles the 3D cube preview on the right
    showCube = (v[2] != 0);
    // ensure request/intake state cleared when legacy flags are used
    dynReqPiece = false;
    dynHasPiece = false;
    tubeActive = false;
    tubeFinished = false;
    dynReqTextVisible = true;
    lastHas = false;
    // no climb state in legacy format
    dynClimbState = 0;
  }
}

/*/
runDynamicFrame
sensor read + filters, draw network (always), banners, ai swap,
intake override (text-only 2× blink + tube slide-in)
/*/
void runDynamicFrame() {
  // sensor → g's
  sensors_event_t evt;
  lis.getEvent(&evt);
  accX = evt.acceleration.y / 9.81f;
  accY = evt.acceleration.z / 9.81f;
  accZ = evt.acceleration.x / 9.81f;

  // pose filter
  float pitchRaw = atan2f(-accX, sqrtf(accY * accY + accZ * accZ));
  float rollCand = atan2f(accY, accZ);
  float delta = rollCand - filtRoll;
  delta = fmodf(delta + M_PI, 2 * M_PI);
  if (delta < 0) delta += 2 * M_PI;
  delta -= M_PI;
  float rollRaw = (fabsf(delta) < ANGLE_THRESHOLD) ? rollCand : filtRoll;
  filtRoll = filtRoll * (1.0f - SMOOTHING) + rollRaw * SMOOTHING;
  filtPitch = filtPitch * (1.0f - SMOOTHING) + pitchRaw * SMOOTHING;

  // blinks
  unsigned long now = millis();
  if (now - dynAiPrev >= dynAiInterval) {
    dynAiPrev = now;
    dynAiState = !dynAiState;
  }
  if (now - dynReqPrev >= dynReqInterval) {
    dynReqPrev = now;
    dynReqState = !dynReqState;
  }
  if (now - dynHasPrev >= (dynReqInterval / 2)) {
    dynHasPrev = now;
    dynHasBlink = !dynHasBlink;
  }

  matrix.fillScreen(0);

  // network (always)
  drawNet();

  // common layout constants
  const char* W1 = "FETCH";
  const char* W2 = "PIECE";
  const int16_t pad = 2;
  const int16_t h = CHAR_H + pad + 1;         // per-line height used for banners
  const int16_t wBox = 5 * CHAR_W + pad * 2;  // 5 letters
  const int16_t xCtr = (matrix.width() - wBox) / 2;
  const int16_t yTop = 1;
  const int16_t yBot = matrix.height() - (2 * h + 2);


  if (dynScoreActive && dynScoreLevel > 0) {
    // periodically update the bar amplitudes
    unsigned long nowScore = millis();

    if (nowScore - scoreLastUpdate > 10UL) {
      scoreLastUpdate = nowScore;
      updateScoreBars();
    }

    // update the animated number
    int displayVal;
    if (scoreNumberAnimating) {
      unsigned long elapsed = millis() - scoreNumberStart;
      float t = (float)elapsed / (float)scoreAnimDuration;
      if (t >= 1.0f) {
        scoreNumberAnimating = false;
        displayVal = scoreNumberTargetVal;
      } else {
        // ease-out cubic: accelerate fast then slow as it approaches 1
        float ease = 1.0f - powf(1.0f - t, 3.0f);
        float delta = (float)(scoreNumberStartVal - scoreNumberTargetVal);
        float v = (float)scoreNumberStartVal - ease * delta;
        displayVal = (int)roundf(v);
      }
    } else {
      displayVal = scoreNumberTargetVal;
    }

    // select meter colour based on level
    uint16_t meterColor;
    switch (dynScoreLevel) {
      case 1: meterColor = matrix.color565(0, 255, 0); break;    // green
      case 2: meterColor = matrix.color565(255, 255, 0); break;  // yellow
      case 3: meterColor = matrix.color565(255, 165, 0); break;  // orange
      case 4: meterColor = matrix.color565(255, 0, 0); break;    // red
      default: meterColor = matrix.color565(200, 200, 200); break;
    }

    const int barW2 = 2;
    const int barGap2 = 1;
    // Determine maximum horizontal length; subtract 1 pixel to leave
    // a small gap at the centre when fully extended.
    int maxLen = (matrix.width() / 2) - 1;
    if (maxLen < 1) maxLen = 1;

    // Precompute an amplitude scaling factor based on the score level.
    // Reduce the amplitude slightly so bars waver smoothly without
    // huge jumps.  The amplitude scales with the level, but capped
    // to 0.3.
    float ampScale = ((float)dynScoreLevel) / 4.0f * 0.3f;
    // current time in milliseconds, reused for both top and bottom
    unsigned long tNow = nowScore;

    // Top segment bounds
    int16_t topSegY = yTop;
    int16_t topSegH = SEG_TOP_H;
    // Number of bars that fit in the top segment
    int numBarsTop = topSegH / (barW2 + barGap2);
    // Draw each bar row for the top segment
    for (int j = 0; j < numBarsTop; j++) {
      // choose a base height index cyclically to give variety
      uint8_t idx = (uint8_t)(j % kScoreBars);
      uint8_t base = scoreBarHeights[idx];
      // compute amplitude fraction from base and sine modulation
      float fracBase = (float)base / (float)scoreMeterHeight;

      float mod = sinf((float)tNow / 100.0f + (float)j) * ampScale;
      float frac = fracBase + mod;
      if (frac < 0.0f) frac = 0.0f;
      if (frac > 1.0f) frac = 1.0f;
      int len = (int)roundf(frac * (float)maxLen);
      if (len < 1) len = 1;
      int16_t yBar = topSegY + j * (barW2 + barGap2);
      // ensure we don't overrun the segment height (clip last bar if needed)
      if (yBar + barW2 > topSegY + topSegH) break;
      // draw left and right bars
      matrix.fillRect(0, yBar, len, barW2, meterColor);
      matrix.fillRect(matrix.width() - len, yBar, len, barW2, meterColor);
    }

    // Bottom segment bounds
    const int16_t hAI = CHAR_H + pad + 1;
    int16_t bottomSegY = SEG_TOP_H + SEG_MID_H + 1 + hAI + 2;
    int16_t bottomSegH = matrix.height() - bottomSegY;
    int numBarsBot = bottomSegH / (barW2 + barGap2);
    for (int j = 0; j < numBarsBot; j++) {
      uint8_t idx = (uint8_t)(j % kScoreBars);
      uint8_t base = scoreBarHeights[idx];
      float fracBase = (float)base / (float)scoreMeterHeight;
      // Increase the modulation frequency by reducing the period.
      float mod = sinf((float)tNow / 100.0f + (float)j) * ampScale;
      float frac = fracBase + mod;
      if (frac < 0.0f) frac = 0.0f;
      if (frac > 1.0f) frac = 1.0f;
      int len = (int)roundf(frac * (float)maxLen);
      if (len < 1) len = 1;
      int16_t yBar = bottomSegY + j * (barW2 + barGap2);
      if (yBar + barW2 > bottomSegY + bottomSegH) break;
      matrix.fillRect(0, yBar, len, barW2, meterColor);
      matrix.fillRect(matrix.width() - len, yBar, len, barW2, meterColor);
    }

    // Prepare text labels.  "LEVEL" label (5 chars) and dynamic
    // number string.  We'll compute the numeric string on the fly.
    const char* Lbl = "LEVEL";
    char numBuf[6];
    snprintf(numBuf, sizeof(numBuf), "%d", displayVal);
    size_t numLen = strlen(numBuf);

    // Set text colour once
    uint16_t txtCol = matrix.color565(255, 255, 255);
    matrix.setTextColor(txtCol);

    // Determine heights for the label line (size 1) and the number line
    // (size 2).  The TomThumb font height is CHAR_H.  With
    // setTextSize(2), height becomes 2*CHAR_H.  We include pad and
    // inter-line spacing (1 pixel) to align neatly.
    int16_t hLbl = CHAR_H + pad + 1;
    int16_t hNum = (CHAR_H * 2) + pad + 1;
    int16_t totalTextH = hLbl + hNum;

    // Top segment: centre the block vertically
    int16_t yMidTop = topSegY + topSegH / 2;
    int16_t yBlockTop = yMidTop - totalTextH / 2;
    // Label line position: centre glyph vertically within its line
    int16_t yLevelTop = yBlockTop + (hLbl - CHAR_H) / 2;
    // Number line position: centre glyph within double-height line
    int16_t yNumTop = yBlockTop + hLbl + (hNum - (CHAR_H * 2)) / 2;
    // Horizontal positions: centre label (size 1) and number (size 2)
    int16_t lvlX = (matrix.width() - (5 * CHAR_W)) / 2;
    int16_t numX = (matrix.width() - (int)(numLen * CHAR_W * 2)) / 2;
    // Draw top text
    matrix.setTextSize(1);
    matrix.setCursor(lvlX, yLevelTop);
    matrix.print(Lbl);
    matrix.setTextSize(2);
    matrix.setCursor(numX, yNumTop);
    matrix.print(numBuf);
    // Reset to size 1 after drawing number
    matrix.setTextSize(1);

    // Bottom segment: compute analogous positions
    int16_t yMidBot = bottomSegY + bottomSegH / 2;
    int16_t yBlockBot = yMidBot - totalTextH / 2;
    int16_t yLevelBot = yBlockBot + (hLbl - CHAR_H) / 2;
    int16_t yNumBot = yBlockBot + hLbl + (hNum - (CHAR_H * 2)) / 2;
    lvlX = (matrix.width() - (5 * CHAR_W)) / 2;
    numX = (matrix.width() - (int)(numLen * CHAR_W * 2)) / 2;
    matrix.setTextSize(1);
    matrix.setCursor(lvlX, yLevelBot);
    matrix.print(Lbl);
    matrix.setTextSize(2);
    matrix.setCursor(numX, yNumBot);
    matrix.print(numBuf);
    matrix.setTextSize(1);

  } else if (dynClimbState > 0) {

    // determine dimensions and positions
    const int16_t climbGap = 2;  // gap between text and boxes
    const int16_t readyLen = 5;  // length of "READY"
    const int16_t readyWidth = readyLen * CHAR_W;
    // Precompute colours
    const uint16_t redCol = matrix.color565(255, 0, 0);
    const uint16_t greenCol = matrix.color565(0, 255, 0);
    // Top segment
    {
      int16_t segY = yTop;
      int16_t segH = SEG_TOP_H;
      // total height: label line (h) + gap + box height
      int16_t totalH = h + climbGap + chkBoxH;
      int16_t yStart = segY + (segH - totalH) / 2;
      int16_t yLabel = yStart;
      int16_t yBoxes = yStart + h + climbGap;
      // draw according to climbState
      if (dynClimbState == 3) {
        // success: flashing "WHAT", "A", "CLIMB".  Use the dynHasBlink
        // flag to blink text at roughly 2× request blink speed.
        bool visible = dynHasBlink;
        if (visible) {
          matrix.setTextColor(greenCol);
          // centre each line horizontally
          const char* w1 = "WHAT";
          const char* w2 = "A";
          const char* w3 = "CLIMB";
          size_t l1 = strlen(w1);
          size_t l2 = strlen(w2);
          size_t l3 = strlen(w3);
          int16_t x1 = (matrix.width() - (l1 * CHAR_W)) / 2;
          int16_t x2 = (matrix.width() - (l2 * CHAR_W)) / 2;
          int16_t x3 = (matrix.width() - (l3 * CHAR_W)) / 2;
          // compute vertical positions for three lines within the top segment
          int16_t totalTextH = h * 3;
          int16_t yMid = segY + segH / 2;
          int16_t y0 = yMid - totalTextH / 2;
          int16_t yLine1 = y0 + (h - CHAR_H) / 2;
          int16_t yLine2 = y0 + h + (h - CHAR_H) / 2;
          int16_t yLine3 = y0 + 2 * h + (h - CHAR_H) / 2;
          matrix.setCursor(x1, yLine1);
          matrix.print(w1);
          matrix.setCursor(x2, yLine2);
          matrix.print(w2);
          matrix.setCursor(x3, yLine3);
          matrix.print(w3);
        }
      } else {
        // states 1 or 2: draw READY label and two boxes
        matrix.setTextColor(matrix.color565(255, 255, 255));
        int16_t xReady = (matrix.width() - readyWidth) / 2;
        int16_t yReady = yLabel + (h - CHAR_H) / 2;
        matrix.setCursor(xReady, yReady);
        matrix.print("READY");
        // Determine box colour: state 1 = red; state 2 = flashing green
        uint16_t boxCol;
        if (dynClimbState == 1) {
          boxCol = redCol;
        } else {
          // flash green at 4× AI blink frequency
          unsigned long interval = dynAiInterval / 4;
          bool blink = ((millis() / (interval > 0 ? interval : 1)) % 2) == 0;
          boxCol = blink ? greenCol : redCol;
        }
        // boxes horizontal layout
        int16_t gapBoxes = 4;
        int16_t totalBoxesW = chkBoxW * 2 + gapBoxes;
        int16_t xBoxes = (matrix.width() - totalBoxesW) / 2;
        // draw top boxes
        matrix.fillRect(xBoxes, yBoxes, chkBoxW, chkBoxH, boxCol);
        matrix.fillRect(xBoxes + chkBoxW + gapBoxes, yBoxes, chkBoxW, chkBoxH, boxCol);
      }
    }
    // Bottom segment: replicate the top segment layout
    {
      int16_t segY = SEG_TOP_H + SEG_MID_H + 1 + (CHAR_H + pad + 1) + 2;  // baseline for bottom banners
      int16_t segH = matrix.height() - segY;
      // adjust total height same as top
      int16_t totalH = h + climbGap + chkBoxH;
      int16_t yStart = segY + (segH - totalH) / 2;
      int16_t yLabel = yStart;
      int16_t yBoxes = yStart + h + climbGap;
      if (dynClimbState == 3) {
        bool visible = dynHasBlink;
        if (visible) {
          matrix.setTextColor(greenCol);
          const char* w1 = "WHAT";
          const char* w2 = "A";
          const char* w3 = "CLIMB";
          size_t l1 = strlen(w1);
          size_t l2 = strlen(w2);
          size_t l3 = strlen(w3);
          int16_t x1 = (matrix.width() - (l1 * CHAR_W)) / 2;
          int16_t x2 = (matrix.width() - (l2 * CHAR_W)) / 2;
          int16_t x3 = (matrix.width() - (l3 * CHAR_W)) / 2;
          int16_t totalTextH = h * 3;
          int16_t yMid = segY + segH / 2;
          int16_t y0 = yMid - totalTextH / 2;
          int16_t yLine1 = y0 + (h - CHAR_H) / 2;
          int16_t yLine2 = y0 + h + (h - CHAR_H) / 2;
          int16_t yLine3 = y0 + 2 * h + (h - CHAR_H) / 2;
          matrix.setCursor(x1, yLine1);
          matrix.print(w1);
          matrix.setCursor(x2, yLine2);
          matrix.print(w2);
          matrix.setCursor(x3, yLine3);
          matrix.print(w3);
        }
      } else {
        matrix.setTextColor(matrix.color565(255, 255, 255));
        int16_t xReady = (matrix.width() - readyWidth) / 2;
        int16_t yReady = yLabel + (h - CHAR_H) / 2;
        matrix.setCursor(xReady, yReady);
        matrix.print("READY");
        uint16_t boxCol;
        if (dynClimbState == 1) {
          boxCol = redCol;
        } else {
          unsigned long interval = dynAiInterval / 4;
          bool blink = ((millis() / (interval > 0 ? interval : 1)) % 2) == 0;
          boxCol = blink ? greenCol : redCol;
        }
        int16_t gapBoxes = 4;
        int16_t totalBoxesW = chkBoxW * 2 + gapBoxes;
        int16_t xBoxes = (matrix.width() - totalBoxesW) / 2;
        matrix.fillRect(xBoxes, yBoxes, chkBoxW, chkBoxH, boxCol);
        matrix.fillRect(xBoxes + chkBoxW + gapBoxes, yBoxes, chkBoxW, chkBoxH, boxCol);
      }
    }

  } else {
    // No scoring or climb overlay: handle request/intake banners and tube animation
    // colours for request (use your improved red/green)
    const uint16_t colW = matrix.color565(255, 0, 0);  // text when swapping
    const uint16_t colG = matrix.color565(0, 255, 0);  // bg when swapping

    // intake override?
    if (dynHasPiece) {
      // text-only, blinking green at 2× speed
      if (dynHasBlink || !tubeFinished) {
        matrix.setTextColor(matrix.color565(0, 255, 0));
        // top two lines
        matrix.setCursor(xCtr + pad, yTop + (h - CHAR_H) / 2);
        matrix.print(W1);
        matrix.setCursor(xCtr + pad, yTop + h + (h - CHAR_H) / 2);
        matrix.print(W2);
        // bottom two lines
        matrix.setCursor(xCtr + pad, yBot + (h - CHAR_H) / 2);
        matrix.print(W1);
        matrix.setCursor(xCtr + pad, yBot + h + (h - CHAR_H) / 2);
        matrix.print(W2);
      }

      // tube slide-in from top over the top banner
      if (tubeActive) {
        // body
        uint16_t white = matrix.color565(255, 255, 255);
        matrix.fillRect(tubeX, tubeY, tubeW, tubeBodyH, white);

        // “rim” near the front (simulate a pipe mouth)
        int rimH = 4;
        int ry = tubeY + tubeBodyH - rimH;
        matrix.drawRect(tubeX, ry, tubeW, rimH, white);
        matrix.fillRect(tubeX + 1, ry + 1, tubeW - 2, rimH - 2, 0);

        // advance: slide until the bottom of the tube reaches the network
        // boundary defined by SEG_TOP_H.  Once the bottom is past that
        // line, stop the animation and hide the request banners.
        if ((tubeY + tubeBodyH) < SEG_TOP_H) {
          tubeY += tubeSpeed;
        } else {
          tubeActive = false;
          tubeFinished = true;
          dynReqTextVisible = false;  // hide the banners once tube completes
        }
      } else if (tubeFinished) {
        // Keep the tube in place after it has finished sliding in until
        // dynHasPiece becomes false.  Draw the tube body and rim at its
        // final position.
        uint16_t white = matrix.color565(255, 255, 255);
        matrix.fillRect(tubeX, tubeY, tubeW, tubeBodyH, white);
        int rimH = 4;
        int ry = tubeY + tubeBodyH - rimH;
        matrix.drawRect(tubeX, ry, tubeW, rimH, white);
        matrix.fillRect(tubeX + 1, ry + 1, tubeW - 2, rimH - 2, 0);
      }
    } else if (dynReqPiece) {
      // normal request (boxes that color-swap)
      if (dynReqTextVisible) {
        uint16_t bg = dynReqState ? colG : colW;
        uint16_t tx = dynReqState ? colW : colG;

        // top: FETCH / PIECE
        matrix.fillRect(xCtr, yTop, wBox, h, bg);
        matrix.setTextColor(tx);
        matrix.setCursor(xCtr + pad, yTop + (h - CHAR_H) / 2);
        matrix.print(W1);
        matrix.fillRect(xCtr, yTop + h, wBox, h, bg);
        matrix.setTextColor(tx);
        matrix.setCursor(xCtr + pad, yTop + h + (h - CHAR_H) / 2);
        matrix.print(W2);

        // bottom: FETCH / PIECE
        matrix.fillRect(xCtr, yBot, wBox, h, bg);
        matrix.setTextColor(tx);
        matrix.setCursor(xCtr + pad, yBot + (h - CHAR_H) / 2);
        matrix.print(W1);
        matrix.fillRect(xCtr, yBot + h, wBox, h, bg);
        matrix.setTextColor(tx);
        matrix.setCursor(xCtr + pad, yBot + h + (h - CHAR_H) / 2);
        matrix.print(W2);
      }
    } else {
      // neither requested nor in intake -> ensure tube state cleared
      tubeActive = false;
      tubeFinished = false;
      dynReqTextVisible = true;
    }
  }

  // --- AI | ON strip just below the network boundary ---
  {
    const uint16_t colY = matrix.color565(255, 255, 0);
    const uint16_t colR = matrix.color565(255, 0, 0);
    const int16_t hAI = CHAR_H + pad + 1;
    const int16_t wBoxAI = 2 * CHAR_W + pad * 2;  // "AI", "ON"
    const int16_t x0 = 2;
    const int16_t x1 = x0 + wBoxAI;
    const int16_t yb = SEG_TOP_H + SEG_MID_H + 1;

    uint16_t aBg = dynAiState ? colY : colR;  // left
    uint16_t aTx = dynAiState ? colR : colY;
    uint16_t bBg = dynAiState ? colR : colY;  // right
    uint16_t bTx = dynAiState ? colY : colR;

    matrix.fillRect(x0, yb, wBoxAI, hAI, aBg);
    matrix.setTextColor(aTx);
    matrix.setCursor(x0 + pad, yb + (hAI - CHAR_H) / 2);
    matrix.print("AI");

    matrix.fillRect(x1, yb, wBoxAI, hAI, bBg);
    matrix.setTextColor(bTx);
    matrix.setCursor(x1 + pad, yb + (hAI - CHAR_H) / 2);
    matrix.print("ON");
  }

  if (showCube && !(dynScoreActive && dynScoreLevel > 0)) {
    drawTinyCube(matrix.width() - 28, SEG_TOP_H + SEG_MID_H + 14);
  }

  matrix.show();
}
