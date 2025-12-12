package frc.robot.subsystems;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.ctre.phoenix6.hardware.CANcoder;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Shoulder extends SubsystemBase {

  private SparkMax wristMotor1 = new SparkMax(Constants.MotorIDs.wristMotorID1, MotorType.kBrushless);
  
 // private SparkMax wristMotor2 = new SparkMax(Constants.wristMotorID2, MotorType.kBrushless);
  private CANcoder rotationCANcoder = new CANcoder(Constants.MotorIDs.cancoderID);
  // private DutyCycleEncoder rotationEncoder = new DutyCycleEncoder(3);

  /** Creates a new Shoulder. */
  public Shoulder() {}
  
  public void setSpeed(double speed) {
    SmartDashboard.putNumber("pivot speed", speed);
    SmartDashboard.putNumber("Shoulder Current", wristMotor1.getOutputCurrent());
    // if(getAbsoluteAngle()<=8 && speed <0){
    //   wristMotor1.set(speed/4);
    // }else if (getAbsoluteAngle() >= -35 && speed>0) {
    //   wristMotor1.set(speed/4);

     if ((getAbsoluteAngle()<=8 && speed <0) || (getAbsoluteAngle() >= -50 && speed>0)) {
      wristMotor1.set(speed);
      //wristMotor2.set(-speed);
    }else{
      wristMotor1.set(0);
      //wristMotor2.set(0);
    }

  }

  public double getAbsoluteAngle() {
    //-141.4 straight up
    //-130 forward
    //-177 back
    double output =rotationCANcoder.getAbsolutePosition().getValueAsDouble()*360+142.9;
    if(output>180){
      output-=360;
    }
    return output;
    
  }
  
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    SmartDashboard.putNumber("Shoulder Absolute Angle", getAbsoluteAngle());
  }
}