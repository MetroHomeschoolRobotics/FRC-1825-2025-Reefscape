package frc.robot.commands;
//slurp
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Intake;

public class RunIntake extends Command {

    private Intake intake;
    private boolean reversed;

    public RunIntake(Intake _intake, Boolean _reversed){
        addRequirements(_intake);
        
        _intake = intake;
    }
    @Override
    public void initialize() {}

    @Override
    public void execute(){
        if(reversed){
            intake.setSpeed(-1);
        }else{
            if(intake.coralInIntake()){
                intake.setSpeed(0);
            }else{
                intake.setSpeed(1);
            }
        }
    }

    @Override
    public void end(boolean interrupted){
        intake.setSpeed(0);
    }
    @Override
    public boolean isFinished(){
        return false;
        //TO/DO change to !Intake.coralInIntake() when beambreak ready
    }
}
