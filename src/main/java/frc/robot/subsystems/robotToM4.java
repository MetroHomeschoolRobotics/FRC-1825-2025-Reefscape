package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SerialPort;
import java.nio.charset.StandardCharsets;

/**
 * Minimal, steady-cadence RS-232 transmitter for MatrixPortal.
 * - kOnboard, 9600 8-N-1
 * - Sends every ~100ms (5 loops @ ~20ms loop time)
 * - Debounce = 5 loops (~100ms)
 * - CRLF line termination to please Arduino line readers
 * Message format: "<case> <piece>,<elev>,<angle>,<fms>\r\n"
 */
public class robotToM4 extends SubsystemBase {
  public static robotToM4 INSTANCE;

  private final SerialPort rs232Port;

  // Inputs from subsystems (updated via setters)
  private double  elevatorAngle;
  private boolean elevatorBeamBreak;
  private boolean intakeBeamBreak;
  private boolean visionReceive; // reserved

  // Dynamic mode flags
  // reqPiece: set to true when the robot is driving to the source or otherwise
  // requesting a game piece.  Set to false otherwise.  Commands such as
  // DriveToSource or other intake preparation routines should call
  // setRequestPiece(true) when starting and setRequestPiece(false) when the
  // operation completes.
  private volatile boolean reqPiece = false;
  // scoreLevel: non‑zero when the robot is actively attempting to score at a
  // given level.  Valid values are 1–4.  Set back to 0 when no scoring
  // attempt is underway.  Commands that perform scoring should set this
  // appropriately via setScoreLevel(int).
  private volatile int scoreLevel = 0;
  private volatile int latchScoreLevel = 0;
  private int latchedScoreTTL = 0;
  private static final int SCORE_TTL_CYCLES = 20;
  private String lastLineSent=null;

  // climbState: the climb overlay state.  0 = none, 1 = ready, 2 = attempt,
  // 3 = success.  Use setClimbState(int) to update.
  private volatile int climbState = 0;

  // Startup/shutdown flags.  These are used to send one‑time mode changes
  // requested by the MatrixPortal firmware.  On the very first periodic call
  // after robot startup we send a mode 1 message with the current
  // checklist values to instruct the display to play the boot sequence.
  // On the first disable after having been enabled we send mode 3 (shutdown)
  // once.  Subsequent disables fall back to mode 0 (checklist) to keep the
  // LED checklist updated.
  private boolean startupSent = false;
  private boolean shutdownSent = false;

  // FMS override flags (static so they can be set via static setter)
  private static boolean fmsOverridePresent = false;
  private static boolean fmsOverrideValue   = false;

  // Legacy cycle/flag variables from earlier merges.  These are static to
  // satisfy references in generated code but are otherwise unused here.
  private static int cycleCounter = 0;
  private static int savedCycleCounter = 0;
  private static boolean completeFlag = false;

  // Golden angle for ANGLE box
  private static final double ANGLE_TARGET_DEGREES = -27.0;

  // Debounce (loops of ~20ms)
  private static final int SENSOR_DEBOUNCE_CYCLES = 5;

  private int pieceHigh = 0, pieceLow = 0, pieceState = 0;
  private int elevHigh  = 0, elevLow  = 0, elevState  = 0;
  private int angHigh   = 0, angLow   = 0, angState   = 0;

  // Steady transmission rate: every 5 loops ≈ 100ms
  private static final int MESSAGE_SEND_INTERVAL_CYCLES = 5;
  private int sendCounter = 0;

  public robotToM4() {
    INSTANCE = this;
    rs232Port = new SerialPort(
      9600,
      SerialPort.Port.kOnboard,
      8,
      SerialPort.Parity.kNone,
      SerialPort.StopBits.kOne
    );
  }

  /**
   * Override the FMS/Driver Station connectivity flag used in checklist
   * messages.  This static method allows callers that may not have an
   * instance of robotToM4 to force the MatrixPortal's FMS indicator on or
   * off.  Once set, the override remains in effect until changed again.
   *
   * @param connected true to force the FMS flag to 1, false to force it to 0
   */
  public static void setFMSConnected(boolean connected) {
    // Always take note of the override.  If a Driver Station is attached,
    // force the flag to true even if the caller passes false.  This helps
    // indicate FMS/DS connectivity when running tethered but not on the field.
    fmsOverridePresent = true;
    fmsOverrideValue   = connected || DriverStation.isDSAttached();
  }

  /**
   * Handle high‑level mode changes requested by external commands.  This
   * method interprets string mode names used throughout the robot code and
   * updates internal flags accordingly.  Mode names correspond to the
   * animations defined in the MatrixPortal firmware (e.g. STARTUP,
   * DISABLED, AUTO, DRIVING, INTAKEPRE, INTAKEACTIVE, INTAKECOMPLETE,
   * SCOREPRE, SCOREACTIVE, SCORECOMPLETE, CLIMBPRE, CLIMBACTIVE,
   * CLIMBCOMPLETE).  Unrecognised modes have no effect.
   *
   * @param mode human‑readable mode identifier
   */
  public static void changeMode(String mode) {
    if (mode == null) return;
    switch (mode) {
      case "STARTUP":
        // Reset startup flag so that periodic sends a mode‑1 message on the
        // next disabled cycle.  Also clear other flags to a neutral state.
        if (INSTANCE != null) {
          INSTANCE.startupSent = false;
          INSTANCE.shutdownSent = false;
          INSTANCE.reqPiece = false;
          INSTANCE.scoreLevel = 0;
          INSTANCE.climbState = 0;
          INSTANCE.latchedScoreTTL=0;
          INSTANCE.latchScoreLevel=0;
        }
        break;
      case "DISABLED":
        // No special action; periodic will handle disabled logic.  Reset
        // request and scoring flags for safety.
        if (INSTANCE != null) {
          // Reset request and score flags
          INSTANCE.reqPiece = false;
          INSTANCE.scoreLevel = 0;
          // Mark that we have already sent the startup message and that
          // shutdown has not yet been sent.  This causes periodic() to
          // transmit the shutdown code (mode 3) on the next disabled loop.
          INSTANCE.startupSent = true;
          INSTANCE.shutdownSent = false;
        }
        break;
      case "AUTO":
        // No dynamic flags during autonomous
        if (INSTANCE != null) {
          INSTANCE.reqPiece = false;
          INSTANCE.scoreLevel = 0;
          // leave climb state untouched
        }
        break;
      case "DRIVING":
        // Idle teleop: clear request/score/climb flags
        if (INSTANCE != null) {
          INSTANCE.reqPiece = false;
          INSTANCE.scoreLevel = 0;
          INSTANCE.climbState = 0;
        }
        break;
      case "INTAKEPRE":
      case "INTAKEACTIVE":
        // Requesting a game piece.  Do not change score or climb.
        if (INSTANCE != null) {
          INSTANCE.reqPiece = true;
        }
        break;
      case "INTAKECOMPLETE":
        // Finished intake; clear request flag.
        if (INSTANCE != null) {
          INSTANCE.reqPiece = false;
        }
        break;
      case "SCOREPRE":
        // Preparing to score; no change here.  Actual score level should be
        // set by calling setScoreLevel(level) when known.
        break;
      case "SCOREACTIVE":
        // Actively scoring; nothing to update here.  scoreLevel should
        // already be set via setScoreLevel().
        break;
      case "SCORECOMPLETE":
        // Scoring complete; reset score level
        if (INSTANCE != null) {
          INSTANCE.scoreLevel = 0;
        }
        break;
      case "CLIMBPRE":
        if (INSTANCE != null) {
          INSTANCE.climbState = 1;
        }
        break;
      case "CLIMBACTIVE":
        if (INSTANCE != null) {
          INSTANCE.climbState = 2;
        }
        break;
      case "CLIMBCOMPLETE":
        if (INSTANCE != null) {
          INSTANCE.climbState = 3;
        }
        break;
      default:
        // Unknown mode: no action
        break;
    }
  }

  // ---- Setters (call from subsystems/RobotContainer) ----
  public void setElevatorAngle(double angle)          { this.elevatorAngle = angle; }
  public void setElevatorBeamBreak(boolean triggered) { this.elevatorBeamBreak = triggered; }
  public void setIntakeBeamBreak(boolean triggered)   { this.intakeBeamBreak = triggered; }
  public void setVisionReceive(boolean detected)      { this.visionReceive   = detected; }

  /**
   * In dynamic (teleop) mode the first field in the payload indicates a
   * "request" for a game piece.  Commands that direct the robot to the
   * human player station or otherwise prepare to intake should set this
   * flag to true when the request begins and set it back to false when
   * complete.  When set the MatrixPortal will display the FETCH/PIECE
   * animation in the top bar.
   */
  public void setRequestPiece(boolean request) {
    this.reqPiece = request;
  }

  /**
   * Set the current scoring level.  A non‑zero level triggers the scoring
   * overlay on the MatrixPortal; valid values are 1–4 corresponding to
   * different heights.  Set to 0 when the robot is not actively scoring.
   */
  public void setScoreLevel(int level) {
    // clamp to [0,4] for safety
    if (level < 0) level = 0;
    if (level > 4) level = 4;
    this.scoreLevel = level;
    if(level>0){
      this.latchScoreLevel = level;
      this.latchedScoreTTL=SCORE_TTL_CYCLES;
  
    }else{
      this.latchScoreLevel=0;
      this.latchedScoreTTL=0;
    }
  }

  /**
   * Set the climb state for the dynamic overlay.  0 = no climb, 1 = ready,
   * 2 = attempt, 3 = success.  This influences the bottom bar in dynamic
   * mode.  For teams not using the climb overlay this may remain zero.
   */
  public void setClimbState(int state) {
    if (state < 0) state = 0;
    if (state > 3) state = 3;
    this.climbState = state;
  }

  private void sendLine(String line) {
    if (rs232Port != null) {
      // Use CRLF to make Arduino "read line" code happier
      byte[] bytes = (line + "\r\n").getBytes(StandardCharsets.US_ASCII);
      rs232Port.write(bytes, bytes.length);
    }
  }
  @Override
  public void periodic() {
    // ---- Debounce PIECE (intake) ----
    if (intakeBeamBreak) {
      if (++pieceHigh >= SENSOR_DEBOUNCE_CYCLES) {
        pieceState = 1;
        pieceHigh = SENSOR_DEBOUNCE_CYCLES;
      }
      pieceLow = 0;
    } else {
      if (++pieceLow >= SENSOR_DEBOUNCE_CYCLES) {
        pieceState = 0;
        pieceLow = SENSOR_DEBOUNCE_CYCLES;
      }
      pieceHigh = 0;
    }

    // ---- Debounce ELEV (elevator beam break) ----
    if (elevatorBeamBreak) {
      if (++elevHigh >= SENSOR_DEBOUNCE_CYCLES) {
        elevState = 1;
        elevHigh = SENSOR_DEBOUNCE_CYCLES;
      }
      elevLow = 0;
    } else {
      if (++elevLow >= SENSOR_DEBOUNCE_CYCLES) {
        elevState = 0;
        elevLow = SENSOR_DEBOUNCE_CYCLES;
      }
      elevHigh = 0;
    }

    // ---- Debounce ANGLE ----
    boolean angleInRange = Math.abs(elevatorAngle - ANGLE_TARGET_DEGREES) <= 1.0;
    if (angleInRange) {
      if (++angHigh >= SENSOR_DEBOUNCE_CYCLES) {
        angState = 1;
        angHigh = SENSOR_DEBOUNCE_CYCLES;
      }
      angLow = 0;
    } else {
      if (++angLow >= SENSOR_DEBOUNCE_CYCLES) {
        angState = 0;
        angLow = SENSOR_DEBOUNCE_CYCLES;
      }
      angHigh = 0;
    }

    // ---- Determine which mode and payload to send ----
    int mode;        // 0 = checklist, 1 = autonomous/startup, 2 = dynamic, 3 = shutdown
    String payload;  // comma separated values or empty for modes without payload

    boolean enabled = DriverStation.isEnabled();
    boolean autoMode = DriverStation.isAutonomousEnabled();

    // Compute FMS flag for checklist/startup messages
    int fms;
    if (fmsOverridePresent) {
      fms = fmsOverrideValue ? 1 : 0;
    } else {
      fms = (DriverStation.isFMSAttached() || DriverStation.isDSAttached()) ? 1 : 0;
    }

    if (!enabled) {
      // robot disabled
      if (!startupSent) {
        // On the very first disable after startup send mode 1 with checklist values
        mode = 1;
        payload = pieceState + "," + elevState + "," + angState + "," + fms;
        startupSent = true;
      } else if (!shutdownSent) {
        // First time we disable after being enabled: send shutdown
        mode = 3;
        payload = ""; // no payload for shutdown
        shutdownSent = true;
      } else {
        // Subsequent disables: send checklist to keep UI updated
        mode = 0;
        payload = pieceState + "," + elevState + "," + angState + "," + fms;
      }
    } else if (autoMode) {
      // Autonomous mode: send mode 1 (auto animation); no payload
      mode = 1;
      payload = "";
      // Reset the shutdown flag so that the next disable triggers shutdown again
      shutdownSent = false;
    } else {
      // Teleop (dynamic) mode
      mode = 2;
      // Build dynamic payload: request flag, has piece (intake), score level, climb state
      int reqFlag = reqPiece ? 1 : 0;
      int intakeFlag = pieceState;
      int score = scoreLevel;
      int climb = climbState;
      payload = reqFlag + "," + intakeFlag + "," + score + "," + climb;
      // Reset the shutdown flag so that the next disable triggers shutdown again
      shutdownSent = false;
    }

    // Build final line.  Modes without payload are just "<mode>".  Others have
    // the mode, a space, and the payload.
    String line;
    if (payload.isEmpty()) {
      line = Integer.toString(mode);
    } else {
      line = mode + " " + payload;
    }

    // ---- Send at steady cadence ----
    if (++sendCounter >= MESSAGE_SEND_INTERVAL_CYCLES) {
      sendCounter = 0;
      sendLine(line);
    }

    // Leave cycleCounter/completeFlag logic intact from previous merge.  If in use
    // this will call changeMode when appropriate.  changeMode is a no‑op by
    // default.
    if (cycleCounter == savedCycleCounter + 50 && completeFlag == true) {
      changeMode("DRIVING");
      completeFlag = false;
    }
  }
}
