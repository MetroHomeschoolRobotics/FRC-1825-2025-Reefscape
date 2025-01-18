// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.Autos;
import frc.robot.commands.ExampleCommand;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.SwerveSubsystem;
import swervelib.SwerveInputStream;

import java.io.File;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {

  public boolean developerMode = true;

  // The robot's subsystems and commands are defined here...
  private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();

  // Replace with CommandPS4Controller or CommandJoystick if needed
  private final CommandXboxController driveXbox =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);

  // Swerve subsystem
  private final SwerveSubsystem swerveSubsystem = new SwerveSubsystem(new File(Filesystem.getDeployDirectory(), "swerve"));



  /*
   * Configure smart dashboard inputs
   */

  private SendableChooser<Command> developerModeChooser = new SendableChooser<>();

  /*
   * Configure the driving types
   */
  private SwerveInputStream driveAngularVel = SwerveInputStream.of(swerveSubsystem.getSwerveDrive(),
                                                                    ()->driveXbox.getLeftX() * -1, 
                                                                    ()->driveXbox.getLeftY() * -1)
                                                                    .withControllerRotationAxis(driveXbox::getRightX)
                                                                    .deadband(Constants.OperatorConstants.joystickDeadband)
                                                                    .scaleTranslation(0.8)
                                                                    .allianceRelativeControl(true);

  private SwerveInputStream driveDirectAngle = driveAngularVel.copy()
                                                      .withControllerHeadingAxis(driveXbox::getRightX, driveXbox::getRightY)
                                                      .headingWhile(true);


  private SwerveInputStream driveAngularVelocitySim = SwerveInputStream.of(swerveSubsystem.getSwerveDrive(),
                                                      () -> -driveXbox.getLeftY(),
                                                      () -> -driveXbox.getLeftX())
                                                  .withControllerRotationAxis(() -> driveXbox.getRawAxis(2))
                                                  .deadband(Constants.OperatorConstants.joystickDeadband)
                                                  .scaleTranslation(0.8)
                                                  .allianceRelativeControl(true);

  private SwerveInputStream driveDirectAngleSim = driveAngularVelocitySim.copy()
                                                                         .withControllerHeadingAxis(() -> Math.sin(
                                                                                                    driveXbox.getRawAxis(
                                                                                                        2) * Math.PI) * (Math.PI * 2),
                                                                                                () -> Math.cos(
                                                                                                    driveXbox.getRawAxis(
                                                                                                        2) * Math.PI) *
                                                                                                      (Math.PI * 2))
                                                                        .headingWhile(true);



  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the trigger bindings
    configureBindings();
    setDeveloperMode();

    DriverStation.silenceJoystickConnectionWarning(true);
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
   * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {
    Command driveFieldOrientedDirectAngle         = swerveSubsystem.driveFieldOriented(driveDirectAngle);
    Command driveFieldOrientedAnglularVelocity    = swerveSubsystem.driveCommand(() -> MathUtil.applyDeadband(-driveXbox.getLeftY(), OperatorConstants.joystickDeadband), 
                                                                                 () -> MathUtil.applyDeadband(-driveXbox.getLeftX(), OperatorConstants.joystickDeadband), 
                                                                                 () -> MathUtil.applyDeadband(-driveXbox.getRightX(),OperatorConstants.joystickDeadband));//swerveSubsystem.driveFieldOriented(driveAngularVel);
    Command driveSetpointGen                      = swerveSubsystem.driveWithSetpointGeneratorFieldRelative(driveDirectAngle);
    Command driveFieldOrientedDirectAngleSim      = swerveSubsystem.driveFieldOriented(driveDirectAngleSim);
    Command driveFieldOrientedAnglularVelocitySim = swerveSubsystem.driveFieldOriented(driveAngularVelocitySim);
    Command driveSetpointGenSim = swerveSubsystem.driveWithSetpointGeneratorFieldRelative(driveDirectAngleSim);

    if(developerMode) {
      swerveSubsystem.setDefaultCommand(driveFieldOrientedAnglularVelocity);
    } else {
      swerveSubsystem.setDefaultCommand(driveFieldOrientedDirectAngle);
    }


    driveXbox.y().onTrue(Commands.runOnce(swerveSubsystem::zeroGyro));
    // Schedule `exampleMethodCommand` when the Xbox controller's B button is pressed,
    // cancelling on release.
  }

  private void setDeveloperModeTrue() {
    developerMode = true;
  }
  private void setDeveloperModeFalse() {
    developerMode = false;
  }
  private void setDeveloperMode() {
    developerModeChooser.addOption("Developer Mode", Commands.runOnce(this::setDeveloperModeTrue));
    developerModeChooser.addOption("Driver Mode", Commands.runOnce(this:: setDeveloperModeFalse));

    SmartDashboard.putData(developerModeChooser);
  }



  private void configureAutos() {

  }




  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return Autos.exampleAuto(m_exampleSubsystem);
  }
}
