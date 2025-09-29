// © 2025 SC5K Systems

#include "src/shutdown.h"
#include "src/matrix_config.h"
#include "src/globals.h"
#include <Fonts/TomThumb.h>

#include <Arduino.h>
#include <string.h>

// match over screen: draw header and dashed line; scroll fake code using ring buffer

static const char *const fakeCode[] = {
  "// shutting down subsystems...",
  "void disableMotors() {",
  "  motorA.stop();",
  "  motorB.stop();",
  "}",
  "saveLogs(\"match_logs.txt\");",
  "for(int i=0;i<5;i++){",
  "  delay(100);",
  "  Serial.println(i);",
  "}",
  "printf(\"Goodbye!\");",
  "system(\"poweroff\");",
  "// cleanup complete\n",
  "Robot.disable();",
  "// standby...",
  "for(;;){}",
  "// freeing memory...",
  "int status=checkStatus();",
  "if(status){",
  "  handle(status);",
  "}",
  "for(uint8_t j=0;j<10;j++){",
  "  update(j);",
  "}",
  "if(isError()){",
  "  logError();",
  "}",
  "shutdownMotors();",
  "System.reset();",
  "// done",
  // extra dummy code for more variety
  "void resetSystem() {",
  "  config.save();",
  "  delay(200);",
  "}",
  "for(uint8_t m=0; m<3; m++){",
  "  calibrateSensor(m);",
  "}",
  "while(flag){",
  "  processData();",
  "}",
  "return 0;"
};
static const uint8_t fakeCodeCount = sizeof(fakeCode) / sizeof(fakeCode[0]);

// Static state for the shutdown animation
static bool shutdownInitDone = false;
static unsigned long lastCharMillis = 0;
// delay between chars in ms for the typing effect
static const unsigned long charDelay = 30UL; // ms per char
static unsigned long hdrAnimStart = 0;
static uint8_t       hdrAnimPhase = 0; // 0=slide in,1=hold,2=slide out,3=wait
static const unsigned long hdrSlideDuration = 400UL;
static const unsigned long hdrHoldDuration  = 5000UL;
static const unsigned long hdrWaitDuration  = 1000UL;
static int16_t shutdownCodeStartY = 0;
static uint8_t linesSinceBlank = 0;
static uint8_t maxLinesBeforeBlank = 2;
static uint8_t currentStringIdx = 0;
static uint8_t currentCharIdx   = 0;
static const uint8_t MAX_ROWS = 24;
static const uint8_t MAX_COLS = 16;
static char buffer[MAX_ROWS][MAX_COLS + 1];
static uint8_t bufferRows = 0;
static uint8_t bufferCols = 0;
static uint8_t curRow  = 0;
static uint8_t curCol  = 0;

// Forward declaration
static void shiftBufferUp();

void initShutdown() {
  // Reset state only once per transition into shutdown mode
  shutdownInitDone = true;
  lastCharMillis = millis();
  currentStringIdx = 0;
  currentCharIdx   = 0;
  int16_t yMatch = 1;
  int16_t yDash  = yMatch + CHAR_H * 2 + 1;
  const int16_t ttCharH = 5;
  const int16_t ttRowH  = ttCharH + 1;
  int16_t topLimit = yDash + ttRowH;
  // Estimate characters per row: each char is 3 pixels wide plus a 1 pixel gap
  const int16_t ttCharW = 3;
  const int16_t ttColW  = ttCharW + 1;
  // topLimit.
  int16_t availH = matrix.height() - topLimit;
  if (availH < 0) availH = 0;
  // characters, include it.
  int16_t rows = 0;
  if (availH >= ttCharH) {
    rows = availH / ttRowH;
    // if leftover height can accommodate a character row, add one
    if ((availH % ttRowH) >= ttCharH) {
      rows++;
    }
  } else {
    rows = 1;
  }
  if (rows > (int)MAX_ROWS) rows = (int)MAX_ROWS;
  if (rows < 1) rows = 1;
  int16_t startCandidate = matrix.height() - rows * ttRowH + (ttRowH - ttCharH);
  // If this would overlap the header region (above topLimit),
  // reduce the row count until it fits.  Ensure at least one row
  // remains.
  while (rows > 0 && startCandidate < topLimit) {
    rows--;
    startCandidate = matrix.height() - rows * ttRowH + (ttRowH - ttCharH);
  }
  if (rows < 1) {
    rows = 1;
    startCandidate = matrix.height() - ttRowH + (ttRowH - ttCharH);
  }
  bufferRows = (uint8_t)rows;
  shutdownCodeStartY = startCandidate;
  // Compute number of columns that fit across the matrix width
  bufferCols = (uint8_t)min((int)(matrix.width() / ttColW), (int)MAX_COLS);
  // Initialise buffer with spaces and null terminators
  for (uint8_t r = 0; r < bufferRows; r++) {
    for (uint8_t c = 0; c < bufferCols; c++) {
      buffer[r][c] = ' ';
    }
    if (bufferCols < MAX_COLS) {
      buffer[r][bufferCols] = '\0';
    } else {
      buffer[r][MAX_COLS] = '\0';
    }
  }
  curRow = 0;
  curCol = 0;
  // Reset animation phases and counters
  hdrAnimStart = millis();
  hdrAnimPhase = 0;
  linesSinceBlank = 0;
  // Choose an initial number of lines before inserting the next blank.
  maxLinesBeforeBlank = 2 + (uint8_t)random(0, 2);
}

// Shift all lines in the buffer up by one row, discarding the first
// row and clearing the last row.  Called when the buffer overflows.
static void shiftBufferUp() {
  for (uint8_t r = 0; r + 1 < bufferRows; r++) {
    memcpy(buffer[r], buffer[r + 1], bufferCols);
    buffer[r][bufferCols] = '\0';
  }
  // Clear last row
  for (uint8_t c = 0; c < bufferCols; c++) {
    buffer[bufferRows - 1][c] = ' ';
  }
  buffer[bufferRows - 1][bufferCols] = '\0';
}

void runShutdownFrame() {
  // Ensure init has been called
  if (!shutdownInitDone) {
    initShutdown();
  }
  // Simulate typing characters into the buffer at charDelay ms intervals
  unsigned long now = millis();
  while (now - lastCharMillis >= charDelay) {
    lastCharMillis += charDelay;
    // Fetch current string and character
    const char *str = fakeCode[currentStringIdx];
    char ch = str[currentCharIdx++];
    if (ch == '\0') {
      // End of current string: move to next string and insert a newline
      currentStringIdx = (currentStringIdx + 1) % fakeCodeCount;
      currentCharIdx = 0;
      // Force newline
      ch = '\n';
    }
    if (ch == '\n') {
      // Move to next line
      curRow++;
      curCol = 0;
      if (curRow >= bufferRows) {
        shiftBufferUp();
        curRow = bufferRows - 1;
      }
      linesSinceBlank++;
      if (linesSinceBlank >= maxLinesBeforeBlank) {
        // Insert a blank row
        curRow++;
        curCol = 0;
        if (curRow >= bufferRows) {
          shiftBufferUp();
          curRow = bufferRows - 1;
        }
        for (uint8_t c = 0; c < bufferCols; c++) {
          buffer[curRow][c] = ' ';
        }
        // Reset counter and choose next threshold (2 or 3 lines)
        linesSinceBlank = 0;
        maxLinesBeforeBlank = 2 + (uint8_t)random(0, 2);
      }
    } else {
      // Regular character: insert into buffer
      if (curCol >= bufferCols) {
        // wrap to next line
        curRow++;
        curCol = 0;
        if (curRow >= bufferRows) {
          shiftBufferUp();
          curRow = bufferRows - 1;
        }
      }
      buffer[curRow][curCol] = ch;
      curCol++;
    }
  }
  // Draw the shutdown screen
  matrix.fillScreen(0);
  // renderer.
  const char* hdr1 = "MATCH";
  const char* hdr2 = "OVER";
  int matchLen = (int)strlen(hdr1);
  int overLen  = (int)strlen(hdr2);
  int matchWidth = matchLen * CHAR_W - 1;
  int overWidth  = overLen  * CHAR_W - 1;
  // Centred positions for each word
  int16_t matchMidX = (matrix.width() - matchWidth) / 2;
  int16_t overMidX  = (matrix.width() - overWidth) / 2;
  // Starting (off-screen) positions
  int16_t matchStartInX = -matchWidth;
  int16_t overStartInX  = matrix.width();
  // Ending (off-screen) positions when sliding out
  int16_t matchEndOutX  = matrix.width();
  int16_t overEndOutX   = -overWidth;
  unsigned long nowMs   = millis();
  unsigned long hdrDt   = nowMs - hdrAnimStart;
  float p;
  int16_t matchX;
  int16_t overX;
  if (hdrAnimPhase == 0) {
    // slide in
    p = (float)hdrDt / (float)hdrSlideDuration;
    if (p >= 1.0f) {
      p = 1.0f;
      hdrAnimPhase = 1;
      hdrAnimStart = nowMs;
    }
    matchX = matchStartInX + (int)((matchMidX - matchStartInX) * p);
    overX  = overStartInX  + (int)((overMidX  - overStartInX)  * p);
  } else if (hdrAnimPhase == 1) {
    // hold centred
    matchX = matchMidX;
    overX  = overMidX;
    if (hdrDt >= hdrHoldDuration) {
      hdrAnimPhase = 2;
      hdrAnimStart = nowMs;
    }
  } else if (hdrAnimPhase == 2) {
    // slide out
    p = (float)hdrDt / (float)hdrSlideDuration;
    if (p >= 1.0f) {
      p = 1.0f;
      hdrAnimPhase = 3;
      hdrAnimStart = nowMs;
    }
    matchX = matchMidX + (int)((matchEndOutX - matchMidX) * p);
    overX  = overMidX  + (int)((overEndOutX  - overMidX)  * p);
  } else {
    // wait off‑screen
    matchX = matchEndOutX;
    overX  = overEndOutX;
    if (hdrDt >= hdrWaitDuration) {
      hdrAnimPhase = 0;
      hdrAnimStart = nowMs;
    }
  }
  // Draw header text on separate rows.  Red text with default
  // font sizes.  Dashed line drawn below.
  matrix.setFont();
  uint16_t redCol   = matrix.color565(255,0,0);
  uint16_t white    = matrix.color565(255,255,255);
  matrix.setTextColor(redCol);
  int16_t yMatchHdr = 1;
  int16_t yOverHdr  = yMatchHdr + CHAR_H;
  matrix.setCursor(matchX, yMatchHdr);
  matrix.print(hdr1);
  matrix.setCursor(overX,  yOverHdr);
  matrix.print(hdr2);
  // Dashed line below header, always centred and white
  int16_t yDash = yMatchHdr + CHAR_H * 2 + 1;
  for (int x = 0; x < matrix.width(); x += 3) {
    matrix.drawPixel(x, yDash, white);
  }
  // Prepare to draw code: set TomThumb font and green text colour
  matrix.setFont(&TomThumb);
  matrix.setTextColor(matrix.color565(0,255,0));
  // Each row uses a height of 5 pixels plus a 1‑pixel gap.
  const int16_t ttRowH = 5 + 1;
  for (uint8_t r = 0; r < bufferRows; r++) {
    int16_t y = shutdownCodeStartY + (int16_t)r * ttRowH;
    matrix.setCursor(0, y);
    matrix.print(buffer[r]);
  }
  // Restore default font for any subsequent drawing outside shutdown
  matrix.setFont();
  matrix.show();
}