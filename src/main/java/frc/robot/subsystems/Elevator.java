package frc.robot.subsystems;
// import com.revrobotics.spark.SparkBase;


// import com.revrobotics.spark.config.SparkBaseConfig;
// import com.revrobotics.spark.config.SparkMaxConfig;

import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
// import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Constants;

//78 cm high at base
//190 cm high at apex
public class Elevator extends SubsystemBase {
    
    
    

    private PIDController pid = new PIDController(.030, 0.00, 0.001);

    //
    private double desiredposition = 0;
    private double highestGetDistance;

    private SparkMax elevatorMotor1 = new SparkMax(Constants.MotorIDs.elevatorDeviceID1, SparkLowLevel.MotorType.kBrushless);
    private SparkMax elevatorMotor2 = new SparkMax(Constants.MotorIDs.elevatorDeviceID2, SparkLowLevel.MotorType.kBrushless);
    
    private DigitalInput beambreak = new DigitalInput(1);
    private Boolean beamTriggered = false;
    private int timer = 0;

    
    //90% sure those are the right motor objects(they were not)(they are now)
    //private SparkBaseConfig config = new SparkMaxConfig().inverted(true);
    
    /**
     * Creates a new elevator
     * <p>
     * Controls the elevator motors and reads the elevator sensors
     */
    public Elevator(){
        
        resetEncoders();
        pid.setTolerance(25);
        //mayhaps idk, if it doesnt work make the  motor1 speeds negative in periodic()
        //elevatorMotor1.configure(
          //  config, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);
        //elevatorMotor1.setInverted(true);
        
    }

    public void setSpeed(double speed, double distanceToLimit){
        
       speed*=1.25;//scalar TO//DO tune this
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
        
       return beambreak.get();
        
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
        double output;
        double ModifiedOutput;
        output = pid.calculate(getDistance());
        // Custom feedforward that modifies based on the angle of the shoulder
        if(pid.getSetpoint()<-98.66){
            ModifiedOutput = output-0.17*Math.cos(Math.toRadians(ShoulderPID.getAbsoluteAngle()));
        }else{
            ModifiedOutput = output;
        }
        ModifiedOutput= MathUtil.clamp(ModifiedOutput,-0.99,0.3);
        elevatorMotor1.set(ModifiedOutput);
        elevatorMotor2.set(-ModifiedOutput);

        SmartDashboard.putNumber("elevator Desired Pos", pid.getSetpoint());
        // SmartDashboard.putNumber("elevator error", pid.getError());
        SmartDashboard.putNumber("elevator Distance", getDistance());
         SmartDashboard.putNumber("elevator Encoder Value", getEncoder());
        // SmartDashboard.putBoolean("atSetpoint", atSetpoint());
        SmartDashboard.putNumber("motor1 speed", elevatorMotor1.get());
        SmartDashboard.putNumber("motor1 rpm", elevatorMotor1.getEncoder().getVelocity());
        if(getDistance()<highestGetDistance){
            highestGetDistance = getDistance();
        }
        SmartDashboard.putNumber("Elevator pid output", output);
        SmartDashboard.putNumber("Modified Elevator pid output", ModifiedOutput);
        SmartDashboard.putNumber("Elevator Error", desiredposition-getDistance());
        SmartDashboard.putBoolean("isLowest", isLowest());
        
        if(isLowest() &&timer == 10 && beamTriggered == false ){
            //if(beamTriggered ==false){
                resetEncoders();
                timer = 0;
                beamTriggered = true;
           // }

            
           // beamTriggered = true;
        //}else{
           // beamTriggered = false;
       }else if(isLowest()){
        timer +=1;
       }else{
        timer = 0;
        beamTriggered = false;
       }
    }

}

