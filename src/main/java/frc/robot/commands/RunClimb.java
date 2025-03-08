package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShoulderPID;
import frc.robot.subsystems.climber;

public class RunClimb extends Command {
    private climber climber;
    private ShoulderPID shoulder;
    public RunClimb(climber _climber, ShoulderPID _shoulder){
        addRequirements(_climber);
        addRequirements(_shoulder);
        shoulder = _shoulder;
        climber = _climber;
    }
    public void initialize(){}
    public void execute(){
        climber.openClimber();
        if(climber.atSetpoint()){
            shoulder.setPID(-50);
        }
    }
    public boolean isFinished(){
        return false;
    }
    public void end(){}
}
