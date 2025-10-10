// © 2025 SC5K Systems

#pragma once
#include <Arduino.h>           // pinMode, A1/A2, etc.
#include <Adafruit_GFX.h>
#include <Adafruit_Protomatter.h>

// matrix dimensions (pixels)
#define WIDTH   128
#define HEIGHT  32

// character cell size; defines CHAR_W and CHAR_H constants
extern const uint8_t charW;
extern const uint8_t charH;
#define CHAR_W charW
#define CHAR_H charH

// audio visualizer pins and limits: MIC_PIN, GAIN_PIN, MAX_BAR_HEIGHT
#define MIC_PIN         A1
#define GAIN_PIN        A2
#define MAX_BAR_HEIGHT  16

// rgb matrix pin mappings: rgbPins, addrPins, clock, latch, OE
extern uint8_t rgbPins[];
extern uint8_t addrPins[];
extern uint8_t clockPin, latchPin, oePin;

// shared serial buffer pointer (allocated in .cpp)
extern char *buf;

// primary matrix object constructed with WIDTH×HEIGHT
extern Adafruit_Protomatter matrix;
