package frc.robot.subsystems;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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
    
    
    
    private PIDController pid = new PIDController(.01, 0, 0);
    private double desiredposition = 0;

    private SparkMax elevatorMotor1 = new SparkMax(Constants.elevatorDeviceID1, SparkLowLevel.MotorType.kBrushless);
    private SparkMax elevatorMotor2 = new SparkMax(Constants.elevatorDeviceID2, SparkLowLevel.MotorType.kBrushless);
    private DigitalInput beambreak = new DigitalInput(1);
    
    
    //90% sure those are the right motor objects(they were not)(they are now)
    //private SparkBaseConfig config = new SparkMaxConfig().inverted(true);
    
    

    public Elevator(){
        

        //mayhaps idk, if it doesnt work make the  motor1 speeds negative in periodic()
        //elevatorMotor1.configure(
          //  config, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);
        //elevatorMotor1.setInverted(true);
        
    }

    public void setSpeed(double speed, double distanceToLimit){
        
        
        if(speed <=0 && desiredposition>= Constants.elevatorMaxHeight && distanceToLimit> Constants.distToLimOffset){
            
            //elevatorMotor1.set(speed);
            //elevatorMotor2.set(speed);
            desiredposition += speed;
            pid.setSetpoint(desiredposition);
            
        }else if(speed>=0 && !beambreak.get() ){//!beambreak.get()
            
            //elevatorMotor1.set(speed);
            //elevatorMotor2.set(speed);
            desiredposition += speed;
            pid.setSetpoint(desiredposition);
            
        }else{
            pid.setSetpoint(desiredposition);
            //elevatorMotor1.set(0);
            //elevatorMotor2.set(0);
            
        }
    }

    public double getDistance(){
        
        return ((elevatorMotor1.getEncoder().getPosition()*Constants.elevatorGearConversion)+
        (elevatorMotor2.getEncoder().getPosition()*Constants.elevatorGearConversion))/2;
        
    }
    public void resetEncoders(){
        
        elevatorMotor1.getEncoder().setPosition(0);
        elevatorMotor2.getEncoder().setPosition(0);
    }
    public boolean isLowest(){
        return beambreak.get();
        
    }
    public void setPID(double setPoint){
        desiredposition = setPoint;
        pid.setSetpoint(desiredposition);
    }
    public boolean atSetpoint(){
        return pid.atSetpoint();
    }
    public void periodic(){
        SmartDashboard.putBoolean("elevator lowest", isLowest());
        SmartDashboard.putNumber("elevator distance", getDistance());

        double output = pid.calculate(getDistance());
        elevatorMotor1.set(output);
        elevatorMotor2.set(output);
        System.out.println(desiredposition);
        if(isLowest()){
            resetEncoders();
            
        }
    }
}
