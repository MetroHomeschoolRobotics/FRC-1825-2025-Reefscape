package frc.robot.subsystems;

import frc.robot.subsystems.robotToM4;

import org.ejml.simple.SimpleMatrix;

import com.ctre.phoenix6.hardware.CANcoder;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.math.MatBuilder;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.Nat;
import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import edu.wpi.first.math.numbers.*;

public class ShoulderPID extends SubsystemBase {

  private SparkMax wristMotor1 = new SparkMax(Constants.MotorIDs.wristMotorID1, MotorType.kBrushless);
  private boolean isClimbing = false;

 // private SparkMax wristMotor2 = new SparkMax(Constants.wristMotorID2, MotorType.kBrushless);
  private static CANcoder rotationCANcoder = new CANcoder(Constants.MotorIDs.cancoderID);
    
    //private PIDController pid = new PIDController(0.09, 0, 0.00);
    // private PIDController pid = new PIDController(0.0324, 0, 0.00066);
    private double a = 1;
    //private Matrix<N5, N1> PIDMatrix = MatBuilder.fill(Nat.N5(), Nat.N1(),0.0324+0.00033*50,-0.00033*50,0,0,0);
    private Matrix<N5, N1> PIDMatrix = MatBuilder.fill(Nat.N5(), Nat.N1(),(0.0324+0.00033*50)/4,0.0324/4,0.0324/4,0.0324/4,-0.00033*50/4);
    private Matrix<N1,N5> errorHistory = MatBuilder.fill(Nat.N1(), Nat.N5(), 0, 0, 0, 0, 0);
    
    private ElevatorFeedforward feedforward = new ElevatorFeedforward(0, 0.05, 0);
    private double desiredposition = 0;
    private double shoulderTolerance = 0.75;
    
  
    /** Creates a new Shoulder.
     * <p>
     * Controls the Shoulder motors with a PID
     */
    public ShoulderPID() {
      //pid.setTolerance(0.75);
      setClimb(false);
    }
    
    public void incrementPID(double speed) {
       if ((desiredposition<=8 && speed >0) || (desiredposition >= -60 && speed<0)) {
        desiredposition+=speed;
        //pid.setSetpoint(desiredposition);
        
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
    public void setPID(double value){
    
          desiredposition = value;
          //pid.setSetpoint(desiredposition);
          
        
    }
    public double getSetpoint(){
      //return pid.getSetpoint();
      return desiredposition;
    }
    public static double getAbsoluteAngle() {
      //-141.4 straight up
      //-130 forward
      //-177 back
      double output = rotationCANcoder.getAbsolutePosition().getValueAsDouble()*360+142.9;
    if(output>180){
      output-=360;
    }
    return output;
    
  }
  public boolean atSetpoint(){
    // return pid.atSetpoint();
    if (Math.abs(desiredposition - getAbsoluteAngle()) < shoulderTolerance) {
      return true;
  }
  else {
      return false;
  }
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
  @Override
  public void periodic() {
    robotToM4.INSTANCE.setElevatorAngle(getAbsoluteAngle());

    // This method will be called once per scheduler run
    SmartDashboard.putNumber("Shoulder Absolute Angle", getAbsoluteAngle());
    // SmartDashboard.putNumber("ShoulderPid DesiredPos", desiredposition);
    SmartDashboard.putNumber("shoulderPID actualSetpoint",desiredposition);
    double output;
    if(getClimb()==false){
      Matrix<N1, N5> helperMatrix = new Matrix<>(new SimpleMatrix(1, 5));
      helperMatrix.set(0, 0, desiredposition - getAbsoluteAngle());
      Matrix<N1, N4> slice = errorHistory.block(1, 4, 0, 0);
      helperMatrix.getStorage().insertIntoThis(0, 1, slice.getStorage());
      errorHistory = helperMatrix;

      output = errorHistory.times(PIDMatrix).get(0, 0);
      
        
    
    //SmartDashboard.putNumber("shoulder output ", -output);
    wristMotor1.set(-output);
    }
  }
}
