// © 2025 SC5K Systems

#pragma once

#include <Arduino.h>
#include "matrix_config.h"
#include "globals.h"

// initPerryLoader: setup loader animation (measure lengths, reset indices, set timing and clear)
void initPerryLoader();

// getRandomCharacterP: return a random printable ASCII char (33–126)
char getRandomCharacterP();

// writeEncryptedText4: type perry lines one char at a time as random glyphs
void writeEncryptedText4();

// displayRandomText4: render perry lines with decrypted letters (blue) and random glyphs (gold)
void displayRandomText4();

// updateDecryption4: reveal one random char per call across lines and refresh display
void updateDecryption4();
