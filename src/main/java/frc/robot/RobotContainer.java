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
import frc.robot.commands.Autos;
import frc.robot.commands.ExampleCommand;
import frc.robot.commands.RunElevator;

import frc.robot.subsystems.ExampleSubsystem;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();
  //private final Intake m_intake = new Intake();
  private final Elevator m_elevator = new Elevator();
  
  private final Shoulder m_Shoulder = new Shoulder();
  


  // Replace with CommandPS4Controller or CommandJoystick if needed

  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);
  private final CommandXboxController m_manipulatorController = new CommandXboxController(1);


 private final RunElevator runElevator = new RunElevator(m_elevator, m_manipulatorController);
 private final RunShoulder runShoulder = new RunShoulder(m_Shoulder, m_manipulatorController);
  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the trigger bindings
    
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
    //m_elevator.setDefaultCommand(new RunElevator(m_elevator,m_manipulatorController));
    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
    new Trigger(m_exampleSubsystem::exampleCondition)
        .onTrue(new ExampleCommand(m_exampleSubsystem));;;;;;;;;;;;;;;

    // Schedule `exampleMethodCommand` when the Xbox controller's B button is pressed,
    // cancelling on release.
    

   // m_manipulatorController.rightBumper().whileTrue(new RunIntake(m_intake,true));
    //m_manipulatorController.leftBumper().whileTrue(new RunOuttake(m_intake));

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
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return Autos.exampleAuto(m_exampleSubsystem);
  }
  public void resetEncoders(){
    m_elevator.resetEncoders();
    m_elevator.setPID(0);
  }
}
