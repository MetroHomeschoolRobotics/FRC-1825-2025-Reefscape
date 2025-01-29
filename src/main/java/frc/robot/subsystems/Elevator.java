package frc.robot.subsystems;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Constants;
import com.revrobotics.spark.SparkMax;

import com.revrobotics.spark.SparkBase;

import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkMaxConfig;

import com.revrobotics.spark.SparkLowLevel;


public class Elevator extends SubsystemBase {
    
    
    //code for encoders may or may not be broken idk

    private SparkMax elevatorMotor1 = new SparkMax(Constants.elevatorDeviceID1, SparkLowLevel.MotorType.kBrushless);
    private SparkMax elevatorMotor2 = new SparkMax(Constants.elevatorDeviceID2, SparkLowLevel.MotorType.kBrushless);
    private DigitalInput beambreak = new DigitalInput(1);

    
    //90% sure those are the right motor objects(they were not)(they are now)
    private SparkBaseConfig config = new SparkMaxConfig().inverted(true);
    
    

    public Elevator(){
        

        //mayhaps idk, if it doesnt work make the  motor1 speeds negative in setSpeed()
        elevatorMotor1.configure(
            config, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);
        //elevatorMotor1.setInverted(true);
        
    }

    public void setSpeed(double speed, double distanceToLimit){
        //TO/DO MAKE SURE THE DISTANCE IS RIGHT BEFORE RUNNING, IT PROBABLY ISNT
        //!!!
        
        if(speed <=0 && getDistance()>= -Constants.elevatorMaxHeight && distanceToLimit> Constants.distToLimOffset){
            
            elevatorMotor1.set(speed);
            elevatorMotor2.set(speed);
            
            
        }else if(speed>=0 && !beambreak.get()){
            
            elevatorMotor1.set(speed);
            elevatorMotor2.set(speed);
            
            
        }else{
            SmartDashboard.putBoolean("speed no changed", true);
            elevatorMotor1.set(0);
            elevatorMotor2.set(0);
            
        }
    }

    public double getDistance(){
        
        return ((elevatorMotor1.getEncoder().getPosition()*Constants.vortexcmConversion)+
        (elevatorMotor2.getEncoder().getPosition()*Constants.vortexcmConversion))/2;
        
    }
    public void resetEncoders(){
        
        elevatorMotor1.getEncoder().setPosition(0);
        elevatorMotor2.getEncoder().setPosition(0);
    }
    public boolean isLowest(){
        return beambreak.get();
        
    }
    public void periodic(){
        SmartDashboard.putBoolean("elevator lowest", isLowest());
        SmartDashboard.putNumber("elevator distance", getDistance());

        if(isLowest()){
            resetEncoders();
        }
    }
}
