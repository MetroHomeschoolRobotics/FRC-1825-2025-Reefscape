package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.Intake;

public class RunOuttake extends Command {
    private Intake shooter;
    
    public RunOuttake(Intake _shooter){
        addRequirements(_shooter);
        shooter = _shooter;
    }
    @Override
    public void initialize(){
        
    }
    @Override
    public void execute(){
        if(shooter.coralInIntake()){
            shooter.setSpeed(Constants.shooterSpeed); 
        }
        
    }
    @Override
    public void end(boolean interrupted){
        shooter.setSpeed(0);
    }
    @Override
    public boolean isFinished(){
        return !shooter.coralInIntake();
    }
}
