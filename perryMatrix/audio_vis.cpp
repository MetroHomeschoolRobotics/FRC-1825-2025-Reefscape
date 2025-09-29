// © 2025 SC5K Systems

#include "src/audio_vis.h"
#include "src/helpers.h"
#include "src/checklist.h"
#include "arduinoFFT.h"

// readFFT
void readFFT() {
  unsigned long nextMicros = micros();
  for (int i = 0; i < samples; i++) {
    while (micros() < nextMicros);
    double raw = analogRead(MIC_PIN);
    raw = min(raw, 1023.0);
    smoothedInput[i] = (smoothedInput[i] * (smoothingFactor - 1) + raw) / smoothingFactor;
    vReal[i] = smoothedInput[i];
    vImag[i] = 0;
    nextMicros += sampleDelay;
  }
  FFT.windowing(FFTWindow::Hamming, FFTDirection::Forward);
  FFT.compute(FFTDirection::Forward);
  FFT.complexToMagnitude();
  for (int i = 2; i < (WIDTH/3) + 2; i++) {
    int idx = i - 2;
    int h = map((int)vReal[i], 0, 1023, 1, MAX_BAR_HEIGHT);
    barHeights[idx] = min(h, MAX_BAR_HEIGHT);
    redlined[idx]   = (barHeights[idx] > MAX_BAR_HEIGHT * 0.6);
  }
}

// updatePeaks
void updatePeaks() {
  unsigned long now = millis();
  for (int i = 0; i < WIDTH/3; i++) {
    if (barHeights[i] > peakLevels[i]) {
      peakLevels[i] = barHeights[i];
      peakTimes[i]  = now;
    } else if (now - peakTimes[i] > 50) {
      if (peakLevels[i] > barHeights[i] && now - peakTimes[i] > 10) {
        peakLevels[i]--;
        peakTimes[i] = now;
      }
    }
  }
}

// getBarColor
uint16_t getBarColor(int y, int h) {
  int seg = h / 3;
  if (y < seg) {
    return matrix.color565(map(y, 0, seg, 128, 0), 0, 255);
  } else if (y < 2 * seg) {
    return matrix.color565(
      0,
      map(y, seg, 2*seg, 0, 255),
      map(y, seg, 2*seg, 255, 0)
    );
  } else {
    return matrix.color565(0,255,0);
  }
}

// drawWaveform
void drawWaveform() {
  const int rawW = WIDTH, rawH = HEIGHT;
  const int midY = rawH/2 - 4, scaleY = rawH/2;
  int dispW = matrix.width(), dispH = matrix.height();

  for (int rx = 0; rx < rawW; rx++) {
    double raw = analogRead(MIC_PIN);
    raw = min(raw, 1023.0);
    int16_t v = int(raw) - 512;
    int16_t dy = (v * scaleY) / 512;
    int16_t ry = constrain(midY - dy, 0, rawH - 1);
    int dx = (dispW - 1) - ry + WAVE_X_SHIFT;
    int dy2 = rx;
    if (dx >= 0 && dx < dispW && dy2 >= 0 && dy2 < dispH) {
      matrix.drawPixel(dx, dy2, matrix.color565(0,255,255));
    }
  }
}

// drawBars
void drawBars() {
  const int rawW = WIDTH, rawH = HEIGHT;
  const int barW = 2, barGap = 1;
  int numBars = WIDTH/3;
  int blockW = numBars*(barW+barGap) - barGap;
  int offsetX = (rawW - blockW)/2;
  int dispW = matrix.width();

  for (int i = 0; i < numBars; i++) {
    int rx = offsetX + i*(barW+barGap);

    // fill bars
    for (int ryOff = 0; ryOff < barHeights[i]; ryOff++) {
      int rawYb = rawH - 1 - ryOff;
      int rawYt = ryOff;
      uint16_t c = redlined[i]
        ? matrix.color565(255,0,128)
        : getBarColor(ryOff, barHeights[i]);

      int dx1 = dispW - 1 - rawYb;
      matrix.drawFastVLine(dx1, rx, barW, c);

      int dx2 = dispW - 1 - rawYt;
      matrix.drawFastVLine(dx2, rx, barW, c);
    }

    // peaks
    int rawPb = rawH - 1 - peakLevels[i];
    int dxp1 = dispW - 1 - rawPb;
    uint16_t pk = redlined[i]
      ? matrix.color565(255,0,128)
      : matrix.color565(0,255,0);
    matrix.drawFastVLine(dxp1, rx, barW, pk);

    int rawPt = peakLevels[i];
    int dxp2 = dispW - 1 - rawPt;
    matrix.drawFastVLine(dxp2, rx, barW, pk);
  }
}

// initAudioVis
void initAudioVis() {
  pinMode(GAIN_PIN, OUTPUT);
  digitalWrite(GAIN_PIN, LOW);
  matrix.setTextWrap(false);

  for (int i = 0; i < WIDTH/3; i++) {
    barHeights[i] = 1;
    redlined[i]   = false;
    peakLevels[i] = 0;
    peakTimes[i]  = 0;
  }
  for (int i = 0; i < samples; i++) {
    smoothedInput[i] = 0.0;
  }

  audioActive    = true;
  audioStartTime = millis();
  matrix.fillScreen(0);
}

// runAudioVisFrame
void runAudioVisFrame() {
  if (!gReadyState) {
    audioActive     = false;
    sponsorLaunched = perryActive = false;
    readyTimestamp  = lastSponsor = 0;
    matrix.fillScreen(0);
    drawChecklistStatic();
    return;
  }
  if (millis() - audioStartTime >= audioDuration) {
    audioActive   = false;
    readyTimestamp = 0;
    return;
  }
  readFFT();
  updatePeaks();
  matrix.fillScreen(0);
  drawWaveform();
  drawBars();
  matrix.show();
  delay(1);
}
