// © 2025 SC5K Systems

#pragma once
#include <Arduino.h>
#include "matrix_config.h"  // matrix instance, WIDTH, HEIGHT
#include "globals.h"        // FFT object, samples, barHeights[], etc.

// readFFT: sample mic, perform FFT and map bins into bar heights and flags
void readFFT();

// updatePeaks: update and decay peak levels per bar
void updatePeaks();

// getBarColor: return gradient colour based on y position and bar height
uint16_t getBarColor(int y, int h);

// drawWaveform: plot raw audio waveform across the display
void drawWaveform();

// drawBars: draw bars for each FFT bin and peak markers
void drawBars();

// initAudioVis: set pins, clear buffers, enable audio and clear screen
void initAudioVis();

// runAudioVisFrame: process one frame of the visualizer when active
void runAudioVisFrame();
