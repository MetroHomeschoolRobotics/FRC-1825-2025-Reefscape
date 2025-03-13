package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.ShoulderPID;




public class RunElevator extends Command {

    private CommandXboxController xboxController;
    private Elevator elevator;
    private ShoulderPID angle;

    public RunElevator(Elevator _elevator,CommandXboxController _xboxController,ShoulderPID _angle){
        addRequirements(_elevator);

        elevator = _elevator;
        xboxController = _xboxController;
        angle = _angle;
    }
    @Override
    public void initialize() {
        
    }

    @Override
    public void execute(){
        
        double distToLim = (-Constants.elevatorConstants.elevatorMaxHeight)-elevator.getDistance();
        //double distToLim = (-Constants.elevatorMaxHeight)-elevator.getDistance();
        elevator.setSpeed(MathUtil.applyDeadband(xboxController.getRightY(),0.03), distToLim);
        if((angle.getAbsoluteAngle()<-20 && elevator.getSetpoint() <-130) ){
            elevator.setPID(-93.66);
        }   
        //SmartDashboard.putNumber("elevator.getDistance",xboxController.getRightY());
    }

    @Override
    public void end(boolean interrupted){
        
    }

    @Override
    public boolean isFinished(){
        return false;
    }
}
