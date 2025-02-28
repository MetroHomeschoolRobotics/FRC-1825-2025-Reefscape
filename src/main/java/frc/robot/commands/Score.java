package frc.robot.commands;
//https://www.chiefdelphi.com/t/how-to-make-a-elevator-go-to-each-level-with-a-button-press-also-homing-system/482214/5
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Intake;

public class Score extends Command {
    Elevator elevator;
    Intake intake;
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
    public void execute(){
        //mayhaps, perchance even
        switch (level) {
            case 1:
                elevator.setPID(Constants.level1Height);
                if(elevator.atSetpoint()){
                    intake.setSpeed(Constants.shooterSpeed);
                }
            case 2:
                
                elevator.setPID(Constants.level2Height);
                if(elevator.atSetpoint()){
                    intake.setSpeed(Constants.shooterSpeed);
                }
            case 3:
                
                elevator.setPID(Constants.level3Height);
                if(elevator.atSetpoint()){
                    intake.setSpeed(Constants.shooterSpeed);
                }
            case 4:
                elevator.setPID(Constants.level4Height);
                if(elevator.atSetpoint()){
                    intake.setSpeed(Constants.shooterSpeed);
                }
        
            
        }
    }

    
    
   

    @Override
    public boolean isFinished(){
        return !intake.coralInIntake();
    }
    @Override
    public void end(boolean interrupted){
        intake.setSpeed(0);
        elevator.setSpeed(0,2);
        
    }

}
