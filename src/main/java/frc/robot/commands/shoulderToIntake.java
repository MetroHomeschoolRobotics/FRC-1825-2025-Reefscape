package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Shoulder;
import frc.robot.subsystems.ShoulderPID;

public class shoulderToIntake extends Command {


/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */


  private ShoulderPID shoulder;
  private Elevator elevator;
  
  /** Creates a new Shoulder to intake command. 
   * Moves the shoulder and elevator to the intake position.
   * Ends when both PID loops have finished.
   * @param _shoulder The shoulder subsystem object
   * @param _elevator The elevator subsystem object
  */
  public shoulderToIntake(ShoulderPID _shoulder,Elevator _elevator) {
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(_shoulder);
    addRequirements(_elevator);

    shoulder = _shoulder;
    elevator = _elevator;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // TO/DO: Make it so that it doesn't go outside of starting perimeter
    elevator.setPID(-93.66);
    shoulder.setPID(-33);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return elevator.atSetpoint() && shoulder.atSetpoint();
  }
}


