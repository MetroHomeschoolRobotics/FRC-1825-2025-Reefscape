package frc.robot.commands;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.ShoulderPID;

public class RunShoulderPID extends Command {
    private ShoulderPID shoulder;
  private CommandXboxController xboxcontroller;
  private Elevator elevator;
  
  /** Creates a new RunShoulder. */
  public RunShoulderPID(ShoulderPID _shoulder, CommandXboxController _xboxController,Elevator _elevator) {
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(_shoulder);

    shoulder = _shoulder;
    xboxcontroller = _xboxController;
    elevator = _elevator;
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
      if((Math.cos(Math.toRadians(-shoulder.getAbsoluteAngle()-90))*elevator.getDistance()-16.58<70*2.54)
        && (Math.cos(Math.toRadians(shoulder.getAbsoluteAngle()-90))*elevator.getDistance()-49.23<70*2.54)){
          shoulder.incrementPID(-MathUtil.applyDeadband(xboxcontroller.getLeftY(),0.1));
            
        }else{
            
            if(xboxcontroller.getLeftY()<0){
                shoulder.incrementPID(-MathUtil.applyDeadband(xboxcontroller.getLeftY(),0.03));
            }
            // if(angle.getAbsoluteAngle()>0){
            //     elevator.setPID(-(16.58+18*2.54)/Math.toRadians(angle.getAbsoluteAngle()-90));
            // }else{
            //     elevator.setPID((49.23+18*2.54)/Math.toRadians(angle.getAbsoluteAngle()-90));
            // }
            
        }
        
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
