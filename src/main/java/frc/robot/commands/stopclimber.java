package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShoulderPID;
import frc.robot.subsystems.climber;

public class stopclimber extends Command {
    private climber climber;
    
    public stopclimber(climber _climber){
        addRequirements(_climber);
       
        climber = _climber;
    }
    public void initialize(){}
    public void execute(){
        climber.setClimber(0);
      
    }
    public boolean isFinished(){
        return false;
    }
    public void end(){
        //climber.openClimber();
        
    }
}
