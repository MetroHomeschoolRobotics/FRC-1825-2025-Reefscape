package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShoulderPID;
import frc.robot.subsystems.climber;

public class testClimberPID extends Command {
    private climber climber;
    
    public testClimberPID(climber _climber){
        addRequirements(_climber);
       
        climber = _climber;
    }
    public void initialize(){}
    public void execute(){
        climber.closeClimber();
      
    }
    public boolean isFinished(){
        return false;
    }
    public void end(){
        //climber.openClimber();
    }
}
