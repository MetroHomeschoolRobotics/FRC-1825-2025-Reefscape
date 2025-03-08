package frc.robot.commands;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.ShoulderPID;

public class RunShoulderPID extends Command {
    private ShoulderPID shoulder;
  private CommandXboxController xboxcontroller;
  
  /** Creates a new RunShoulder. */
  public RunShoulderPID(ShoulderPID _shoulder, CommandXboxController _xboxController) {
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(_shoulder);

    shoulder = _shoulder;
    xboxcontroller = _xboxController;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // TO/DO: Make it so that it doesn't go outside of starting perimeter
    if(shoulder.getClimb() == true){
      shoulder.runDirectly(-MathUtil.applyDeadband(xboxcontroller.getLeftY(),0.03));
    }else{
      shoulder.incrementPID(-MathUtil.applyDeadband(xboxcontroller.getLeftY(),0.03));
    }
    
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
