// © 2025 SC5K Systems

#pragma once

#include <stdint.h>
#include <Arduino.h>
#include <stdlib.h>
#include <math.h>
#include "matrix_config.h"

// getRandomChar: return a random printable ASCII char (33–126) for obfuscation
inline char getRandomChar() {
  return (char)random(33, 127);
}

// color565: wrap matrix.color565 to convert 8-bit RGB to 16-bit 565
inline uint16_t color565(uint8_t r, uint8_t g, uint8_t b) {
  return matrix.color565(r, g, b);
}

// shuffleArray: in-place fisher–yates shuffle on a uint16_t array
inline void shuffleArray(uint16_t *arr, uint16_t n) {
  for (uint16_t i = n - 1; i > 0; i--) {
    uint16_t j = random(0, i + 1);
    uint16_t t = arr[i];
    arr[i] = arr[j];
    arr[j] = t;
  }
}

// hsvToRgb: convert HSV (0–360°,0–1,0–1) to 0–255 RGB values
inline void hsvToRgb(float h, float s, float v,
                     uint8_t &r, uint8_t &g, uint8_t &b) {
  int   i = int(h / 60.0f) % 6;
  float f = (h / 60.0f) - i;
  float p = v * (1 - s);
  float q = v * (1 - f * s);
  float t = v * (1 - (1 - f) * s);
  float rf, gf, bf;
  switch (i) {
    case 0: rf = v;  gf = t;  bf = p;  break;
    case 1: rf = q;  gf = v;  bf = p;  break;
    case 2: rf = p;  gf = v;  bf = t;  break;
    case 3: rf = p;  gf = q;  bf = v;  break;
    case 4: rf = t;  gf = p;  bf = v;  break;
    default:rf = v;  gf = p;  bf = q;  break;
  }
  r = uint8_t(rf * 255);
  g = uint8_t(gf * 255);
  b = uint8_t(bf * 255);
}
