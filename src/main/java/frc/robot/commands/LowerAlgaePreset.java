package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
//import frc.robot.Constants;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.ShoulderPID;

public class LowerAlgaePreset extends Command {
    private Elevator elevator;
    private ShoulderPID shoulder;


    public LowerAlgaePreset(Elevator _elevator,ShoulderPID _shoulder){
        addRequirements(_elevator);
        addRequirements(_shoulder);

        elevator = _elevator;
        shoulder = _shoulder;
    }
    public void initialize(){
        shoulder.setPID(0);
    }
    public void execute(){
        if(shoulder.atSetpoint()){
            elevator.setPID(-93.66);
            
        }
    }
}