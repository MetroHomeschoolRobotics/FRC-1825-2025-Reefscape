// © 2025 SC5K Systems

#include <Arduino.h>
#include "src/matrix_config.h"
#include "src/globals.h"
#include "src/checklist.h"
#include "src/boot_sequence.h"
#include "src/autonomous.h"
#include "src/dynamic.h"
#include "src/shutdown.h"

void setup() {
  // init usb and RoboRIO serial
  Serial.begin(115200);
  Serial1.begin(9600);
  delay(500);
  // set short serial timeouts (~50ms) to avoid partial lines from RoboRIO
  Serial.setTimeout(50);
  Serial1.setTimeout(50);
  if (Serial) {
    Serial.println("USB serial active");
    Serial.println("RoboRIO serial active");
  }

  // Matrix init
  if (matrix.begin() != PROTOMATTER_OK) while (1) delay(10);
  matrix.setRotation(1);
  matrix.setTextWrap(false);
  matrix.setTextSize(1);

  // allocate the shared buffer used by readAndProcess()
  buf = new char[32];

  // one‐time boot sequence (splash, color test, write-on checklist)
  initBootSequence();
}

void loop() {
  // 1) read/dispatch incoming messages from RoboRIO or USB
  handleRobotMessage();

  // 2) run per‐mode logic
  switch (currentMode) {
    case MODE_CHECKLIST:
    // checklist mode: update checklist and run scroller/perry/audio
      runBootSequence();
      break;

    case MODE_AUTONOMOUS:
    // autonomous mode: run auto animation while active
      if (autoActive) runAutonomousFrame();
      break;

    case MODE_DYNAMIC:
    // dynamic mode: draw network and optional accel/AI/cube
      runDynamicFrame();
      break;

    case MODE_SHUTDOWN:
    // shutdown mode: draw match over header and scroll fake code
      runShutdownFrame();
      break;

    default:
    // MODE_NULL or undefined: do nothing
      break;
  }
}
