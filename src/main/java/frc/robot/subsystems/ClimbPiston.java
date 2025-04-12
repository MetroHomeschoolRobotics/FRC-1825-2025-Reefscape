package frc.robot.subsystems;
//AKA crud

import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.elevatorConstants;

public class ClimbPiston extends SubsystemBase{
    private SparkMax piston = new SparkMax(3, SparkLowLevel.MotorType.kBrushless);//Motor id
    
    private boolean softLimitsOn = true;
    public ClimbPiston(){}
    public void RunPiston(double speed){
    
        if(getEncoder()>20&&speed<0){
            piston.set(speed);
        }else if(speed>=0){
            piston.set(speed);
        } else if(getEncoder()<20&&speed<0){
            piston.set(0);
        }
   
        
        
        //positive speed runs it forward
    }
    public void StopPiston(){
        piston.set(0);
    }
    public double getEncoder(){
        return piston.getEncoder().getPosition();
    }
    public void toggleSoftLimits(){
        if(softLimitsOn == true){
            softLimitsOn=false;
        }else{
            softLimitsOn = true;
        }
    }
    public boolean isSoftLimitsOn(){
        return isSoftLimitsOn();
    }
    public void periodic(){
        SmartDashboard.putNumber("pistonEncoder", getEncoder());
        SmartDashboard.putNumber("actuator current", piston.getOutputCurrent());
        //132 all the way down
    }
}
