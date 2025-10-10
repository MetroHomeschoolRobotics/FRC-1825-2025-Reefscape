// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.Constants;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class runDriveTrain extends Command {
  private CommandXboxController xboxController;
  private CommandSwerveDrivetrain drivetrain;
  private Double MaxSpeed = Constants.DrivetrainConstants.maxSpeed;
  private Double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); //Why is this not in our constants file?;

  private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric() 
    .withDeadband(MaxSpeed * 0.05).withRotationalDeadband(MaxAngularRate * 0.05) // Add a deadband, this will apply to all request to the drive object
    .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
  /** Creates a new runDriveTrain. */
  
  public runDriveTrain(CommandSwerveDrivetrain _drivetrain,CommandXboxController _xboxController) {
    addRequirements(_drivetrain);
    drivetrain = _drivetrain;
    xboxController = _xboxController;


    // Creates a drive object that will be used to create drive commands later on

  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    drivetrain.applyRequest(() -> drive.withVelocityX(-Math.pow(xboxController.getLeftY(), 3) * MaxSpeed)
        .withVelocityY(-Math.pow(xboxController.getLeftX(), 3) * MaxSpeed) // Drive left with negative X (left)
        .withRotationalRate(-Math.pow(xboxController.getRightX(), 3) * MaxAngularRate) // Drive counterclockwise with
    );
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
