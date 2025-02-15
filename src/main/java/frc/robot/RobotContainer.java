// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.pathplanner.lib.auto.NamedCommands;
import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;

import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;

public class RobotContainer {
    private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
    private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity TODO check this

    /* Setting up bindings for necessary control of the swerve drive platform */
    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.1).withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

    private final Telemetry logger = new Telemetry(MaxSpeed);

    private final CommandXboxController driverXbox = new CommandXboxController(0);

    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

    public final SendableChooser<Command> autoChooser = new SendableChooser<>();

    public final Boolean developerMode = true; // TODO finalize the programming and change this developer mode var

    public RobotContainer() {
        createAutoChooser();
        configureBindings();
    }

    private void configureBindings() {

        
        if(developerMode){
            
            // Note that X is defined as forward according to WPILib convention, TODO Create an angle based turn system
            // and Y is defined as to the left according to WPILib convention.
            drivetrain.setDefaultCommand(
                // Drivetrain will execute this command periodically
                drivetrain.applyRequest(() -> drive.withVelocityX(-driverXbox.getLeftY() * MaxSpeed) // Drive forward with negative Y (forward)
                                                    .withVelocityY(-driverXbox.getLeftX() * MaxSpeed) // Drive left with negative X (left)
                                                    .withRotationalRate(-driverXbox.getRightX() * MaxAngularRate) // Drive counterclockwise with negative X (left)
                )
            );

            // Puts the wheels in an x
            driverXbox.a().whileTrue(drivetrain.applyRequest(() -> brake));

            // points the wheels without driving
            driverXbox.b().whileTrue(drivetrain.applyRequest(() ->
                point.withModuleDirection(new Rotation2d(-driverXbox.getLeftY(), -driverXbox.getLeftX()))
            ));

            // reset the field-centric heading on left bumper press
            driverXbox.leftBumper().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));

            // Sysid buttons
            // driverXbox.a().whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
            // driverXbox.b().whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
            // driverXbox.x().whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
            // driverXbox.y().whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));

            // driverXbox.leftBumper().onTrue(Commands.runOnce(logger::startSignalLogger));
            // driverXbox.rightBumper().onTrue(Commands.runOnce(logger::stopSignalLogger));

        } else {
            // Note that X is defined as forward according to WPILib convention, TODO Create an angle based turn system
            // and Y is defined as to the left according to WPILib convention.
            drivetrain.setDefaultCommand(
                // Drivetrain will execute this command periodically
                drivetrain.applyRequest(() -> drive.withVelocityX(-driverXbox.getLeftY() * MaxSpeed) // Drive forward with negative Y (forward)
                                                    .withVelocityY(-driverXbox.getLeftX() * MaxSpeed) // Drive left with negative X (left)
                                                    .withRotationalRate(-driverXbox.getRightX() * MaxAngularRate) // Drive counterclockwise with negative X (left)
                )
            );

            // Puts the wheels in an x
            driverXbox.a().whileTrue(drivetrain.applyRequest(() -> brake));

            // reset the field-centric heading on left bumper press
            driverXbox.leftBumper().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));
        }
        

        drivetrain.registerTelemetry(logger::telemeterize);
    }

    private void createAutoChooser() {
        // Create the named commands
        //NamedCommands.registerCommand("CommandNameHere", RandomCommandFunction());
        
        // Default is no auto
        autoChooser.setDefaultOption("No Auto", new WaitCommand(15));
        autoChooser.addOption("straight2Meter", drivetrain.getAutonomousCommand("Straight2Meter"));
        autoChooser.addOption("Straight4Meter", drivetrain.getAutonomousCommand("Straight4Meter"));
        autoChooser.addOption("Straight6Meter", drivetrain.getAutonomousCommand("Straight6Meter"));

        SmartDashboard.putData("Auto Chooser", autoChooser);
    }

    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
    }
}
