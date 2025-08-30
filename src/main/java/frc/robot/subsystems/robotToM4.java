package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.DriverStation;
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

  // ---- Setters (call from subsystems/RobotContainer) ----
  public void setElevatorAngle(double angle)          { this.elevatorAngle = angle; }
  public void setElevatorBeamBreak(boolean triggered) { this.elevatorBeamBreak = triggered; }
  public void setIntakeBeamBreak(boolean triggered)   { this.intakeBeamBreak = triggered; }
  public void setVisionReceive(boolean detected)      { this.visionReceive   = detected; }

  private void sendLine(String line) {
    if (rs232Port != null) {
      // Use CRLF to make Arduino "read line" code happier
      byte[] bytes = (line + "\r\n").getBytes(StandardCharsets.US_ASCII);
      rs232Port.write(bytes, bytes.length);
    }
  }

  @Override
  public void periodic() {
    // ---- Debounce PIECE ----
    if (intakeBeamBreak) { if (++pieceHigh >= SENSOR_DEBOUNCE_CYCLES) { pieceState = 1; pieceHigh = SENSOR_DEBOUNCE_CYCLES; } pieceLow = 0; }
    else { if (++pieceLow >= SENSOR_DEBOUNCE_CYCLES) { pieceState = 0; pieceLow = SENSOR_DEBOUNCE_CYCLES; } pieceHigh = 0; }

    // ---- Debounce ELEV ----
    if (elevatorBeamBreak) { if (++elevHigh >= SENSOR_DEBOUNCE_CYCLES) { elevState = 1; elevHigh = SENSOR_DEBOUNCE_CYCLES; } elevLow = 0; }
    else { if (++elevLow >= SENSOR_DEBOUNCE_CYCLES) { elevState = 0; elevLow = SENSOR_DEBOUNCE_CYCLES; } elevHigh = 0; }

    // ---- Debounce ANGLE ----
    boolean angleInRange = Math.abs(elevatorAngle - ANGLE_TARGET_DEGREES) <= 1.0;
    if (angleInRange) { if (++angHigh >= SENSOR_DEBOUNCE_CYCLES) { angState = 1; angHigh = SENSOR_DEBOUNCE_CYCLES; } angLow = 0; }
    else { if (++angLow >= SENSOR_DEBOUNCE_CYCLES) { angState = 0; angLow = SENSOR_DEBOUNCE_CYCLES; } angHigh = 0; }

    // ---- Decide case ----
    final int matrixCase =
        !DriverStation.isEnabled()          ? 0 :
        DriverStation.isAutonomousEnabled() ? 1 : 2;

    // ---- FMS/DS presence ----
    final int fms = (DriverStation.isFMSAttached() || DriverStation.isDSAttached()) ? 1 : 0;

    // ---- Build one compact line ----
    final String line = matrixCase + " " + pieceState + "," + elevState + "," + angState + "," + fms;

    // ---- Send at steady cadence ----
    if (++sendCounter >= MESSAGE_SEND_INTERVAL_CYCLES) {
      sendCounter = 0;
      sendLine(line);
    }
  }
}
