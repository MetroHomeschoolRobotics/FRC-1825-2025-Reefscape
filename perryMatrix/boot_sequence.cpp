// © 2025 SC5K Systems

#include "src/boot_sequence.h"
#include "src/checklist.h"
#include "src/helpers.h"
#include "src/globals.h"
#include "src/matrix_config.h"
#include "src/audio_vis.h"
#include "src/sponsor_scroller.h"
#include "src/perry_loader.h"
#include "src/autonomous.h"
#include "src/dynamic.h"
#include "src/shutdown.h"
#include <Arduino.h>
#ifndef USB_SIM_INPUT
#define USB_SIM_INPUT 1 //set to 1 for usb debugging, 0 for RIO only
#endif

// Parse "<mode> <payload>" and run your existing mode logic.
// 'label' is just for the debug print prefix.
static void parseAndDispatchLine(char *line, const char *label) {
  // Debug one clean line
  Serial.print(label);
  Serial.println(line);

  // Skip leading spaces
  char *p = line;
  while (*p == ' ') ++p;

  // Parse mode
  char *endMode = nullptr;
  long modeVal  = strtol(p, &endMode, 10);
  if (p == endMode) return;  // no digits -> ignore
  int8_t newMode = (int8_t)modeVal;

  // Payload begins after spaces following the mode (or nullptr if empty)
  char *payload = endMode;
  while (*payload == ' ') ++payload;
  if (*payload == '\0') payload = nullptr;

  // ---- Your existing mode logic ----
  if (newMode != currentMode) {
    lastMode    = currentMode;
    currentMode = Mode(newMode);
    audioActive = sponsorLaunched = perryActive = false;

    if (currentMode == MODE_CHECKLIST) {
      if (payload) processChecklistPayload(payload);
    } else if (currentMode == MODE_AUTONOMOUS) {
      initAutonomous();
    } else if (currentMode == MODE_DYNAMIC) {
      initDynamic();
      if (payload) updateDynamicFromPayload(payload);
    } else if (currentMode == MODE_SHUTDOWN) {
      // initialise shutdown mode; ignore any payload
      initShutdown();
    } else {
      matrix.fillScreen(0);
      matrix.show();
    }
  } else {
    if (currentMode == MODE_CHECKLIST && payload) {
      processChecklistPayload(payload);
    } else if (currentMode == MODE_DYNAMIC && payload) {
      updateDynamicFromPayload(payload);
    }
    // MODE_AUTONOMOUS and MODE_SHUTDOWN ignore payload updates
  }
}
// init boot sequence: display boot/mode text, animate options, draw outline, blink led, optional colour test, then draw checklist
void initBootSequence() {
  Serial.println("USB serial active");
  Serial.println("RoboRIO serial active");
  uint16_t sw = matrix.width(), sh = matrix.height();

  // boot mode splash
  matrix.fillScreen(0);
  const char *b1 = "BOOT", *b2 = "MODE";
  int16_t x1 = (sw - strlen(b1) * CHAR_W) / 2;
  int16_t x2 = (sw - strlen(b2) * CHAR_W) / 2;
  matrix.setTextColor(matrix.color565(255, 255, 255));
  matrix.setCursor(x1, CHAR_H);
  matrix.print(b1);
  matrix.setCursor(x2, CHAR_H * 2);
  matrix.print(b2);
  matrix.show();
  delay(bootDelay);

  // type options animation
  const char *opts[] = { "LED", "ROBOT", "USB_d" };
  for (uint8_t i = 0; i < 3; i++) {
    uint8_t L = strlen(opts[i]);
    int16_t ox = (sw - L * CHAR_W) / 2;
    int16_t oy = (sh - CHAR_H * 6) / 2 + i * CHAR_H * 2;
    for (uint8_t j = 0; j < L; j++) {
      matrix.fillRect(ox + j * CHAR_W, oy, CHAR_W, CHAR_H,
                      matrix.color565(0, 255, 0));
      matrix.show();
      delay(typeDelay);
      matrix.fillRect(ox + j * CHAR_W, oy, CHAR_W, CHAR_H, 0);
      matrix.setTextColor(matrix.color565(0, 255, 0));
      matrix.setCursor(ox + j * CHAR_W, oy);
      matrix.print(opts[i][j]);
      matrix.show();
    }
  }
  delay(postOptionsDelay);

  // progressive outline draw
  uint8_t L0 = strlen(opts[0]);
  int16_t ox0 = (sw - L0 * CHAR_W) / 2, oy0 = (sh - CHAR_H * 6) / 2;
  int16_t bx = ox0 - 1, by = oy0 - 2;
  int16_t bw = L0 * CHAR_W + 1, bh = CHAR_H + 3;
  uint16_t green = matrix.color565(0, 255, 0);
  int hLen = bw - 1, vLen = bh - 1, steps = hLen + vLen,
      delayStep = 350 / steps;
  matrix.drawPixel(bx, by, green);
  matrix.show();
  delay(delayStep);
  for (int s = 1; s <= steps; s++) {
    if (s <= hLen) matrix.drawPixel(bx + s, by, green);
    else matrix.drawPixel(bx + bw - 1, by + (s - hLen), green);
    if (s <= vLen) matrix.drawPixel(bx, by + s, green);
    else matrix.drawPixel(bx + (s - vLen), by + bh - 1, green);
    matrix.show();
    delay(delayStep);
  }
  delay(dashDelayBefore);
  int perim = 2 * (bw + bh) - 4, offset = 0;
  unsigned long dashStart = millis();
  while (millis() - dashStart < dashedPhaseDuration) {
    matrix.drawRect(bx, by, bw, bh, 0);
    drawDashedOutline(bx, by, bw, bh, perim, offset, green);
    matrix.show();
    delay(flashDelay);
    offset = (offset + 1) % perim;
  }

  // blink "LED"
  for (uint8_t f = 0; f < flashCount; f++) {
    matrix.fillRect(ox0, oy0, bw, CHAR_H, 0);
    offset = (offset + 1) % perim;
    matrix.drawRect(bx, by, bw, bh, 0);
    drawDashedOutline(bx, by, bw, bh, perim, offset, green);
    matrix.show();
    delay(flashDelay);
    matrix.setTextColor(green);
    matrix.setCursor(ox0, oy0);
    matrix.print(opts[0]);
    offset = (offset + 1) % perim;
    matrix.drawRect(bx, by, bw, bh, 0);
    drawDashedOutline(bx, by, bw, bh, perim, offset, green);
    matrix.show();
    delay(flashDelay);
  }

  // color test (commented out block)
  
  {
    const char *c1="COLOR", *c2="TEST";
    int16_t cx1=(sw-strlen(c1)*CHAR_W)/2, cy1=sh/2-CHAR_H,
            cx2=(sw-strlen(c2)*CHAR_W)/2, cy2=sh/2;
    for (uint8_t p=0; p<3; p++){
      uint8_t pr=(p==0)*255, pg=(p==1)*255, pb=(p==2)*255;
      uint8_t invR=255-pr, invG=255-pg, invB=255-pb;
      unsigned long start=millis();
      uint16_t total=sw*sh;
      uint16_t *coords=new uint16_t[total];
      for (uint16_t i=0;i<total;i++) coords[i]=i;
      shuffleArray(coords,total);
      for (uint16_t i=0;i<total;i++){
        matrix.drawPixel(coords[i]%sw,coords[i]/sw,
                         matrix.color565(pr,pg,pb));
        if ((i&0x1F)==0){
          float t=float(millis()-start)/float(hueCycleDuration);
          if (t>1.0) t=1.0;
          uint8_t tr=uint8_t(255+(invR-255)*t),
                  tg=uint8_t(255+(invG-255)*t),
                  tb=uint8_t(255+(invB-255)*t);
          matrix.setTextColor(matrix.color565(tr,tg,tb));
          matrix.setCursor(cx1,cy1); matrix.print(c1);
          matrix.setCursor(cx2,cy2); matrix.print(c2);
          matrix.show();
        }
      }
      shuffleArray(coords,total);
      for (uint16_t i=0;i<total;i++){
        matrix.drawPixel(coords[i]%sw,coords[i]/sw,0);
        if ((i&0x1F)==0) matrix.show();
      }
      delete[] coords;
    }
  }
  

  // checklist phase write-on
  matrix.fillScreen(0);
  delay(100);
  matrix.setTextColor(matrix.color565(255, 255, 255));
  matrix.setCursor((sw - 5 * CHAR_W) / 2, topSpacing);
  matrix.print("MATCH");
  matrix.setCursor((sw - 5 * CHAR_W) / 2, topSpacing + CHAR_H);
  matrix.print("SETUP");
  matrix.show();
  delay(1000);
  for (uint8_t i = 0; i < numChecklist; i++) {
    const char *L = checklistItems[i];
    int16_t ln = strlen(L);
    int16_t ty = topSpacing + CHAR_H + betweenSetupAndPiece + pieceYOffset
                 + i * (CHAR_H * 2 + textBoxGap + itemGap);
    int16_t lx = (sw - ln * CHAR_W) / 2;
    for (uint8_t j = 0; j < ln; j++) {
      int16_t cx = lx + j * CHAR_W;
      matrix.fillRect(cx, ty, CHAR_W, CHAR_H,
                      matrix.color565(255, 255, 255));
      matrix.show();
      delay(typeDelay);
      matrix.fillRect(cx, ty, CHAR_W, CHAR_H, 0);
      matrix.setTextColor(matrix.color565(255, 255, 255));
      matrix.setCursor(cx, ty);
      matrix.print(L[j]);
      matrix.show();
    }
    sweepBoxLR(i, matrix.color565(255, 0, 0));
  }
  delay(postChecklistDelay);
  matrix.setTextColor(matrix.color565(255, 0, 0));
  matrix.setCursor((sw - 3 * CHAR_W) / 2, sh - CHAR_H * 2);
  matrix.print("NOT");
  matrix.setCursor((sw - 5 * CHAR_W) / 2, sh - CHAR_H);
  matrix.print("READY");
  matrix.show();
}

// run boot sequence: process serial updates and manage audio/sponsor/perry based on readiness
void runBootSequence() {
  // read incoming checklist updates
  // The MatrixPortal receives robot messages exclusively over Serial1.  We
  // handle those in handleRobotMessage() from the main loop of
  // perryMatrix.ino.  Calling readAndProcess() on Serial1 here would
  // compete with that reader and lead to broken or duplicate lines.  To
  // avoid this, we only invoke readAndProcess() on the USB Serial for
  // interactive debugging.  Any Serial1 input will be consumed by
  // handleRobotMessage() instead.
  readAndProcess(Serial, "USB");
  // Do not call readAndProcess() with Serial1 here.  handleRobotMessage()
  // will read and dispatch Serial1 messages.

  // if not ready, reset everything and redraw static checklist
  if (!gReadyState) {
    sponsorLaunched = perryActive = audioActive = false;
    readyTimestamp = lastSponsor = 0;
    drawChecklistStatic();
    return;
  }

  // if audio‐vis is running, delegate to it
  if (audioActive) {
    runAudioVisFrame();
    return;
  }

  // sponsor → perry → audio sequence
  if (gReadyState && !sponsorLaunched) {
    // wait 10s after ready to launch sponsor scroller
    if (readyTimestamp == 0) {
      readyTimestamp = millis();
    } else if (millis() - readyTimestamp >= 10000UL) {
      sponsorLaunched = true;
      initSponsorScroller();
    }

  } else if (sponsorLaunched && !perryActive) {
    // run sponsor scroller until cycle wraps, then start Perry loader
    runSponsorScroller();
    if (lastSponsor == sponsorCount - 1 && currentSponsor == 0) {
      perryActive = true;
      initPerryLoader();
    }
    lastSponsor = currentSponsor;

  } else if (perryActive) {
    // Perry loader write → obfuscation → decryption → hold → audio
    if (p_writing) {
      writeEncryptedText4();

    } else if (!p_decrypting) {
      if (millis() >= p_startObf + ODUR) {
        p_decrypting = true;
        p_lastUpdate = millis();
      } else if (millis() - p_lastObf > OSPD) {
        displayRandomText4();
        p_lastObf = millis();
      }

    } else if (p_linesDone < perryLineCount4) {
      // continue decryption animation
      if (millis() - p_lastUpdate > ADEL) {
        updateDecryption4();
      }

    } else {
      // all lines decrypted: hold full text for 2s, then start audio‐vis
      if (p_finalHold == 0) {
        p_finalHold = millis();
      } else if (millis() - p_finalHold >= 3000UL) {
        if (!audioActive) {
          perryActive = false;
          audioActive = true;
          initAudioVis();
        }
      }
    }
  }
}

// handle robot message: consolidate and dispatch serial/usb messages (see header)
void handleRobotMessage() {
  // When dripFeedMode is enabled, consolidate multiple incoming
  // messages into the latest complete line.  Otherwise run the
  // original first‑in/first‑out behaviour.  Keeping the original code
  // intact allows toggling this feature off if desired.
  if (dripFeedMode) {
    // Buffers for Serial1 and optional USB simulation.  These mirror
    // the original static buffers to preserve capacity across calls.
    static char    rxBuf[64];
    static size_t  fill = 0;
#if USB_SIM_INPUT
    static char    usbBuf[64];
    static size_t  ufill = 0;
    bool           usbGot = false;
    char           usbLast[64];
    // Consolidate all available lines on USB Serial into the last
    // complete message.  Only the most recent newline‑terminated line
    // will be dispatched below.
    while (Serial.available()) {
      int b = Serial.read();
      if (b < 0) break;
      char c = (char)b;
      if (c == '\r') continue;
      if (c != '\n') {
        if (ufill < sizeof(usbBuf) - 1) usbBuf[ufill++] = c;
        continue;
      }
      // newline encountered
      usbBuf[ufill] = '\0';
      ufill = 0;
      if (usbBuf[0] != '\0') {
        strncpy(usbLast, usbBuf, sizeof(usbLast));
        usbLast[sizeof(usbLast)-1] = '\0';
        usbGot = true;
      }
    }
#endif
    // Consolidate Serial1 messages in the same fashion
    bool serialGot = false;
    char lastRx[64];
    while (Serial1.available()) {
      int b = Serial1.read();
      if (b < 0) break;
      char c = (char)b;
      if (c == '\r') continue;
      if (c != '\n') {
        if (fill < sizeof(rxBuf) - 1) {
          rxBuf[fill++] = c;
        } else {
          // overflow: ignore until newline
        }
        continue;
      }
      // newline terminator: capture latest non‑blank line
      rxBuf[fill] = '\0';
      fill = 0;
      if (rxBuf[0] != '\0') {
        strncpy(lastRx, rxBuf, sizeof(lastRx));
        lastRx[sizeof(lastRx)-1] = '\0';
        serialGot = true;
      }
    }
    // Dispatch the latest USB simulation line first (if any); this
    // mirrors the original order of handling USB before Serial1.  Then
    // dispatch the latest Serial1 line.  Only one line from each
    // source is processed per call, eliminating backlog.
#if USB_SIM_INPUT
    if (usbGot) {
      parseAndDispatchLine(usbLast, "USB SIM (drip) -> ");
    }
#endif
    if (serialGot) {
      parseAndDispatchLine(lastRx, "Serial1 (drip) -> ");
    }
  } else {
    // Serial1-only, line-safe reader/dispatcher
    static char rxBuf[64];  // Adjust if you add more fields
    static size_t fill = 0;

#if USB_SIM_INPUT
    // --- USB simulation (type: "0 1,1,0,1" + Enter in Serial Monitor) ---
    static char    usbBuf[64];
    static size_t  ufill = 0;

    while (Serial.available()) {
      int b = Serial.read();
      if (b < 0) break;
      char c = (char)b;

      if (c == '\r') continue;             // ignore CR
      if (c != '\n') {
        if (ufill < sizeof(usbBuf) - 1) usbBuf[ufill++] = c;
        continue;                          // accumulate until newline
      }

      // newline -> terminate and dispatch
      usbBuf[ufill] = '\0';
      ufill = 0;
      if (usbBuf[0] != '\0') {
        parseAndDispatchLine(usbBuf, "USB SIM -> ");
      }
    }
#endif

    while (Serial1.available()) {
      int b = Serial1.read();
      if (b < 0) break;
      char c = (char)b;

      if (c == '\r') continue;  // ignore CR; trigger on LF
      if (c != '\n') {
        if (fill < sizeof(rxBuf) - 1) {
          rxBuf[fill++] = c;
        } else {
          // overflow: drop until newline
        }
        continue;
      }

      // newline -> terminate current line
      rxBuf[fill] = '\0';
      fill = 0;

      if (rxBuf[0] == '\0') continue;  // blank line, ignore

      // Debug print of exactly one full line from Serial1
      Serial.print("Msg from Serial1: ");
      Serial.println(rxBuf);

      // ---- Parse "<mode> <payload>" ----
      char *p = rxBuf;
      while (*p == ' ') ++p;

      char *endMode = nullptr;
      long modeVal = strtol(p, &endMode, 10);
      if (p == endMode) continue;  // no digits parsed -> malformed

      int8_t newMode = (int8_t)modeVal;

      // Skip spaces; payload is remainder (or nullptr if none)
      char *payload = endMode;
      while (*payload == ' ') ++payload;
      if (*payload == '\0') payload = nullptr;

      // ---- Mode switch / update (your original logic) ----
      if (newMode != currentMode) {
        lastMode = currentMode;
        currentMode = Mode(newMode);
        audioActive = sponsorLaunched = perryActive = false;

        if (currentMode == MODE_CHECKLIST) {
          if (payload) processChecklistPayload(payload);
        } else if (currentMode == MODE_AUTONOMOUS) {
          initAutonomous();
        } else if (currentMode == MODE_DYNAMIC) {
          initDynamic();
          if (payload) updateDynamicFromPayload(payload);
        } else {
          matrix.fillScreen(0);
          matrix.show();
        }
      } else {
        if (currentMode == MODE_CHECKLIST && payload) {
          processChecklistPayload(payload);
        } else if (currentMode == MODE_DYNAMIC && payload) {
          updateDynamicFromPayload(payload);
        }
        // autonomous ignores payload updates
      }
    }
  }
}
