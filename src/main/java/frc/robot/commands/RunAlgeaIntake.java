// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.AlgeaIntake;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class RunAlgeaIntake extends Command {

  private AlgeaIntake algeaIntake;
  private Boolean reversed;

  /** Creates a new RunAlgeaIntake. */
  public RunAlgeaIntake(AlgeaIntake _algeaIntake, Boolean _reversed) {
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(_algeaIntake);
    
    algeaIntake = _algeaIntake;
    reversed = _reversed;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if(reversed){
      algeaIntake.setSpeed(-1);
  }
}
  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    algeaIntake.setSpeed(0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
