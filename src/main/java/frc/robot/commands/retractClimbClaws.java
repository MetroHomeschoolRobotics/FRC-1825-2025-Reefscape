package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.climber;
import frc.robot.subsystems.robotToM4;

public class retractClimbClaws extends Command {
    private climber climber;
    
    public retractClimbClaws(climber _climber){
        addRequirements(_climber);
       
        climber = _climber;
    }
    public void initialize(){}
    public void execute(){
        climber.setClimber(0.4);
      
    }
    public boolean isFinished(){
        return false;
    }
    public void end(boolean interrupted){
        robotToM4.changeMode("CLIMBACTIVE");
        
        //climber.openClimber();
        // if(interrupted){
        //     climber.setClimber(0.0);
        // }
        
    }
}
