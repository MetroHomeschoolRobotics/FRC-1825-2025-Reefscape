package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.Intake;

public class RunOuttake extends Command {
    private Intake shooter;
    private int timer;
    public RunOuttake(Intake _shooter){
        addRequirements(_shooter);
        shooter = _shooter;
    }
    @Override
    public void initialize(){
        timer = 0;
    }
    @Override
    public void execute(){
        
        shooter.setSpeed(Constants.shooterSpeed); 
        timer+=1;
        
    }
    @Override
    public void end(boolean interrupted){
        shooter.setSpeed(0);
    }
    @Override
    public boolean isFinished(){
        if(timer>=25){
            return !shooter.coralInIntake();
        }
        return false;
        
    }
}
