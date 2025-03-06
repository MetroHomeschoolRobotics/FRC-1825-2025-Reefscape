package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants;
import frc.robot.subsystems.Elevator;




public class ResetElevatorEncoders extends Command {

    private Elevator elevator;


    public ResetElevatorEncoders(Elevator _elevator){
        addRequirements(_elevator);

        elevator = _elevator;
        
    }
    @Override
    public void initialize() {
        
    }

    @Override
    public void execute(){
        elevator.resetEncoders();
        
    }

    @Override
    public void end(boolean interrupted){
        
    }

    @Override
    public boolean isFinished(){
        return false;
    }
}
