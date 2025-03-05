package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.Shoulder;
import frc.robot.subsystems.ShoulderPID;

public class shoulderToIntake extends Command {


/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class RunShoulder extends Command {

  private ShoulderPID shoulder;
  
  
  /** Creates a new RunShoulder. */
  public void shoulderToIntake(ShoulderPID _shoulder) {
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(_shoulder);

    shoulder = _shoulder;
    
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // TO/DO: Make it so that it doesn't go outside of starting perimeter
    
    shoulder.setPID(-32);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}

}
