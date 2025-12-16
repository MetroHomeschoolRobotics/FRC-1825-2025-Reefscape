package frc.robot.subsystems;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Constants;
import com.revrobotics.spark.SparkMax;

import com.revrobotics.spark.SparkLowLevel;





public class Intake extends SubsystemBase {

    //Define the motors and sensors that will be used in this subsystem
    private SparkMax intakeMotor1 = new SparkMax(Constants.MotorIDs.intakeDeviceID1, SparkLowLevel.MotorType.kBrushless);
    private SparkMax intakeMotor2 = new SparkMax(Constants.MotorIDs.intakeDeviceID2, SparkLowLevel.MotorType.kBrushless);
    private DigitalInput beambreak = new DigitalInput(0);
   
    //private SparkBaseConfig config = new SparkMaxConfig().inverted(true);
  
    /** Creates a new Intake.
     * <p>
     * Controls the intake motors and reads intake sensors
     */
    public Intake() {
     // intakeMotor2.configure(
     //     config, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);
    }
    
    @Override
    public void periodic() {
      // This method will be called once per scheduler run
      SmartDashboard.putBoolean("coral in Intake", coralInIntake() );
      if(frc.robot.subsystems.robotToM4.INSTANCE!=null){
        frc.robot.subsystems.robotToM4.INSTANCE.setIntakeBeamBreak(coralInIntake());
      }
    }
  
    public Boolean coralInIntake() {
      return !beambreak.get();

      
    }  
    public void scoreBackwards(){
      intakeMotor1.set(0.225);
      intakeMotor2.set(0.2);
    }
    public void setSpeed(double speed) {
      //check inverts
      intakeMotor1.set(speed);
      intakeMotor2.set(speed);
      
    }
    public void setSpeedSideways(double speed){
      intakeMotor1.set(speed);
      intakeMotor2.set(0);
    }
    public void setSpeedSideways2(double speed){
      intakeMotor1.set(0);
      intakeMotor2.set(speed);
    }

    public double getDistance() {
      return intakeMotor1.getEncoder().getPosition();
      // I have no idea if we are using this
    }

    public double getSpeed() {
      return intakeMotor1.getEncoder().getVelocity();
        
    }
  }
