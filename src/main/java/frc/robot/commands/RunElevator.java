package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.ShoulderPID;




public class RunElevator extends Command {

    private CommandXboxController xboxController;
    private Elevator elevator;
    private ShoulderPID angle;
    private double frameToPivot;
    private double elevatorPID;
    public boolean isLegal = true;
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
        //elevator.setSpeed(MathUtil.applyDeadband(xboxController.getRightY(),0.03), distToLim);
        
        
       
        if((Math.cos(Math.toRadians(-angle.getAbsoluteAngle()-90))*elevator.getDistance()-16.58<70*2.54)
        && (Math.cos(Math.toRadians(angle.getAbsoluteAngle()-90))*elevator.getDistance()-49.23<70*2.54)){
            isLegal = true;
            elevator.setSpeed(MathUtil.applyDeadband(xboxController.getRightY(),0.1), distToLim);
        }else{
            isLegal = false;
            if(xboxController.getRightY()>0){
                elevator.setSpeed(MathUtil.applyDeadband(xboxController.getRightY(),0.1), distToLim);
            }
            // if(angle.getAbsoluteAngle()>0){
            //     elevator.setPID(-(16.58+18*2.54)/Math.toRadians(angle.getAbsoluteAngle()-90));
            // }else{
            //     elevator.setPID((49.23+18*2.54)/Math.toRadians(angle.getAbsoluteAngle()-90));
            // }
            
        }
        //SmartDashboard.putBoolean("elevator legal", isLegal );
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
