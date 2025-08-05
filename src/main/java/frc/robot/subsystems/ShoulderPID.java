package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.CANcoder;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import dev.doglog.DogLog;

import com.revrobotics.spark.SparkMax;
import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class ShoulderPID extends SubsystemBase {

    private SparkMax wristMotor1 = new SparkMax(Constants.MotorIDs.wristMotorID1, MotorType.kBrushless);
    private boolean isClimbing = false;

 // private SparkMax wristMotor2 = new SparkMax(Constants.wristMotorID2, MotorType.kBrushless);
  private static CANcoder rotationCANcoder = new CANcoder(Constants.MotorIDs.cancoderID);
  
  private PIDController pid = new PIDController(0.035, 0, 0.00);
  
  private ElevatorFeedforward feedforward = new ElevatorFeedforward(0, 0.05, 0);
  private double desiredposition = 0;
  

  /** Creates a new Shoulder. */
  public ShoulderPID() {
    pid.setTolerance(0.75);
    setClimb(false);
  }
  
  public void incrementPID(double speed) {
     if ((desiredposition<=8 && speed >0) || (desiredposition >= -60 && speed<0)) {
      desiredposition+=speed;
      pid.setSetpoint(desiredposition);
    }

  }
  public boolean climbingCloseEnough(){
    return getAbsoluteAngle()<-50;
  }

  public void runDirectly(double speed){
    if ((getAbsoluteAngle()<=8 && speed <0) || (getAbsoluteAngle() >= -58 && speed>0)) {
      wristMotor1.set(speed);
      //wristMotor2.set(-speed);
    }else{
      wristMotor1.set(0);
      //wristMotor2.set(0);
    }
  }
//PID
  public void setPID(double value){
  
        desiredposition = value;
        pid.setSetpoint(desiredposition);
      
  }
  public double getSetpoint(){
    return pid.getSetpoint();
  }
  public static double getAbsoluteAngle() {
    //-141.4 straight up
    //-130 forward
    //-177 back
    double output =rotationCANcoder.getAbsolutePosition().getValueAsDouble()*360+142.9;
    if(output>180){
      output-=360;
    }
    return output;
    
  }
  public boolean atSetpoint(){
    return pid.atSetpoint();
  }
  public boolean getClimb(){
    return isClimbing;
  }
  public void setClimb(Boolean _isClimb){
    isClimbing = _isClimb;
    // if(isClimbing ==true){
    //   pid.setP(0.04);
    //   pid.setI(0.001);
    // }else if(isClimbing == false){
    //   pid.setP(0.04);
    // }
  }
  private void log(double output){
    DogLog.log("Shoulder/Angle", getAbsoluteAngle());
    DogLog.log("Shoulder/Setpoint", pid.getSetpoint());
    DogLog.log("Shoulder/PIDOutput",output);
  }
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    SmartDashboard.putNumber("Shoulder Absolute Angle", getAbsoluteAngle());
    // SmartDashboard.putNumber("ShoulderPid DesiredPos", desiredposition);
    SmartDashboard.putNumber("shoulderPID actualSetpoint",pid.getSetpoint());
    double output;
    if(getClimb()==false){
      
        output = pid.calculate(getAbsoluteAngle());
    
      
        
    
    //SmartDashboard.putNumber("shoulder output ", -output);
    wristMotor1.set(-output);
    log(-output);
    }
    
    //wristMotor2.setVoltage(output*12)
  }
}
