// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CommandSwerveDrivetrain;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class DriveToSource extends Command {



  private Pose2d closestSource;
  private Pose2d[] sources;

  private CommandSwerveDrivetrain drivetrain;

  /** Creates a new DriveToSource. */
  public DriveToSource(CommandSwerveDrivetrain _drivetrain) {

    drivetrain = _drivetrain;

    addRequirements(_drivetrain);
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {

    // find the closest Source Position
      for(Pose2d positions : sources) {
        double apriltag1Dist = drivetrain.distanceToPose(closestSource);
        double apriltag2Dist = drivetrain.distanceToPose(positions);

        if(apriltag1Dist > apriltag2Dist) {
          closestSource = positions;
        }
      }




  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
