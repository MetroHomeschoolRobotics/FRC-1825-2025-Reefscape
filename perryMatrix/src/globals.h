// © 2025 SC5K Systems

#pragma once
#include <stdint.h>
#include <Arduino.h>
#include <Adafruit_LIS3DH.h>
#include "arduinoFFT.h"

// application modes: track current and last display mode
enum Mode : int8_t {
  MODE_NULL       = -1,
  MODE_CHECKLIST  =  0,
  MODE_AUTONOMOUS =  1,
  MODE_DYNAMIC    =  2,
  // Shutdown/match end mode.  When a message with mode '3' is
  // received, the display shows a MATCH OVER splash with a
  // scrolling fake code animation.
  MODE_SHUTDOWN   =  3
};
extern Mode currentMode, lastMode;

// dynamic mode config: segment heights, node array, LIS3DH and cube parameters
struct Node { int16_t x, y; int8_t dx, dy; };
extern const int16_t SEG_TOP_H, SEG_MID_H, SEG_BOT_H;
extern const uint8_t NODE_COUNT;
extern Node       nodes[];
extern uint8_t    netFrame;
extern Adafruit_LIS3DH lis;
extern const float SMOOTHING, ANGLE_THRESHOLD;
extern float       filtRoll, filtPitch;
extern float       accX, accY, accZ;
extern bool        showAccel, showAI, showCube;
extern const float CUBE_SIZE, CAMERA_DIST, FOV;
extern const float VERTS[8][3];
extern const uint8_t EDGES[12][2];
extern const float AXIS_V[3][3];

// checklist data: item labels, counts, previous states, ready flag and timestamp
extern const char*   checklistItems[];
extern const uint8_t numChecklist;
extern int           prevChecklist[];
extern bool          gReadyState;
extern unsigned long readyTimestamp;

// sponsor scroller config: names, count, positions, hue and launch flag
extern const char*   sponsors[];
extern const uint8_t sponsorCount;
extern int16_t       currentSponsor, yOffset, sponsorX;
extern uint8_t       hueOffset;
extern bool          sponsorLaunched;

// perry loader timing constants: pause, write delay, obfuscation speed, duration
extern const uint16_t IPAU;   
extern const uint16_t WDEL;   
extern const uint16_t OSPD;   
extern const uint16_t ADEL;   
extern const uint16_t ODUR;   

// perry loader state: flags, last sponsor, lines, lengths, decrypt flags, indices, timers and layout
extern bool          perryActive;
extern int           lastSponsor;
extern const char*   perryLines4[];
extern const uint8_t perryLineCount4;
extern uint8_t       perryLens4[];
extern bool          perryDec[][16];
extern uint8_t       perryDone[], perryWriteIdx4[];
extern unsigned long p_lastUpdate, p_lastObf, p_startObf, p_finalHold;
extern bool          p_writing, p_decrypting;
extern uint8_t       p_linesDone;
extern int16_t       p_yStart, p_lineGap;

// audio visualizer state and config: FFT, flags, timing, sample buffers, bar metrics, peaks and smoothing
extern ArduinoFFT<double> FFT;
extern bool               audioActive;
extern unsigned long      audioStartTime;
extern const unsigned long audioDuration;
extern const uint16_t     samples;
extern const double       samplingFrequency;
extern const unsigned long sampleDelay;
extern double             vReal[], vImag[];
extern int                barHeights[];
extern bool               redlined[];
extern int                peakLevels[];
extern unsigned long      peakTimes[];
extern const int          smoothingFactor;
extern double             smoothedInput[];
extern const int          WAVE_X_SHIFT;

// timing and layout constants for boot/animations/checklist: delays, counts, speeds and spacing
extern const unsigned long bootDelay,
                            dashDelayBefore,
                            dashedPhaseDuration,
                            typeDelay,
                            postOptionsDelay,
                            flashDelay,
                            hueCycleDuration,
                            postChecklistDelay,
                            boxDelay;
extern const uint8_t      flashCount;
extern const uint16_t     animDelayP,
                            obfSpeedP,
                            obfDurationP,
                            pauseP,
                            loadDurP,
                            writeDelayP;
extern const int          SCROLL_SPEED,
                            FRAME_DELAY;
extern const uint8_t      HUE_DELTA,
                            SEP_Y;
extern const uint8_t      topSpacing,
                            betweenSetupAndPiece,
                            textBoxGap,
                            itemGap,
                            pieceYOffset,
                            chkBoxW,
                            chkBoxH;

// autonomous mode config: timers, intervals, state flags, text and geometry
extern unsigned long autoTextPrev,
                     textInterval,
                     autoStarPrev,
                     starInterval,
                     startTime,
                     animationDuration;
extern bool        autoState, autoActive;
extern const char* line1, *line2;
extern int16_t     tX1, tX2, tY1, tY2, boxX, boxY, boxW, boxH;

// starfield: star struct/array and dynamic circle radii
#define MAX_STARS 50
struct Star { int16_t xInt, yInt; float accX, accY, stepX, stepY; };
extern Star   stars[];
extern const int DYN_CIRCLES, maxDynRadius;
extern int   dynRadius[];

// request/intake ui state
extern bool         dynReqPiece;
extern bool         dynReqState;
extern unsigned long dynReqPrev;
extern uint16_t     dynReqInterval;

extern bool         dynHasPiece;
extern bool         dynHasBlink;
extern unsigned long dynHasPrev;
extern bool         dynReqTextVisible;

// ai strip state
extern bool         dynAiState;
extern unsigned long dynAiPrev;
extern uint16_t     dynAiInterval;

// serial line buffer: 64-byte storage for incoming commands
extern char lineBuf[64];

// dripFeedMode flag: when true, process only the latest complete serial line per loop
extern bool dripFeedMode;
