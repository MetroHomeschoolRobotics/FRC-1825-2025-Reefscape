// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;


import static edu.wpi.first.units.Units.Amps;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.mechanisms.swerve.LegacySwerveModule.ClosedLoopOutputType;
import com.ctre.phoenix6.mechanisms.swerve.LegacySwerveModuleConstants.SteerFeedbackType;
import com.ctre.phoenix6.swerve.SwerveModuleConstants.DriveMotorArrangement;
import com.ctre.phoenix6.swerve.SwerveModuleConstants.SteerMotorArrangement;

import edu.wpi.first.units.measure.Current;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SwerveModule extends SubsystemBase {

  private static final ClosedLoopOutputType driveClosedLoopOutput = ClosedLoopOutputType.Voltage;
  private static final ClosedLoopOutputType turnClosedLoopOutput = ClosedLoopOutputType.Voltage;

  private static final DriveMotorArrangement driveMotorType = DriveMotorArrangement.TalonFX_Integrated;
  private static final SteerMotorArrangement turnMotorType = SteerMotorArrangement.TalonFX_Integrated;

  // TODO find the correct Current Limit
  public static final TalonFXConfiguration driveInitialConfig = new TalonFXConfiguration();
  public static final TalonFXConfiguration turnInitialConfig = new TalonFXConfiguration();
                                                      // .withCurrentLimits(
                                                      //   new CurrentLimitsConfigs()
                                                      //   .withStatorCurrentLimit(60)
                                                      //   .withStatorCurrentLimitEnable(true)
                                                      // );

  private static final SteerFeedbackType turnEncoderType = SteerFeedbackType.RemoteCANcoder;
  private static final CANcoderConfiguration encoderInitialConfig = new CANcoderConfiguration();
  

  /** Creates a new SwerveModule. */
  public SwerveModule(int driveMotorID, int turnMotorID, int encoderID) {}

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
