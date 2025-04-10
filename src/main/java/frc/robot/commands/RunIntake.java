package frc.robot.commands;
//slurp
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Intake;

public class RunIntake extends Command {

    private Intake intake;
    

    public RunIntake(Intake _intake){
        addRequirements(_intake);
        
        intake = _intake;
        
        
    }
    @Override
    public void initialize() {}

    @Override
    public void execute(){
        
            if(intake.coralInIntake()){
                //intake.setSpeed(0.4);
            }else{
                intake.setSpeed(-0.1);
            }
        }
    

    @Override
    public void end(boolean interrupted){
        intake.setSpeed(0);
    }
    @Override
    public boolean isFinished(){
        return intake.coralInIntake();
        
    }
}
