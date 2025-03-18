package frc.robot.commands;
//https://www.chiefdelphi.com/t/how-to-make-a-elevator-go-to-each-level-with-a-button-press-also-homing-system/482214/5
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.ShoulderPID;

public class RetractElevator extends Command {
    Elevator elevator;
    
    ShoulderPID shoulder;
   

    public RetractElevator(Elevator _elevator, ShoulderPID _shoulder){
        addRequirements(_elevator);
        addRequirements(_shoulder);
        

        elevator = _elevator;
        shoulder = _shoulder;
       
    }
    @Override
    public void initialize(){
        shoulder.setPID(-8);
    }

    @Override
    public void execute(){
        //mayhaps, perchance even
        
        if(shoulder.getAbsoluteAngle()<=0){
            elevator.setPID(-93.66);
        }

    }

    
    
   

    @Override
    public boolean isFinished(){
       if(elevator.getSetpoint() == -93.66){
        return elevator.atSetpoint() && shoulder.atSetpoint();
       }
        return false;
    }
    @Override
    public void end(boolean interrupted){
     
    }

}
