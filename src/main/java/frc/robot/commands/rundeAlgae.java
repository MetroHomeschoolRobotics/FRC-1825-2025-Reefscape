package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.deAlgae;

public class rundeAlgae extends Command {
    private deAlgae deAlgae;
    
    public rundeAlgae(deAlgae _deAlgae){
        addRequirements(_deAlgae);
        deAlgae = _deAlgae;
    }
    public void initialize(){}
    public void execute(){
        deAlgae.runDeAlgae();
    }
    public void end(boolean interrupted){
        deAlgae.stopDeAlgae();
    }
    public boolean isFinished(){
        return false;
    }
}
