// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shoulder;
import frc.robot.subsystems.ShoulderPID;
import frc.robot.subsystems.Vision;
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
import frc.robot.commands.StaggerMotors;
import frc.robot.commands.TeleopToBranchPID;
import frc.robot.commands.ToggleActuatorSoftLimits;
import frc.robot.commands.UpperAlgaePreset;
import frc.robot.commands.l1AutoAlign;
import frc.robot.commands.l1timer;
import frc.robot.commands.scoreL1Backwards;
import frc.robot.commands.rundeAlgae;
import frc.robot.commands.shoulderToIntake;
import frc.robot.commands.stopclimber;
import frc.robot.commands.testClimberPID;
import frc.robot.commands.ClimberMotorBackwards;
import frc.robot.commands.DriveToBranch;
import frc.robot.commands.DriveToBranchPID;
import frc.robot.commands.LowerAlgaePreset;
import frc.robot.commands.DriveToSource;
import frc.robot.commands.PIDToPose;
import frc.robot.commands.RaiseElevator;
import frc.robot.commands.ResetElevatorEncoders;
import frc.robot.commands.RetractElevator;
import frc.robot.commands.RunClimb;
import frc.robot.commands.RunClimbPiston;
import frc.robot.commands.RunClimbPiston2;
import frc.robot.commands.RunClimbPistonBackwards;
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
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;

import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.ClimbPiston;
import frc.robot.subsystems.CommandSwerveDrivetrain;

public class RobotContainer {

  // public final Boolean developerMode = true; 
  // TODO finalize the programming and change this developer mode var

  // drive constants
  private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
  private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity
  //  TODO check this


  // The robot's subsystems and commands are defined here.

  // Setting up bindings for necessary control of the swerve drive platform
  // Creates a drive object that will be used to create drive commands later on
  private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric() 
    .withDeadband(MaxSpeed * 0.05).withRotationalDeadband(MaxAngularRate * 0.05) // Add a deadband, this will apply to all request to the drive object
    .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors

  // Create a brake command that can be used later
  private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();

  // private final SwerveRequest.PointWheelsAt point = new
  // SwerveRequest.PointWheelsAt(); // Cool idea, doesn't work yet

  public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();


  // Create all the subsystems for the code
  private final Intake m_intake = new Intake();
  private final Elevator m_elevator = new Elevator();
  private final deAlgae m_deAlgae = new deAlgae();
  private final ShoulderPID m_Shoulder = new ShoulderPID();
  private final climber m_climber = new climber();
  private final ClimbPiston m_piston = new ClimbPiston();
  private final Telemetry logger = new Telemetry(MaxSpeed);  // Pose Stuffs

  // Create the objects for the controllers
  // Replace with CommandPS4Controller or CommandJoystick if needed
  private final CommandXboxController driverXbox = new CommandXboxController(OperatorConstants.kDriverControllerPort);
  private final CommandXboxController m_manipulatorController = new CommandXboxController(1);
  private final CommandXboxController m_streamdeck = new CommandXboxController(2);

  // Command Init. These are commands that never end so that will always be running
  // Therefore they start at robot init, instead of being bound to a button
  private final RunElevator runElevator = new RunElevator(m_elevator, m_manipulatorController, m_Shoulder);
  private final RunShoulderPID runShoulder = new RunShoulderPID(m_Shoulder, m_manipulatorController, m_elevator);

  // Auto Chooser
  public final SendableChooser<Command> autoChooser = new SendableChooser<>();

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    // Configure the trigger bindings
    createAutoChooser();
    configureBindings();
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link CommandXboxController Xbox}
   * / {@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller PS4} controllers or
   * {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight joysticks}.
   */
  private void configureBindings() {
    // TODO Create an angle based turn system
    // Note that X is defined as forward according to WPILib convention,
    // and Y is defined as to the left according to WPILib convention.
    // Drivetrain will execute this command periodically
    drivetrain.setDefaultCommand(
      drivetrain.applyRequest(() -> drive.withVelocityX(-Math.pow(driverXbox.getLeftY(), 3) * MaxSpeed) // Drive forward with negative Y (forward)
        .withVelocityY(-Math.pow(driverXbox.getLeftX(), 3) * MaxSpeed) // Drive left with negative X (left)
        .withRotationalRate(-Math.pow(driverXbox.getRightX(), 3) * MaxAngularRate) // Drive counterclockwise with negative X (left)
      )
    );

      // Puts the wheels in an x
      // driverXbox.x().whileTrue(drivetrain.applyRequest(() -> brake));

      // points the wheels without driving
      // driverXbox.b().whileTrue(drivetrain.applyRequest(() ->
      // point.withModuleDirection(new Rotation2d(-driverXbox.getLeftY(),
      // -driverXbox.getLeftX()))
      // ));

      // driverXbox.y().whileTrue(new TeleopToBranchPID(drivetrain, "L"));

      // driverXbox.y().whileTrue(new PIDToPose(drivetrain,
      // Constants.FieldSetpoints.RedAlliance.reefA));
      // driverXbox.y().whileTrue(drivetrain.driveToPose(Constants.FieldSetpoints.RedAlliance.reefL,
      // 2, 2,180,360));

      // Sysid buttons
      // driverXbox.a().whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
      // driverXbox.b().whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
      // driverXbox.x().whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
      // driverXbox.y().whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));
      // driverXbox.leftBumper().onTrue(Commands.runOnce(logger::startSignalLogger));
      // driverXbox.rightBumper().onTrue(Commands.runOnce(logger::stopSignalLogger));


      // Puts the wheels in an x
      // driverXbox.x().whileTrue(drivetrain.applyRequest(() -> brake));

      // points the wheels without driving
      // driverXbox.b().whileTrue(drivetrain.applyRequest(() ->
      // point.withModuleDirection(new Rotation2d(-driverXbox.getLeftY(),
      // -driverXbox.getLeftX()))
      // ));


    //Sets the command that subsystems will run if nothing else is scheduled
    //These will be overridden when something else is scheduled in these subsystems
    CommandScheduler.getInstance().setDefaultCommand(m_Shoulder, runShoulder);
    CommandScheduler.getInstance().setDefaultCommand(m_elevator, runElevator);

    // reset the field-centric heading on left bumper press
    driverXbox.povUp().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));
    driverXbox.leftBumper().whileTrue(new DriveToBranch("L", drivetrain));
    driverXbox.rightBumper().whileTrue(new DriveToBranch("R", drivetrain));
    driverXbox.x().whileTrue(new DriveToSource(drivetrain));
    driverXbox.y().whileTrue(drivetrain.applyRequest(() -> brake));
    driverXbox.a().whileTrue(new l1AutoAlign(drivetrain));
    // driverXbox.y().whileTrue(new DriveToBranchPID(drivetrain, "L"));
    // Starting config
    // driverXbox.b().whileTrue(new SetShoulderAngle(m_Shoulder, -25));


    // Manipulator non-scoring commands
    m_manipulatorController.rightBumper().whileTrue(new ShiftCoralForward(m_intake));
    m_manipulatorController.leftBumper().whileTrue(new StaggerMotors(m_intake));
    m_manipulatorController.povUp().whileTrue(new rundeAlgae(m_deAlgae));
    m_manipulatorController.povRight().whileTrue(new UpperAlgaePreset(m_elevator, m_Shoulder));
    m_manipulatorController.povLeft().whileTrue(new LowerAlgaePreset(m_elevator, m_Shoulder));
    m_manipulatorController.rightTrigger().whileTrue(new RunIntakeBackwards(m_intake));
    m_manipulatorController.leftTrigger().whileTrue(new shoulderToIntake(m_Shoulder, m_elevator));

    // These are all the manipulator scoring commands, for each level
    m_manipulatorController.y().whileTrue(new Score(m_elevator, m_Shoulder, m_intake, 4)
        .andThen(new RunOuttake(m_intake)).andThen(new RetractElevator(m_elevator, m_Shoulder)));
    m_manipulatorController.x().whileTrue(new Score(m_elevator, m_Shoulder, m_intake, 3)
        .andThen(new RunOuttake(m_intake)).andThen(new RetractElevator(m_elevator, m_Shoulder)));
    m_manipulatorController.b().whileTrue(new Score(m_elevator, m_Shoulder, m_intake, 2)
        .andThen(new RunOuttake(m_intake)).andThen(new RetractElevator(m_elevator, m_Shoulder)));
    // m_manipulatorController.a().whileTrue(new Score(m_elevator,m_Shoulder,m_intake, 1).andThen(new RunIntakeBackwards(m_intake)));
    m_manipulatorController.a().whileTrue(new scoreL1Backwards(m_elevator, m_Shoulder, m_intake));



    // These are all the streamdeck button commands, only used for climbing
    m_streamdeck.b().whileTrue(new RunClimbPiston2(m_piston));
    m_streamdeck.a().whileTrue(new SetShoulderAngle(m_Shoulder, -34.6).andThen(new RaiseElevator(m_elevator, -115)));
    m_streamdeck.povUp().whileTrue(new RunClimbPistonBackwards(m_piston));// retract actuator, if this ratchets ignore
    m_streamdeck.povLeft().whileTrue(new testClimberPID(m_climber));// claws to cage
    m_streamdeck.povRight().whileTrue(new ClimberMotorBackwards(m_climber));// claws backwards, if this ratchets ignore
    m_streamdeck.povDown().whileTrue(new SetShoulderAngle(m_Shoulder, -54).andThen(new RunClimbPiston(m_piston))
        .andThen(new SetShoulderAngle(m_Shoulder, -47.5)).andThen(new RunClimbPiston2(m_piston)));// actuactor forward
    // m_streamdeck.povLeft().whileTrue(new testClimberPID(m_climber));
    // m_streamdeck.povDown().whileTrue(new RunClimbPiston(m_piston));
    // m_streamdeck.povRight().whileTrue(new RunClimb( m_Shoulder));


    Optional<Alliance> ally = DriverStation.getAlliance();

  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public void resetEncoders() {
    m_elevator.resetEncoders();
    m_climber.resetEncoders();
    // m_climber.setClimber(0);
    m_elevator.setPID(-93.66);
    m_Shoulder.setPID(m_Shoulder.getAbsoluteAngle());
  }

  private void createAutoChooser() {
    // Create the named commands
    NamedCommands.registerCommand("XWheels", drivetrain.applyRequest(() -> brake));
    NamedCommands.registerCommand("ShoulderAngleToL4", new SetShoulderAngle(m_Shoulder, -8));
    NamedCommands.registerCommand("AngleToL4", new SetShoulderAngle(m_Shoulder, Constants.fieldConstants.level4Angle));
    NamedCommands.registerCommand("ElevatorToL4", new Score(m_elevator, m_Shoulder, m_intake, 4));
    NamedCommands.registerCommand("Outtake", new RunOuttake(m_intake));
    NamedCommands.registerCommand("Intake", new StaggerMotors(m_intake));
    NamedCommands.registerCommand("RetractElevator", new RetractElevator(m_elevator, m_Shoulder));
    NamedCommands.registerCommand("ShoulderToLoadingAngle", new shoulderToIntake(m_Shoulder, m_elevator));
    NamedCommands.registerCommand("PathfindToF",
        drivetrain.driveToPose(new Pose2d(5.285, 3.030, new Rotation2d(120)), 2, 2, 180, 360));
    NamedCommands.registerCommand("PIDToBranchL", new DriveToBranchPID(drivetrain, "L"));
    NamedCommands.registerCommand("PIDToBranchR", new DriveToBranchPID(drivetrain, "R"));
    NamedCommands.registerCommand("justL4Elevator",
        new RaiseElevator(m_elevator, Constants.fieldConstants.level4Height));

    // Default is no auto
    autoChooser.setDefaultOption("No Auto", new WaitCommand(15));
    // autoChooser.addOption("Straight2Meter",
    // drivetrain.getAutonomousCommand("Straight2Meter"));
    // autoChooser.addOption("Straight4Meter",
    // drivetrain.getAutonomousCommand("Straight4Meter"));
    // autoChooser.addOption("Straight6Meter",
    // drivetrain.getAutonomousCommand("Straight6Meter"));
    autoChooser.addOption("LeftAuto", drivetrain.getAutonomousCommand("Left Auto2"));
    autoChooser.addOption("RightAuto", drivetrain.getAutonomousCommand("Right Auto"));
    // autoChooser.addOption("RightAutoSpeed",
    // drivetrain.getAutonomousCommand("Right Auto Speed"));
    // autoChooser.addOption("LeftAutoSpeed", drivetrain.getAutonomousCommand("Left
    // Auto Speed"));
    autoChooser.addOption("Middle Auto", drivetrain.getAutonomousCommand("ShortStraightFromMiddle"));
    // autoChooser.addOption("TestAuto",
    // drivetrain.getAutonomousCommand("RightAutoJustPath"));

    SmartDashboard.putData("Auto Chooser", autoChooser);
  }

  public Command getAutonomousCommand() {

    return autoChooser.getSelected();
  }
}