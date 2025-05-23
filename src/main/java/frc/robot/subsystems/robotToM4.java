package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.SerialPort;

/**
 * Centralized serial-to-MatrixPortal subsystem with automatic mode cycling.
 */
public class robotToM4 extends SubsystemBase {
  // Singleton instance
  public static robotToM4 INSTANCE;

  private final SerialPort rs232Port;

  // Stored variables
  private double elevatorAngle;
  private boolean elevatorBeamBreak;
  private boolean intakeBeamBreak;
  private boolean visionReceive;
  //private double fieldPosition;

  // Modes for what to send
  public enum Mode { STARTUP, SENSOR_DEBUG, ROBOT_CLIMB, WAIT, AUTO, TELEOP, DISABLED }
  private Mode currentMode = Mode.STARTUP;

  // Cycle counter
  private int cycleCounter = 0;

  // Timers for AUTO and TELEOP (unused in cycling but kept)
  private int autoCountdown = 15;
  private int teleopCountdown = 2 * 60 + 15;

  public robotToM4() {
    INSTANCE = this;
    rs232Port = new SerialPort(
      9600,
      SerialPort.Port.kOnboard,
      8,
      SerialPort.Parity.kOdd,
      SerialPort.StopBits.kOne
    );
  }

  /**
   * Setters for incoming data
   */
  public void setElevatorAngle(double angle) { this.elevatorAngle = angle; }
  public void setElevatorBeamBreak(boolean triggered) { this.elevatorBeamBreak = triggered; }
  public void setIntakeBeamBreak(boolean triggered) { this.intakeBeamBreak = triggered; }
  public void setVisionReceive(boolean detected) { this.visionReceive = detected; }
  //public void setFieldPosition(double position) { this.fieldPosition = position; }

  /**
   * Send a string of values to the MatrixPortal.
   */
  public void sendData(String data) {
    rs232Port.writeString(data + "\n");
  }

  @Override
  public void periodic() {
    // Advance cycle counter and determine mode based on cycles
    cycleCounter++;
    Mode[] modes = Mode.values();
    int index = (cycleCounter / 200) % modes.length;
    currentMode = modes[index];

    switch (currentMode) {
      case STARTUP:
        sendData("hello world");
        break;

      case SENSOR_DEBUG:
        sendData(
          (elevatorBeamBreak ? "1" : "0") + "," +
          (intakeBeamBreak ? "1" : "0") + "," +
          (visionReceive ? "1" : "0")
        );
        break;

      case ROBOT_CLIMB:
        sendData(Integer.toString((int)Math.round(elevatorAngle)));
        break;

      case WAIT:
        sendData("wait");
        break;

      case AUTO:
        sendData(Integer.toString(autoCountdown));
        break;

      case TELEOP:
        sendData(Integer.toString(teleopCountdown));
        break;

      case DISABLED:
        sendData("END");
        break;
    }
  }
}
