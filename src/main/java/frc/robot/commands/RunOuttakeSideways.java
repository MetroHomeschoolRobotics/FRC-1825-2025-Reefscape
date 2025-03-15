package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.Intake;

public class RunOuttakeSideways extends Command {
    private Intake shooter;
    
    public RunOuttakeSideways(Intake _shooter){
        addRequirements(_shooter);
        shooter = _shooter;
    }
    @Override
    public void initialize(){
        
    }
    @Override
    public void execute(){
        
        shooter.setSpeedSideways(-0.4); 
        
        
    }
    @Override
    public void end(boolean interrupted){
        shooter.setSpeed(0);
    }
    @Override
    public boolean isFinished(){
        //return !shooter.coralInIntake();
        return false;
    }
}
