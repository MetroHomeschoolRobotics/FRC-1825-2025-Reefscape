// © 2025 SC5K Systems

#include "src/globals.h"
#include "src/matrix_config.h"
#include "arduinoFFT.h"
#include <Arduino.h>
#include <math.h>
#include <Fonts/TomThumb.h>

// current and last app mode (initially null until serial command)
Mode currentMode = MODE_NULL;
Mode lastMode    = MODE_NULL;

// dynamic mode config: segment heights, node array, LIS3DH state, filters, accelerometer & cube settings
const int16_t SEG_TOP_H   = 48;
const int16_t SEG_MID_H   = 32;
const int16_t SEG_BOT_H   = 48;
const uint8_t NODE_COUNT  = 12;
Node       nodes[NODE_COUNT];
uint8_t    netFrame       = 0;
Adafruit_LIS3DH lis;
const float SMOOTHING       = 0.2f;
const float ANGLE_THRESHOLD = M_PI/2.0f;
float       filtRoll  = 0.0f;
float       filtPitch = 0.0f;
float       accX = 0.0f, accY = 0.0f, accZ = 0.0f;
bool        showAccel = false;
bool        showAI    = false;
bool        showCube  = false;
const float CUBE_SIZE   = 4.0f;
const float CAMERA_DIST = 60.0f;
const float FOV         = 200.0f;
const float VERTS[8][3] = {
  {-1,-1,-1},{ 1,-1,-1},{ 1, 1,-1},{-1, 1,-1},
  {-1,-1, 1},{ 1,-1, 1},{ 1, 1, 1},{-1, 1, 1}
};
const uint8_t EDGES[12][2] = {
  {0,1},{1,2},{2,3},{3,0},
  {4,5},{5,6},{6,7},{7,4},
  {0,4},{1,5},{2,6},{3,7}
};
const float AXIS_V[3][3] = {
  {1.5f,0,0},{0,1.5f,0},{0,0,1.5f}
};

// checklist defaults: item labels, counts, previous values and ready flag
const char*   checklistItems[] = { "PIECE","ELEV","ANGLE","FMS" };
const uint8_t numChecklist      = 4;
int           prevChecklist[4]  = {-1,-1,-1,-1};
bool          gReadyState       = false;
unsigned long readyTimestamp    = 0;

// sponsor scroller state: names, count, current index/position/hue and launch flag
const char*   sponsors[]    = {
  "Aechelon Technology","Horsey Tech","Lutheran High School",
  "Gene Haas Foundation","Huddleston Movers","SC5K Systems"
};
const uint8_t sponsorCount  = sizeof(sponsors)/sizeof(*sponsors);
int16_t       currentSponsor= 0, yOffset=0, sponsorX=0;
uint8_t       hueOffset     = 0;
bool          sponsorLaunched = false;

// perry loader timing constants for pacing and duration
const uint16_t IPAU = 1000;
const uint16_t WDEL = 100;
const uint16_t OSPD = 50;
const uint16_t ADEL = 80;
const uint16_t ODUR = 5000;

// perry loader runtime state: active flag, current sponsor, lines/lengths, decryption flags, indices and timers
bool    perryActive   = false;
int     lastSponsor   = -1;
const char* perryLines4[]    = { "Perry","The","Peri-","Scope" };
const uint8_t perryLineCount4 = 4;
uint8_t      perryLens4[4]    = {0};
bool         perryDec[4][16]  = {{false}};
uint8_t      perryDone[4]     = {0};
uint8_t      perryWriteIdx4[4]= {0};
unsigned long p_lastUpdate=0, p_lastObf=0, p_startObf=0, p_finalHold=0;
bool    p_writing   = true;
bool    p_decrypting= false;
uint8_t p_linesDone = 0;
int16_t p_yStart    = 0;
int16_t p_lineGap   = 0;

// audio visualizer state: FFT, audio flags, timing, sample buffers, bar metrics, peaks and smoothing
ArduinoFFT<double> FFT(vReal, vImag, samples, samplingFrequency);
bool        audioActive      = false;
unsigned long audioStartTime= 0;
const unsigned long audioDuration = 15000UL;
const uint16_t samples         = 256;
const double   samplingFrequency = 16000.0;
const unsigned long sampleDelay  = 1000000UL/samplingFrequency;
double      vReal[samples]       = {0}, vImag[samples] = {0};
int         barHeights[WIDTH/3]  = {0};
bool        redlined[WIDTH/3]    = {false};
int         peakLevels[WIDTH/3]  = {0};
unsigned long peakTimes[WIDTH/3] = {0};
const int   smoothingFactor      = 4;
double      smoothedInput[samples] = {0};
const int   WAVE_X_SHIFT         = 0;

// timing and layout constants for boot screens, animations and spacing
const unsigned long bootDelay            = 1000;
const unsigned long dashDelayBefore      = 1000;
const unsigned long dashedPhaseDuration = 500;
const unsigned long typeDelay            = 100;
const unsigned long postOptionsDelay     = 2000;
const unsigned long flashDelay           = 100;
const uint8_t     flashCount            = 6;
const unsigned long hueCycleDuration    = 2000;
const unsigned long postChecklistDelay  = 500;
const unsigned long boxDelay            = 200;
const uint16_t animDelayP   = 80;
const uint16_t obfSpeedP    = 50;
const uint16_t obfDurationP = 5000;
const uint16_t pauseP       = 1000;
const uint16_t loadDurP     = 3000;
const uint16_t writeDelayP  = 100;
const int       SCROLL_SPEED = 1;
const int       FRAME_DELAY  = 5;
const uint8_t   HUE_DELTA    = 8;
const uint8_t   SEP_Y        = 17;
const uint8_t   topSpacing   = 4;
const uint8_t   betweenSetupAndPiece = 2;
const uint8_t   textBoxGap   = 0;
const uint8_t   itemGap      = 6;
const uint8_t   pieceYOffset = 10;
const uint8_t   chkBoxW      = 30;
const uint8_t   chkBoxH      = 8;

// autonomous mode data: timers, intervals, state flags, display text and positions
unsigned long autoTextPrev      = 0;
unsigned long textInterval      = 325;
unsigned long autoStarPrev      = 0;
unsigned long starInterval      = 40;
unsigned long startTime         = 0;
unsigned long animationDuration = 120000;
bool   autoState                = false;
bool   autoActive               = false;
const char* line1               = "AUTO";
const char* line2               = "LOCK";
int16_t tX1=0, tX2=0, tY1=0, tY2=0, boxX=0, boxY=0, boxW=0, boxH=0;

// starfield: preallocate stars array and dynamic circle radii
Star    stars[MAX_STARS]       = {};
const int DYN_CIRCLES          = 2;
int       dynRadius[DYN_CIRCLES] = {0};
const int maxDynRadius         = 50;

// ── dynamic ui flags & timers (request/intake/ai) ────────────────
// request banner (FETCH/PIECE)
bool         dynReqPiece    = false;   // robot requests a piece
bool         dynReqState    = false;   // blink state for request swap
unsigned long dynReqPrev    = 0;       // last toggle time
uint16_t     dynReqInterval = 325;     // ms; matches AI swap cadence by default

// intake override (piece detected in intake)
bool         dynHasPiece      = false; // overrides request
bool         dynHasBlink      = false; // 2× blink for intake text-only
unsigned long dynHasPrev      = 0;     // last 2× toggle time
bool         dynReqTextVisible = true; // hide text after tube finishes

// ai strip swap (“AI” | “ON”)
bool         dynAiState     = false;   // blink state
unsigned long dynAiPrev     = 0;       // last toggle time
uint16_t     dynAiInterval  = 325;     // ms

// serial command buffer for incoming messages
char lineBuf[64] = {};

/*
 * When dripFeedMode is enabled, incoming serial messages are handled in
 * a last‑one‑wins fashion.  Multiple updates received in a single
 * iteration of handleRobotMessage() or readAndProcess() will have
 * earlier lines dropped in favour of the most recent complete
 * message.  This reduces backlog when sensors oscillate rapidly
 * between states.  Set to true by default; may be toggled at runtime
 * via Serial commands if desired.
 */
bool dripFeedMode = true;
