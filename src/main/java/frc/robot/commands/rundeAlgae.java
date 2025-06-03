package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;

import frc.robot.subsystems.deAlgae;

public class rundeAlgae extends Command {
    private deAlgae deAlgae;
    
    public rundeAlgae(deAlgae _deAlgae){
        addRequirements(_deAlgae);
        deAlgae = _deAlgae;

    }

    @Override
    public void execute() {
        m_subsystem.runMotor();
    }

    @Override
    public void end(boolean interrupted) {
        m_subsystem.stopMotor();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
    */

    // Nothing here for now.
}
