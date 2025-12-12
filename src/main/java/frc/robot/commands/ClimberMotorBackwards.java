package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.climber;

public class ClimberMotorBackwards extends Command {
    private climber climber;
    
    public ClimberMotorBackwards(climber _climber){
        addRequirements(_climber);
       
        climber = _climber;
    }
    public void initialize(){}
    public void execute(){
        climber.setClimber(0.3);
      
    }
    public boolean isFinished(){
        return false;
    }
    public void end(boolean interrupted){
        //climber.openClimber();
        
            climber.setClimber(0.0);
        
        
    }
}
