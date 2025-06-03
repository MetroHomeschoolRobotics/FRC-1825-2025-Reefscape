// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

// import com.ctre.phoenix6.SignalLogger;
import com.pathplanner.lib.commands.PathfindingCommand;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
// import frc.robot.subsystems.Elevator;

public class Robot extends TimedRobot {
  private Command m_autonomousCommand;
  private final RobotContainer m_robotContainer;

  /*/ RS-232 port for MatrixPortal (9600 baud, 8 data bits, odd parity, 1 stop bit)
  public final SerialPort rs232Port = new SerialPort(
      9600,
      SerialPort.Port.kOnboard,
      8,
      SerialPort.Parity.kOdd,
      SerialPort.StopBits.kOne
  );
/*/
  public Robot() {
    // Instantiate RobotContainer (button-bindings, auto chooser)
    m_robotContainer = new RobotContainer();
    m_robotContainer.resetEncoders();

    // Warm up Pathfinding
    PathfindingCommand.warmupCommand().schedule();
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }

  @Override
  public void disabledInit() {}
  @Override
  public void disabledPeriodic() {}
  @Override
  public void disabledExit() {}

  @Override
  public void autonomousInit() {
    m_robotContainer.resetEncoders();
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }
  @Override
  public void autonomousPeriodic() {}
  @Override
  public void autonomousExit() {}

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  @Override
  public void teleopPeriodic() {
    // Transmit framed exactly as the Arduino expects (SERIAL_8O1)
    // rs232Port.writeString("Hello from Perry!\r\n");
  }
  @Override
  public void teleopExit() {}

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }
  @Override public void testPeriodic() {}
  @Override public void testExit() {}

  @Override public void simulationPeriodic() {}
}
