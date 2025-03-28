package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.ClimbPiston;

public class RunClimbPiston extends Command {
    private ClimbPiston piston;
    private CommandXboxController xbox;

    public RunClimbPiston(ClimbPiston _Piston){
        addRequirements(_Piston);
        piston = _Piston;
        
    }
    public void initialize(){}

    public void execute(){
        piston.RunPiston(1);
    }
    public void end(boolean interrupted){
        if(interrupted){
            piston.RunPiston(0);
        }else{
            piston.RunPiston(0);
        }
        
    }
    public boolean isFinished(){
        return piston.getEncoder()>144;
    }
}
