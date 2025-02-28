package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.CANcoder;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class ShoulderPID extends SubsystemBase {
    private SparkMax wristMotor1 = new SparkMax(Constants.wristMotorID1, MotorType.kBrushless);
  
 // private SparkMax wristMotor2 = new SparkMax(Constants.wristMotorID2, MotorType.kBrushless);
  private CANcoder rotationCANcoder = new CANcoder(Constants.cancoderID);
  
  private PIDController pid = new PIDController(0.001, 0, 0);
  private ElevatorFeedforward feedforward = new ElevatorFeedforward(0, 0.1, 0);
  private double desiredposition = 0;
  // private DutyCycleEncoder rotationEncoder = new DutyCycleEncoder(3);

  /** Creates a new Shoulder. */
  public ShoulderPID() {}
  
  public void incrementPID(double speed) {
     if ((getAbsoluteAngle()<=8 && speed <0) || (getAbsoluteAngle() >= -35 && speed>0)) {
      desiredposition+=speed;
      
    }

  }
  public void setPID(double value){
    if(value>-45 && value<8){
        desiredposition = value;
        pid.setSetpoint(desiredposition);
    }else{
        System.out.println("PID Setpoint out of range");
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
    double output;
    if(getAbsoluteAngle()>-3 || getAbsoluteAngle()<3){
        output = pid.calculate(getAbsoluteAngle());
    }else if(getAbsoluteAngle()>3){
        output = pid.calculate(getAbsoluteAngle())-feedforward.calculate(0);
    }else{
        output = pid.calculate(getAbsoluteAngle())+feedforward.calculate(desiredposition);
    }
    wristMotor1.setVoltage(output*12);
    //wristMotor2.setVoltage(output*12)
  }
}
