// © 2025 SC5K Systems

#pragma once
#include <Arduino.h>
#include <Stream.h>
#include <stdint.h>

// drawChecklistStatic: render the full checklist (header, items with boxes, bottom status)
void drawChecklistStatic();

// processChecklistPayload: parse CSV flags, update boxes and ready status, animate changes
void processChecklistPayload(const char* payload);

// readAndProcess: read a line from stream, log it and process the payload
void readAndProcess(Stream &in, const char *label);

// sweepBoxLR: animate box fill left→right at given index
void sweepBoxLR(uint8_t idx, uint16_t color);

// sweepBoxRL: animate box fill right→left at given index
void sweepBoxRL(uint8_t idx, uint16_t color);

// drawDashedOutline: draw dashed rectangle at (bx,by) of size (bw,bh) with offset pattern and colour
void drawDashedOutline(int bx, int by, int bw, int bh,
                       int perim, int offset, uint16_t color);