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

    public Score(Elevator _elevator, ShoulderPID _shoulder,Intake _intake, int _level){//re-add intake
        addRequirements(_elevator);
        //addRequirements(_intake);

        elevator = _elevator;
        shoulder = _shoulder;
        intake = _intake;
        level = _level;
    }
    @Override
    public void initialize(){
        shoulder.setPID(-5);
        if(level == 4 || level == 3){
            shoulder.setPID(-8);
        }
    }

    @Override
    public void execute(){
        //mayhaps, perchance even
        if(level==1){
            if(shoulder.atSetpoint()){
                elevator.setPID(Constants.fieldConstants.level1Height);
                if(elevator.atSetpoint()){
                    shoulder.setPID(Constants.fieldConstants.level1Angle);
                }
            }
            

        }else if(level==2){
            
            
            
            if(shoulder.atSetpoint()){
                elevator.setPID(Constants.fieldConstants.level2Height);
                if(elevator.atSetpoint()){
                    shoulder.setPID(Constants.fieldConstants.level2Angle);
                }
            }
            
            
        }else if(level==3){
            //System.out.println("level 3");
            
            
            if(shoulder.atSetpoint()){
                elevator.setPID(Constants.fieldConstants.level3Height);
                if(elevator.atSetpoint()){
                    shoulder.setPID(Constants.fieldConstants.level3Angle);
                }
            }
            
           
        
            
        }else if(level==4){
            
            if(shoulder.atSetpoint()){
                elevator.setPID(Constants.fieldConstants.level4Height);
                if(elevator.atSetpoint()){
                    shoulder.setPID(Constants.fieldConstants.level4Angle);
                }
            }
            
            
        }
            
        }
    

    
    
   

    @Override
    public boolean isFinished(){
       // return !intake.coralInIntake();
        //return false;
        if(shoulder.getSetpoint() != -5 && shoulder.getSetpoint() != -8){
            return (elevator.atSetpoint() && shoulder.atSetpoint());
        }
        return false;
    }
    @Override
    public void end(boolean interrupted){
        
    }

}
