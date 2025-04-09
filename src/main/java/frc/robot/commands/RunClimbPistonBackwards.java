package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.ClimbPiston;

public class RunClimbPistonBackwards extends Command {
    private ClimbPiston piston;
    private CommandXboxController xbox;

    public RunClimbPistonBackwards(ClimbPiston _Piston){
        addRequirements(_Piston);
        piston = _Piston;
        
    }
    public void initialize(){}

    public void execute(){
        if(piston.isSoftLimitsOn()){
            piston.RunPiston(-1);
        }else{
            piston.RunPiston(-0.3);
        }
        
    }
    public void end(boolean interrupted){
        if(interrupted){
            piston.RunPiston(0);
            if(piston.isSoftLimitsOn() == false){
                piston.toggleSoftLimits();
            }
        }
        
    }
    public boolean isFinished(){
        return false;
    }
}
