// © 2025 SC5K Systems

#include "src/matrix_config.h"
#include <Arduino.h>

// char dimensions: 6x8 pixels
const uint8_t charW = 6;
const uint8_t charH = 8;

// matrix wiring pins
uint8_t rgbPins[]  = {7,  8,  9, 10, 11, 12};
uint8_t addrPins[] = {17, 18, 19, 20, 21};
uint8_t clockPin   = 14;
uint8_t latchPin   = 15;
uint8_t oePin      = 16;

// serial buffer pointer (allocated in .ino)
char *buf = nullptr;

// protomatter matrix instance
Adafruit_Protomatter matrix(
  WIDTH, 6, 1,
  rgbPins, 4, addrPins,
  clockPin, latchPin, oePin,
  true
);
