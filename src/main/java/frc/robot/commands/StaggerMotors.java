package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
// import frc.robot.Constants;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.robotToM4;

public class StaggerMotors extends Command {
    private Intake shooter;
    private int timer;
    private boolean isLeftMotor;

    /**
     * Alternates running the two intake motors.
     * Stops when the beambreak is triggered.
     * 
     * @param _shooter The shooter/intake subsystem object
     */
    public StaggerMotors(Intake _shooter){
        addRequirements(_shooter);
        shooter = _shooter;
    }
    @Override
    public void initialize(){
        timer = 0;
        robotToM4.changeMode("INTAKEACTIVE");
        isLeftMotor = false;
    }
    @Override
    public void execute(){
        if(timer %15 == 0){
            if(isLeftMotor ==true){
                isLeftMotor=false;
            }else{
                isLeftMotor=true;
            }
        }
        if(isLeftMotor){
            shooter.setSpeedSideways(-0.4); 
        }else{
            shooter.setSpeedSideways2(-0.4); 
        }
        
        
        timer+=1;
    }
    @Override
    public void end(boolean interrupted){
        shooter.setSpeed(0);
        robotToM4.changeMode("INTAKECOMPLETE");
    }
    @Override
    public boolean isFinished(){
        return shooter.coralInIntake();
        
    }
}
