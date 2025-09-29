// © 2025 SC5K Systems
#pragma once
#include <stdint.h>

// initDynamic: init i2c accelerometer, seed node network, reset filters and flags
void initDynamic();

// updateDynamicFromPayload: parse req,intake,score,climb flags (new format) or legacy accel,ai,cube; update dynamic UI and handle edge detection
void updateDynamicFromPayload(const char* payload);

// runDynamicFrame: render dynamic mode (network, accel strip, AI/on boxes, request banners, optional cube)
void runDynamicFrame();
