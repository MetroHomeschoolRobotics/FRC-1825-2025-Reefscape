package frc.robot.subsystems;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Constants;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.revrobotics.servohub.ServoHub.ResetMode;
import com.revrobotics.spark.SparkLowLevel;


public class Elevator extends SubsystemBase {
    //TO/DO object for beambreak
    
    //code for encoders may or may not be broken idk

    SparkMax elevatorMotor1 = new SparkMax(Constants.elevatorDeviceID1, SparkLowLevel.MotorType.kBrushless);
    SparkMax elevatorMotor2 = new SparkMax(Constants.elevatorDeviceID2, SparkLowLevel.MotorType.kBrushless);
    
    //90% sure those are the right motor objects(they were not)(they are now)
    
  
    

    public Elevator(){

        elevatorMotor1.setInverted(true);
        //deprecated, calling it fine for now
    }

    public void setSpeed(double speed, double distanceToLimit){
        //TO/DO MAKE SURE THE DISTANCE IS RIGHT BEFORE RUNNING, IT PROBABLY ISNT
        //!!!
        
        if(speed <=0 && getDistance()>= -Constants.elevatorMaxHeight && distanceToLimit> Constants.distToLimOffset){
            
            elevatorMotor1.set(speed);
            elevatorMotor2.set(speed);
            
        }else if(speed>=0){//&& !beambreak.get() //TO/DO put left code in elif statement
            
            elevatorMotor1.set(speed);
            elevatorMotor2.set(speed);
        }else{
            SmartDashboard.putBoolean("speed no changed", true);
            elevatorMotor1.set(0);
            elevatorMotor2.set(0);
        }
    }

    public double getDistance(){
        
        return (elevatorMotor1.getEncoder().getPosition()+elevatorMotor2.getEncoder().getPosition())/2;
        
    }
    public void resetEncoders(){
        
        elevatorMotor1.getEncoder().setPosition(0);
        elevatorMotor2.getEncoder().setPosition(0);
    }
    public boolean isLowest(){
        return false;
        //TO/DO return beambreak value
    }
    public void periodic(){
        SmartDashboard.putBoolean("elevator lowest", isLowest());
        SmartDashboard.putNumber("elevator distance", getDistance());

        if(isLowest()){
            resetEncoders();
        }
    }
}
