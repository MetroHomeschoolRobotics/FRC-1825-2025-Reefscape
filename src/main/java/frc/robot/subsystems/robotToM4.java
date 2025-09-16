package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SerialPort;

/**
 * Centralized serial-to-MatrixPortal subsystem with automatic mode cycling.
 */
public class robotToM4 extends SubsystemBase {
  // Singleton instance
  public static robotToM4 INSTANCE;

  private final  SerialPort rs232Port;
  
    // Stored variables
    private static double elevatorAngle;
      private static boolean elevatorBeamBreak;
      private static boolean intakeBeamBreak;
      private static boolean visionReceive;
      private static boolean fmsConnected;
      //private double fieldPosition;
    private static final int AUTOCASE = 1;
    private static final int TELEOPCASE = 2;
    
      // Modes for what to send
      public enum Mode { STARTUP, SENSOR_DEBUG, ROBOT_CLIMB, WAIT, AUTO, DRIVING,
     INTAKEPRE,INTAKEACTIVE,INTAKECOMPLETE,SCOREPRE,SCOREACTIVE,SCORECOMPLETE,CLIMBPRE,CLIMBACTIVE,CLIMBCOMPLETE, DISABLED }
      private static Mode currentMode = Mode.STARTUP;
          
            // Cycle counter
            private static  int cycleCounter = 0;
          
            // Timers for AUTO and TELEOP (unused in cycling but kept)
            private static int autoCountdown = 15;
            private static int teleopCountdown = 2 * 60 + 15;
          
            private static boolean completeFlag = false;
            private static int savedCycleCounter;
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
            public static void setElevatorAngle(double angle) { elevatorAngle = angle; }
          public static void setElevatorBeamBreak(boolean triggered) { elevatorBeamBreak = triggered; }
          public static void setIntakeBeamBreak(boolean triggered) { intakeBeamBreak = triggered; }
          public static void setVisionReceive(boolean detected) { visionReceive = detected; }
          public static void setFMSConnected(boolean connected){ fmsConnected = connected;}
          //public void setFieldPosition(double position) { this.fieldPosition = position; }
        
          /**
           * Send a string of values to the MatrixPortal.
           */
          public void sendData(String data) {
            rs232Port.writeString(data + "\n");
        }
        
        public static void changeMode(String mode){
          currentMode = Mode.valueOf(mode);
          if(completeFlag==false &&(Mode.valueOf(mode)==Mode.INTAKECOMPLETE || Mode.valueOf(mode) ==Mode.SCORECOMPLETE)){
            completeFlag = true;
        savedCycleCounter = cycleCounter;
          }
  }
  private String sensorValues(){
    return (elevatorBeamBreak ? "1" : "0") + "," +
    (intakeBeamBreak ? "1" : "0")+","+(fmsConnected ? "1" : "0");
  }
  public static int m4CycleCounter(){
    return cycleCounter;
  }
  @Override
  public void periodic() {
    // Advance cycle counter and determine mode based on cycles
    cycleCounter++;
    // Mode[] modes = Mode.values();
    // int index = (cycleCounter / 200) % modes.length;
    // currentMode = modes[index];

    switch (currentMode) {
      case STARTUP:
        sendData("0 1,1,1,1");
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
        sendData(Integer.toString(AUTOCASE));
        break;

      case DRIVING:
        sendData(Integer.toString(TELEOPCASE)+" 0");
        break;
      case INTAKEPRE:
        sendData(Integer.toString(TELEOPCASE)+" 1,0");
      case INTAKEACTIVE:
        sendData(Integer.toString(TELEOPCASE)+" 1,1");
        break;
      case INTAKECOMPLETE:
        sendData(Integer.toString(TELEOPCASE)+" 1,2");
        break;
      case SCOREPRE:
        sendData(Integer.toString(TELEOPCASE)+" 2,0");
        break;
      case SCOREACTIVE:
        sendData(Integer.toString(TELEOPCASE)+" 2,1");
        break;
      case SCORECOMPLETE:
        sendData(Integer.toString(TELEOPCASE)+" 2,2");
        break;
      case CLIMBPRE:
        sendData(Integer.toString(TELEOPCASE)+ "3,0");
        break;
      case CLIMBACTIVE:
        sendData(Integer.toString(TELEOPCASE)+ "3,1");
        break;
      case CLIMBCOMPLETE:
        sendData(Integer.toString(TELEOPCASE)+ "3,2");
        break;
      case DISABLED:
        sendData("3");
        break;
    }

    
    if(cycleCounter == savedCycleCounter+50&& completeFlag==true){
      changeMode("DRIVING");
      completeFlag = false;
    }    
  }
}
