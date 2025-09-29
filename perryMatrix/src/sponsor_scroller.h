// © 2025 SC5K Systems

#pragma once

#include <Arduino.h>
#include "matrix_config.h"
#include "globals.h"
#include "helpers.h"

// drawStaticHeader: draw "spon-" and "-sors" at top plus dotted divider
void drawStaticHeader();

// wheel: map 0–255 to rainbow colour (red→green→blue)
uint16_t wheel(uint8_t pos);

// initSponsorScroller: seed random and initialise first sponsor position
void initSponsorScroller();

// runSponsorScroller: scroll sponsor up, draw header and cycle to next
void runSponsorScroller();
