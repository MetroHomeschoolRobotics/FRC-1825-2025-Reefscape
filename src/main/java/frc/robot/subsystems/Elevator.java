package frc.robot.subsystems;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Constants;
import com.revrobotics.spark.SparkMax;

import com.revrobotics.spark.SparkBase;


import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkMaxConfig;

import com.revrobotics.spark.SparkLowLevel;

//4.826cm diameter on shaft 62t driven by 2 8t
//gear ratio 62/8
public class Elevator extends SubsystemBase {
    
    
    
    private PIDController pid = new PIDController(.004, 0, 0);
    private ElevatorFeedforward feedforward = new ElevatorFeedforward(0.0, 0.1, 0);
    //
    private double desiredposition = 0;

    private SparkMax elevatorMotor1 = new SparkMax(Constants.elevatorDeviceID1, SparkLowLevel.MotorType.kBrushless);
    private SparkMax elevatorMotor2 = new SparkMax(Constants.elevatorDeviceID2, SparkLowLevel.MotorType.kBrushless);
    
    //private DigitalInput beambreak = new DigitalInput(1);
    
    
    //90% sure those are the right motor objects(they were not)(they are now)
    //private SparkBaseConfig config = new SparkMaxConfig().inverted(true);
    
    

    public Elevator(){
        
        resetEncoders();
        
        //mayhaps idk, if it doesnt work make the  motor1 speeds negative in periodic()
        //elevatorMotor1.configure(
          //  config, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);
        //elevatorMotor1.setInverted(true);
        
    }

    public void setSpeed(double speed, double distanceToLimit){
        
       speed*=1;//scalar TO//DO tune this
        if(speed <=0 && desiredposition>= Constants.elevatorMaxHeight && distanceToLimit > Constants.distToLimOffset){
            
            desiredposition += speed;
            pid.setSetpoint(desiredposition);
            
        } else if(speed>=0 && desiredposition <0  ){//!beambreak.get()
            
             desiredposition += speed;
             pid.setSetpoint(desiredposition);
            
         }
        
        // SmartDashboard.putNumber("Commanded elevator speed 0-1", speed);
        // SmartDashboard.putNumber("Elevator motor 1 current", elevatorMotor1.getOutputCurrent());
        pid.setSetpoint(desiredposition);
        
    }

    public double getDistance(){
        
        return (elevatorMotor2.getEncoder().getPosition()*Constants.elevatorGearConversion);
        
    }
    public void resetEncoders(){
        
        elevatorMotor1.getEncoder().setPosition(0);
        elevatorMotor2.getEncoder().setPosition(0);
    }
    public boolean isLowest(){
        return false;
       // return beambreak.get();
        
    }
    public void setPID(double setPoint){
        desiredposition = setPoint;
        pid.setSetpoint(desiredposition);
    }
    public boolean atSetpoint(){
        return pid.atSetpoint();
    }
    public void periodic(){
        SmartDashboard.putNumber("desiredPos", desiredposition);
        SmartDashboard.putNumber("elevator error", pid.getError());
        SmartDashboard.putNumber("elevator distance", getDistance());
        double output;
        if(getDistance()>-5){
            output = pid.calculate(getDistance())-feedforward.calculate(0);
        }else{
            output = pid.calculate(getDistance());
        }
        
        SmartDashboard.putNumber("pid output", output);
        
        elevatorMotor1.setVoltage(-output*12);
        elevatorMotor2.setVoltage(output*12);
        
        if(isLowest()){
            resetEncoders();
            
        }
    }
}
