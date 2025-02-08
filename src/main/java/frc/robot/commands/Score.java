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
                elevator.setPID(0);
                if(elevator.atSetpoint()){
                    intake.setSpeed(Constants.shooterSpeed);
                }
            case 2 :
                elevator.setPID(Constants.elevatorMaxHeight/2.25);
                if(elevator.atSetpoint()){
                    intake.setSpeed(Constants.shooterSpeed);
                }
            case 3      :
                elevator.setPID(Constants.elevatorMaxHeight*2/3);
                if(elevator.atSetpoint()){
                    intake.setSpeed(Constants.shooterSpeed);
                }
            case 4          :
                elevator.setPID(Constants.elevatorMaxHeight);
                if(elevator.atSetpoint()){
                    intake.setSpeed(Constants.shooterSpeed);
                }
        
            
        }
    }
    public void eexecute(){
        double distToLim = Constants.elevatorMaxHeight+elevator.getDistance();
        
        switch(level){
            case 1:
                if(level ==1 && elevator.isLowest()){
                intake.setSpeed(Constants.shooterSpeed);
                
                }else if(level==1 &&!elevator.isLowest()){
                    while(!elevator.isLowest()){
                        elevator.setSpeed(-1,distToLim);
                    }
                    elevator.setSpeed(0,183-elevator.getDistance());
                    intake.setSpeed(Constants.shooterSpeed);
                      
                }
            case 4:
                if(level==4 && elevator.getDistance()<=Constants.elevatorMaxHeight){
                    intake.setSpeed(Constants.shooterSpeed);
                    
                }else if(level==4 && elevator.getDistance()>Constants.elevatorMaxHeight){
                    while(elevator.getDistance()<Constants.elevatorMaxHeight){
                        elevator.setSpeed(-1,distToLim);
                    }
                    elevator.setSpeed(0,183-elevator.getDistance());
                    intake.setSpeed(Constants.shooterSpeed);
                     
                }
            case 3:
                if(level==3 && elevator.getDistance()>=((Constants.elevatorMaxHeight*2)/3)){
                    intake.setSpeed(Constants.shooterSpeed);
                    

                }else if(level==3 && elevator.getDistance()<((Constants.elevatorMaxHeight*2)/3)){
                    while(elevator.getDistance()<((Constants.elevatorMaxHeight*2)/3)){
                        elevator.setSpeed(1, distToLim);
                    }
                    elevator.setSpeed(0,(Constants.elevatorMaxHeight*2)/3);
                    intake.setSpeed(Constants.shooterSpeed);
                    
                }else if(level==3 && elevator.getDistance()>((Constants.elevatorMaxHeight*2)/3)){
                    while(elevator.getDistance()>((Constants.elevatorMaxHeight*2)/3)){
                        elevator.setSpeed(-1, distToLim);
                    }
                    elevator.setSpeed(0,(Constants.elevatorMaxHeight*2)/3);
                    intake.setSpeed(Constants.shooterSpeed);
                    

                }
            case 2:
                if(level ==2 && elevator.getDistance()==(Constants.elevatorMaxHeight/2.25)){
                    intake.setSpeed(Constants.shooterSpeed);
                    
                }else if(level==2&&elevator.getDistance()<(Constants.elevatorMaxHeight/2.25)){
                    while(elevator.getDistance()<(Constants.elevatorMaxHeight/2.25)){
                        elevator.setSpeed(1, distToLim);
                    }
                    elevator.setSpeed(0,(Constants.elevatorMaxHeight/2.25));
                    intake.setSpeed(Constants.shooterSpeed);
                    
                }else if(level==2&&elevator.getDistance()>(Constants.elevatorMaxHeight/2.25)){
                    while(elevator.getDistance()>(Constants.elevatorMaxHeight/2.25)){
                        elevator.setSpeed(-1, distToLim);
                    }
                    elevator.setSpeed(0,(Constants.elevatorMaxHeight/2.25));
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
