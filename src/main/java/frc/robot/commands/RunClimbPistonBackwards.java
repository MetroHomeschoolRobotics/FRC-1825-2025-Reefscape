package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ClimbPiston;

public class RunClimbPistonBackwards extends Command {
    private ClimbPiston piston;

    public RunClimbPistonBackwards(ClimbPiston _Piston){
        addRequirements(_Piston);
        piston = _Piston;
        
    }
    public void initialize(){}

    public void execute(){
       
        
            piston.RunPiston(-0.3);
        
        
    }
    public void end(boolean interrupted){
        if(interrupted){
            piston.RunPiston(0);
        
        }
        
    }
    public boolean isFinished(){
        return false;
    }
}
