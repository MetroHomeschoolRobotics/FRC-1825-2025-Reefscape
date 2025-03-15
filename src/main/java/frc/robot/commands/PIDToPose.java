// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class PIDToPose extends Command {

  private CommandSwerveDrivetrain drivetrain;

  // drive constants
  private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
  private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity TODO check this

  private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.05).withRotationalDeadband(MaxAngularRate * 0.05); // Add a 10% deadband

  private Pose2d destinationPose;

  private PIDController xPID = new PIDController(1, 0, 0.07);
  private PIDController yPID = new PIDController(1, 0, 0.07);
  private PIDController thetaPID = new PIDController(0.03, 0, 0);

  /** Creates a new PIDToPose. */
  public PIDToPose(CommandSwerveDrivetrain _drivetrain, Pose2d _destinationPose) {
    drivetrain = _drivetrain;
    destinationPose = _destinationPose;
    addRequirements(_drivetrain);
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    thetaPID.enableContinuousInput(-180, 180);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {

    Pose2d currentPose = drivetrain.getRobotPose();

    double xPIDOutput = xPID.calculate(currentPose.getX(), destinationPose.getX());
    double yPIDOutput = yPID.calculate(currentPose.getY(), destinationPose.getY());
    double thetaPIDOutput = thetaPID.calculate(currentPose.getRotation().getDegrees(), destinationPose.getRotation().getDegrees());


    // System.out.println(xPIDOutput);
    drivetrain.applyRequest(() -> drive.withVelocityX(MathUtil.clamp(-xPIDOutput, -0.1, 0.1) * MaxSpeed) // Drive forward with negative Y (forward)
                                                .withVelocityY(MathUtil.clamp(-yPIDOutput, -0.1, 0.1) * MaxSpeed) // Drive left with negative X (left)
                                                .withRotationalRate(MathUtil.clamp(thetaPIDOutput, -0.1, 0.1) * MaxAngularRate) // Drive counterclockwise with negative X (left)
            ).execute();

  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    drivetrain.applyRequest(() -> drive.withVelocityX(0 * MaxSpeed) // Drive forward with negative Y (forward)
                                                .withVelocityY(0 * MaxSpeed) // Drive left with negative X (left)
                                                .withRotationalRate(0 * MaxAngularRate) // Drive counterclockwise with negative X (left)
            ).cancel();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return xPID.atSetpoint() && yPID.atSetpoint() && thetaPID.atSetpoint();
  }
}
