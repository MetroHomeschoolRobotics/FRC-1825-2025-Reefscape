// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shoulder;

import frc.robot.subsystems.Elevator;
import frc.robot.commands.RunIntake;
import frc.robot.commands.RunOuttake;
import frc.robot.commands.RunShoulder;
import frc.robot.commands.Score;

import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.RunElevator;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.pathplanner.lib.auto.NamedCommands;
import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
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

    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

    public final SendableChooser<Command> autoChooser = new SendableChooser<>();

    public final Boolean developerMode = true; // TODO finalize the programming and change this developer mode var


  // The robot's subsystems and commands are defined here...
  
  private final Intake m_intake = new Intake();
  private final Elevator m_elevator = new Elevator();
  
  private final Shoulder m_Shoulder = new Shoulder();
  


  // Replace with CommandPS4Controller or CommandJoystick if needed

  private final CommandXboxController driverXbox =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);
  private final CommandXboxController m_manipulatorController = new CommandXboxController(1);


 private final RunElevator runElevator = new RunElevator(m_elevator, m_manipulatorController);
 private final RunShoulder runShoulder = new RunShoulder(m_Shoulder, m_manipulatorController);
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

            // // Puts the wheels in an x
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


    //m_elevator.setDefaultCommand(new RunElevator(m_elevator,m_manipulatorController));


    // Schedule `exampleMethodCommand` when the Xbox controller's B button is pressed,
    // cancelling on release.
    

    m_manipulatorController.rightBumper().whileTrue(new RunIntake(m_intake,true));
    m_manipulatorController.leftBumper().whileTrue(new RunIntake(m_intake,false));

    // m_manipulatorController.y().whileTrue(new Score(m_elevator, m_intake, 4));
    // m_manipulatorController.x().whileTrue(new Score(m_elevator, m_intake, 3));
    // m_manipulatorController.b().whileTrue(new Score(m_elevator, m_intake, 2));
    // m_manipulatorController.a().whileTrue(new Score(m_elevator, m_intake, 1));

   
    
    CommandScheduler.getInstance().setDefaultCommand(m_Shoulder, runShoulder);
    CommandScheduler.getInstance().setDefaultCommand(m_elevator,runElevator);
    
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public void resetEncoders(){
    m_elevator.resetEncoders();
    m_elevator.setPID(-71);
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
