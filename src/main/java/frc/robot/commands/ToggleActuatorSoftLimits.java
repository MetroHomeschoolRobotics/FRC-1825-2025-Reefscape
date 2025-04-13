package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.ClimbPiston;

public class ToggleActuatorSoftLimits extends Command {
    private ClimbPiston piston;
    

    public ToggleActuatorSoftLimits(ClimbPiston _Piston){
        addRequirements(_Piston);
        piston = _Piston;
        
    }
    public void initialize(){}
    public void execute(){

    }
    public void end(){}
    public boolean isFinished(){
        return true;
    }
}
