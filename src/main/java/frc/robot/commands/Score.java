package frc.robot.commands;
//https://www.chiefdelphi.com/t/how-to-make-a-elevator-go-to-each-level-with-a-button-press-also-homing-system/482214/5
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.ShoulderPID;

public class Score extends Command {
    Elevator elevator;
    Intake intake;
    ShoulderPID shoulder;
    int level;

    public Score(Elevator _elevator, ShoulderPID _shoulder, int _level){//re-add intake
        addRequirements(_elevator);
        //addRequirements(_intake);

        elevator = _elevator;
        shoulder = _shoulder;
        //intake = _intake;
        level = _level;
    }
    @Override
    public void initialize(){}

    @Override
    public void execute(){
        //mayhaps, perchance even
        if(level==1){
            elevator.setPID(Constants.level1Height);
        }else if(level==2){
            elevator.setPID(Constants.level2Height);
        }else if(level==3){
            //System.out.println("level 3");
            elevator.setPID(Constants.level3Height);
        }else if(level==4){
            elevator.setPID(Constants.level4Height);
        }
            
        }
    

    
    
   

    @Override
    public boolean isFinished(){
       // return !intake.coralInIntake();
        return elevator.atSetpoint();

    }
    @Override
    public void end(boolean interrupted){
        //intake.setSpeed(0);
        //elevator.setSpeed(0,2);
        //System.out.println("all done");
    }

}
