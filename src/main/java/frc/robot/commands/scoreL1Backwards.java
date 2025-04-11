package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.ShoulderPID;

public class scoreL1Backwards extends Command {
    Elevator elevator;
    ShoulderPID shoulder;
    Intake intake;
    int timer;
    public scoreL1Backwards(Elevator _elevator, ShoulderPID _shoulder,Intake _intake){
        elevator = _elevator;
        shoulder = _shoulder;
        intake = _intake;
        addRequirements(_elevator);
        addRequirements(_shoulder);
        addRequirements(_intake);
    }
    public void initialize(){
       timer = 1;
    }
    public void execute(){
        timer+=1;
        if(elevator.atSetpoint()){
            shoulder.setPID(Constants.fieldConstants.level1Angle);
            if(elevator.atSetpoint()&&shoulder.atSetpoint()){
                
                intake.scoreBackwards();
            }
        }
    }
    public boolean isFinished(){
        return timer>35&&!intake.coralInIntake();
    }
    public void end(boolean interrupted){
        shoulder.setPID(-33);
        intake.setSpeed(0);
    }
}
