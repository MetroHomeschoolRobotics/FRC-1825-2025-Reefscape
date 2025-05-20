package frc.robot.commands;
//slurp
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Intake;

public class RunIntakeBackwards extends Command {

    private Intake intake;
    
    /**
     * Runs the intake backwards.
     * Does not reference the beambreak
     * 
     * @param _intake The intake subsystem object
     */
    public RunIntakeBackwards(Intake _intake){
        addRequirements(_intake);
        
        intake = _intake;
        
        
    }
    @Override
    public void initialize() {}

    @Override
    public void execute(){
        
            intake.setSpeed(0.3);
    
    }
    @Override
    public void end(boolean interrupted){
        intake.setSpeed(0);
    }
    @Override
    public boolean isFinished(){
        return false;
        
    }
}
