// © 2025 SC5K Systems

#pragma once
#include "globals.h"
#include <Arduino.h>

// resetStar: center star with random heading and speed
void resetStar(Star &s);

// initAutonomous: activate autonomous mode; clear screen, reset timers, seed stars/circles and compute auto lock box
void initAutonomous();

// runAutonomousFrame: render one frame; toggle text, update circles/stars and draw auto lock box
void runAutonomousFrame();
