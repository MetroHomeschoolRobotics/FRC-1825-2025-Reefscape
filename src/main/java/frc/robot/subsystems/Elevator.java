package frc.robot.subsystems;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Constants;
import com.revrobotics.spark.SparkMax;

import com.revrobotics.spark.SparkBase;


import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkMaxConfig;

import com.revrobotics.spark.SparkLowLevel;

//78 cm high at base
//190 cm high at apex
public class Elevator extends SubsystemBase {
    
    
    
    private PIDController pid = new PIDController(.0095, 0, 0);
    private ElevatorFeedforward feedforward = new ElevatorFeedforward(0.0, 0.09, 0);
    //
    private double desiredposition = 0;
    private double highestGetDistance;
    private SparkMax elevatorMotor1 = new SparkMax(Constants.MotorIDs.elevatorDeviceID1, SparkLowLevel.MotorType.kBrushless);
    private SparkMax elevatorMotor2 = new SparkMax(Constants.MotorIDs.elevatorDeviceID2, SparkLowLevel.MotorType.kBrushless);
    
    //private DigitalInput beambreak = new DigitalInput(1);
    
    
    //90% sure those are the right motor objects(they were not)(they are now)
    //private SparkBaseConfig config = new SparkMaxConfig().inverted(true);
    
    

    public Elevator(){
        
        resetEncoders();
        pid.setTolerance(25);
        //mayhaps idk, if it doesnt work make the  motor1 speeds negative in periodic()
        //elevatorMotor1.configure(
          //  config, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);
        //elevatorMotor1.setInverted(true);
        
    }

    public void setSpeed(double speed, double distanceToLimit){
        
       speed*=1.5;//scalar TO//DO tune this
        if(speed <=0 && desiredposition>= Constants.elevatorConstants.elevatorMaxHeight && distanceToLimit > Constants.distToLimOffset){
            
            desiredposition += speed;
            pid.setSetpoint(desiredposition);
            
        } else if(speed>=0 && desiredposition <-93.66  ){//!beambreak.get()
            
             desiredposition += speed;
             pid.setSetpoint(desiredposition);
            
         }
        
        // SmartDashboard.putNumber("Commanded elevator speed 0-1", speed);
        // SmartDashboard.putNumber("Elevator motor 1 current", elevatorMotor1.getOutputCurrent());
        pid.setSetpoint(desiredposition);
        
    }

    public double getDistance(){
        //113.44 / 20.16 between
        return (elevatorMotor1.getEncoder().getPosition()*Constants.elevatorConstants.elevatorConversion)-93.66;
        
    }
    public double getEncoder(){
        return elevatorMotor1.getEncoder().getPosition();
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
    public double getSetpoint(){
        return pid.getSetpoint();
    }
    public boolean atSetpoint(){
        return pid.atSetpoint();
    }
    public void periodic(){
        SmartDashboard.putNumber("desiredPos", pid.getSetpoint());
        // SmartDashboard.putNumber("elevator error", pid.getError());
        SmartDashboard.putNumber("elevator distance", getDistance());
         SmartDashboard.putNumber("encoderValue", getEncoder());
        // SmartDashboard.putBoolean("atSetpoint", atSetpoint());

        if(getDistance()<highestGetDistance){
            highestGetDistance = getDistance();
        }
        //SmartDashboard.putNumber("highestGetDistance", highestGetDistance);
        double output;
        if(pid.getSetpoint()<-98.66){
            output = pid.calculate(getDistance())-feedforward.calculate(0);
        }else{
            output = pid.calculate(getDistance());
        }
        
        // if(output>0.18){
        //     output=0.18;
        // }

        // if(output>1){
        //     output=1;
        // }else if(output<-1){
        //     output = -1;
        // }
        MathUtil.clamp(output,-1,0.18);
        SmartDashboard.putNumber("pid output", output);

        
        elevatorMotor1.setVoltage(output*12);
        elevatorMotor2.setVoltage(-output*12);
        
        if(isLowest()){
            //resetEncoders();
            
        }
    }
}
