package frc.robot.subsystems;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.PIDController;
import frc.robot.Constants;
import com.revrobotics.spark.SparkMax;
import dev.doglog.DogLog;
import frc.robot.RobotContainer;

import com.revrobotics.spark.SparkLowLevel;

//78 cm high at base
//190 cm high at apex
public class Elevator extends SubsystemBase {
    
    
    
    
    private PIDController pid = new PIDController(.0329, 0.00, 0.00066);
    private ElevatorFeedforward feedforward = new ElevatorFeedforward(0.0, 0.18, 0);

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
        return (-elevatorMotor2.getEncoder().getPosition()*Constants.elevatorConstants.elevatorConversion)-93.66;
        
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
//PID
    
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

    private void log(double pidOutput){
        DogLog.log("Elevator/desiredPos", desiredposition);
        DogLog.log("Elevator/distance",getDistance());
        DogLog.log("Elevator/atSetpoint",atSetpoint());
        DogLog.log("Elevator/motor1Speed", elevatorMotor1.get());
        DogLog.log("Elevator/motor1RPM", elevatorMotor1.getEncoder().getVelocity());
        DogLog.log("Elevator/isLowest",isLowest());
        DogLog.log("Elevator/pidOutput", pidOutput);
    }
    public void periodic(){
        SmartDashboard.putNumber("desiredPos", pid.getSetpoint());
        // SmartDashboard.putNumber("elevator error", pid.getError());
        SmartDashboard.putNumber("elevator distance", getDistance());
         SmartDashboard.putNumber("encoderValue", getEncoder());
        // SmartDashboard.putBoolean("atSetpoint", atSetpoint());
        SmartDashboard.putNumber("motor1 speed", elevatorMotor1.get());
        SmartDashboard.putNumber("motor1 rpm", elevatorMotor1.getEncoder().getVelocity());
        if(getDistance()<highestGetDistance){
            highestGetDistance = getDistance();
        }
        //SmartDashboard.putNumber("highestGetDistance", highestGetDistance);
        double output;
        if(pid.getSetpoint()<-98.66){
            output = pid.calculate(getDistance())-feedforward.calculate(0);
           //output = pid.calculate(getDistance())-(0.1+0.07*Math.cos(ShoulderPID.getAbsoluteAngle()));
  
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

        //if(pid.getError()<15 && pid.getError()>-15){
            //MathUtil.clamp(output,-0.05,0.03);
        //}else{
           output= MathUtil.clamp(output,-0.99,0.3);
       // }
       
        SmartDashboard.putNumber("pid output", output);
        SmartDashboard.putBoolean("Elevator atsetpoint", atSetpoint());
        
        elevatorMotor1.set(output);
        elevatorMotor2.set(-output);
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
      if(frc.robot.subsystems.robotToM4.INSTANCE !=null){
        frc.robot.subsystems.robotToM4.INSTANCE.setElevatorBeamBreak(isLowest());
      }
       if(RobotContainer.developerMode == true){
        log(output);
       }
    }

}

