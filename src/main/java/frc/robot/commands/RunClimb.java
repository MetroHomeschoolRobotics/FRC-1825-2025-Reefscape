package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShoulderPID;

public class RunClimb extends Command {
    
    private ShoulderPID shoulder;
    
    
    public RunClimb( ShoulderPID _shoulder){
        
        addRequirements(_shoulder);
        
        shoulder = _shoulder;        
        
    }
    public void initialize(){}
    public void execute(){
        
        
        shoulder.setPID(-29);
        
    }
    public boolean isFinished(){
        return false;
    }
    public void end(){}
}
