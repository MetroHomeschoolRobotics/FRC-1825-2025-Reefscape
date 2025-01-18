package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Intake;

public class Score extends Command {
    Elevator elevator = new Elevator();
    Intake intake = new Intake();
    int level;

    public Score(Elevator _elevator,Intake _intake,int _level){
        addRequirements(_elevator);
        addRequirements(_intake);

        elevator = _elevator;
        intake = _intake;
        level = _level;
    }
    @Override
    public void initialize(){}

    @Override
    public void execute(){}

    @Override
    public boolean isFinished(){
        return false;
    }
    @Override
    public void end(boolean interrupted){}

}
