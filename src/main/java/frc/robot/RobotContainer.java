// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shoulder;
import frc.robot.subsystems.ShoulderPID;
import frc.robot.subsystems.climber;
import frc.robot.subsystems.deAlgae;
import frc.robot.subsystems.Elevator;
import frc.robot.commands.RunIntake;
import frc.robot.commands.RunIntakeBackwards;
import frc.robot.commands.RunOuttake;
import frc.robot.commands.RunOuttakeSideways;
import frc.robot.commands.RunShoulder;
import frc.robot.commands.RunShoulderPID;
import frc.robot.commands.Score;
import frc.robot.commands.SetShoulderAngle;
import frc.robot.commands.ShiftCoralForward;
import frc.robot.commands.UpperAlgaePreset;
import frc.robot.commands.RunDeAlgae;
import frc.robot.commands.shoulderToIntake;
import frc.robot.commands.testClimberPID;
import frc.robot.commands.DriveToBranch;
import frc.robot.commands.ResetElevatorEncoders;
import frc.robot.commands.RetractElevator;
import frc.robot.commands.RunClimb;
import frc.robot.Constants.OperatorConstants;

import frc.robot.commands.RunElevator;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

import static edu.wpi.first.units.Units.*;

import java.util.Optional;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.pathplanner.lib.auto.NamedCommands;
import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;

import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;

public class RobotContainer {

  public final Boolean developerMode = true; // TODO finalize the programming and change this developer mode var

  // drive constants
  private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
  private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity TODO check this

  /* Setting up bindings for necessary control of the swerve drive platform */
  private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.05).withRotationalDeadband(MaxAngularRate * 0.05) // Add a 10% deadband

            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
  private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
  private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();


  // The robot's subsystems and commands are defined here...
  
  private final Intake m_intake = new Intake();
  private final Elevator m_elevator = new Elevator();
  private final deAlgae m_deAlgae = new deAlgae();
  private final ShoulderPID m_Shoulder = new ShoulderPID();
  private final climber m_climber = new climber();


  // Replace with CommandPS4Controller or CommandJoystick if needed
  private final CommandXboxController driverXbox = new CommandXboxController(OperatorConstants.kDriverControllerPort);
  private final CommandXboxController m_manipulatorController = new CommandXboxController(1);
  private final CommandXboxController m_streamdeck = new CommandXboxController(2);
  
  // Command Init.
  private final RunElevator runElevator = new RunElevator(m_elevator, m_manipulatorController);
  private final RunShoulderPID runShoulder = new RunShoulderPID(m_Shoulder, m_manipulatorController);
  
  // Pose Stuffs
  private final Telemetry logger = new Telemetry(MaxSpeed);

  // Auto Chooser
  public final SendableChooser<Command> autoChooser = new SendableChooser<>();

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the trigger bindings
    createAutoChooser();
    configureBindings();
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
    if(developerMode){
            
        // Note that X is defined as forward according to WPILib convention, TODO Create an angle based turn system
        // and Y is defined as to the left according to WPILib convention.
        drivetrain.setDefaultCommand(
            // Drivetrain will execute this command periodically
            drivetrain.applyRequest(() -> drive.withVelocityX(-Math.pow(driverXbox.getLeftY(), 3) * MaxSpeed) // Drive forward with negative Y (forward)
                                                .withVelocityY(-Math.pow(driverXbox.getLeftX(), 3) * MaxSpeed) // Drive left with negative X (left)
                                                .withRotationalRate(-Math.pow(driverXbox.getRightX(), 3) * MaxAngularRate) // Drive counterclockwise with negative X (left)
            )
        );

        // Puts the wheels in an x
        driverXbox.x().whileTrue(drivetrain.applyRequest(() -> brake));

        // points the wheels without driving
        // driverXbox.b().whileTrue(drivetrain.applyRequest(() ->
        //     point.withModuleDirection(new Rotation2d(-driverXbox.getLeftY(), -driverXbox.getLeftX()))
        // ));

        // reset the field-centric heading on left bumper press
        driverXbox.b().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));

        //driverXbox.y().whileTrue(drivetrain.driveToPose(Constants.FieldSetpoints.RedAlliance.reefL, 2, 2,180,360));

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
        
    CommandScheduler.getInstance().setDefaultCommand(m_Shoulder, runShoulder);
    CommandScheduler.getInstance().setDefaultCommand(m_elevator,runElevator);

    driverXbox.povLeft().whileTrue(new DriveToBranch("L", drivetrain));
    driverXbox.povRight().whileTrue(new DriveToBranch("R", drivetrain));
    

    m_manipulatorController.rightBumper().whileTrue(new ShiftCoralForward(m_intake));
    m_manipulatorController.leftBumper().whileTrue(new RunIntake(m_intake));

    m_manipulatorController.y().whileTrue(new Score(m_elevator,m_Shoulder,m_intake, 4).andThen(new RunOuttake(m_intake)).andThen(new RetractElevator(m_elevator, m_Shoulder)));
    m_manipulatorController.x().whileTrue(new Score(m_elevator,m_Shoulder,m_intake, 3).andThen(new RunOuttake(m_intake)).andThen(new RetractElevator(m_elevator, m_Shoulder)));
    m_manipulatorController.b().whileTrue(new Score(m_elevator,m_Shoulder,m_intake, 2).andThen(new RunOuttake(m_intake)).andThen(new RetractElevator(m_elevator, m_Shoulder)));
    m_manipulatorController.a().whileTrue(new Score(m_elevator,m_Shoulder,m_intake, 1).andThen(new RunOuttakeSideways(m_intake)));

    m_manipulatorController.povUp().whileTrue(new RunDeAlgae(m_deAlgae));

    m_manipulatorController.povDown().whileTrue(new RunClimb(m_climber, m_Shoulder));
    m_manipulatorController.povLeft().whileTrue(new testClimberPID(m_climber));
    
    m_manipulatorController.povRight().whileTrue(new UpperAlgaePreset(m_elevator, m_Shoulder));
    m_manipulatorController.rightTrigger().whileTrue(new RunIntakeBackwards(m_intake));
    m_manipulatorController.leftTrigger().whileTrue(new shoulderToIntake(m_Shoulder,m_elevator));//.andThen(new RunIntake(m_intake)));
    
    
    
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public void resetEncoders(){
    m_elevator.resetEncoders();
    m_climber.resetEncoders();
    m_climber.openClimber();
    m_elevator.setPID(-93.66);
    m_Shoulder.setPID(m_Shoulder.getAbsoluteAngle());
  }

    private void createAutoChooser() {
        // Create the named commands
        NamedCommands.registerCommand("ShoulderAngleToL4", new SetShoulderAngle(m_Shoulder, 1));
        NamedCommands.registerCommand("ElevatorToL4", new Score(m_elevator, m_Shoulder, m_intake, 4));
        NamedCommands.registerCommand("Outtake", new RunOuttake(m_intake));
        NamedCommands.registerCommand("Intake", new RunIntake(m_intake));
        NamedCommands.registerCommand("RetractElevator", new RetractElevator(m_elevator, m_Shoulder));
        NamedCommands.registerCommand("ShoulderToLoadingAngle", new shoulderToIntake(m_Shoulder, m_elevator));
        NamedCommands.registerCommand("PathfindToF", drivetrain.driveToPose(new Pose2d(5.285, 3.030, new Rotation2d(120)), 2, 2, 180, 360));
        
        // Default is no auto
        autoChooser.setDefaultOption("No Auto", new WaitCommand(15));
        autoChooser.addOption("Straight2Meter", drivetrain.getAutonomousCommand("Straight2Meter"));
        autoChooser.addOption("Straight4Meter", drivetrain.getAutonomousCommand("Straight4Meter"));
        autoChooser.addOption("Straight6Meter", drivetrain.getAutonomousCommand("Straight6Meter"));
        autoChooser.addOption("LeftAuto", drivetrain.getAutonomousCommand("Left Auto"));
        autoChooser.addOption("RightAuto", drivetrain.getAutonomousCommand("Right Auto"));
        autoChooser.addOption("RightAutoJustDriving", drivetrain.getAutonomousCommand("RightAutoDriving"));
        autoChooser.addOption("Right To D Test", drivetrain.getAutonomousCommand("RightToD"));

        SmartDashboard.putData("Auto Chooser", autoChooser);
    }

    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
    }
}
