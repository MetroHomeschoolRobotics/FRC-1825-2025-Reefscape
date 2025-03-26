package frc.robot.subsystems;

import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class climber extends SubsystemBase {
    private PIDController pid = new PIDController(0.05, 0, 0);
    private SparkMax climber = new SparkMax(Constants.MotorIDs.climberMotorId, SparkLowLevel.MotorType.kBrushless);
    public climber(){
        pid.setTolerance(0.5);
        pid.setSetpoint(0);
    }
    public void setClimber(double setpoint){
        pid.setSetpoint(setpoint);
    }
   
    public void stopClimber(){
        climber.set(0);
    }
    public void resetEncoders(){
        climber.getEncoder().setPosition(0);
    }
    public boolean atSetpoint(){
        if(pid.getSetpoint() != 0){
            return pid.atSetpoint();
        }else{
            return false;
        }
        
    }
   public void periodic(){
    SmartDashboard.putNumber("climber encoder",climber.getEncoder().getPosition());
    SmartDashboard.putBoolean("At setpoint", atSetpoint());
    SmartDashboard.putNumber("climber pid pos", pid.getSetpoint());

    double output = pid.calculate(climber.getEncoder().getPosition());
    SmartDashboard.putNumber("climber", output);
    climber.set(output);
    //if(climber.getEncoder().getPosition()>=18 || climber.getEncoder().getPosition()<=0){
      //  stopClimber();
    //}
   }
}
