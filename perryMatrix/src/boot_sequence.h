// © 2025 SC5K Systems

#pragma once
#include <Arduino.h>

// handleRobotMessage: read serial, parse mode/payload and dispatch
void handleRobotMessage();

// initBootSequence: run startup animations (splash, options, outline, LED blink, colour test) then draw initial checklist
void initBootSequence();

// runBootSequence: in checklist mode, run sponsor scroller, perry loader and audio vis based on state
void runBootSequence();
