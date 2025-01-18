package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.Elevator;

//TO/DO once CAD figures elevator stuff out make math utilities with 
//measurements from the crescendo mathutils/geometryutils


public class RunElevator extends Command {

    private CommandXboxController xboxController;
    private Elevator elevator;


    public RunElevator(Elevator _elevator,CommandXboxController _xboxController){
        addRequirements(_elevator);

        elevator = _elevator;
        xboxController = _xboxController;
    }
    @Override
    public void initialize() {}

    @Override
    public void end(boolean interrupted){}

    @Override
    public boolean isFinished(){
        return false;
    }
}
