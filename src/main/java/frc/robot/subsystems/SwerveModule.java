// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.CANcoder;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.ClosedLoopConfig;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SwerveModule extends SubsystemBase {


  private SparkFlex driveMotor;
  private SparkMax turnMotor;
  private CANcoder absoluteEncoder;

  private ClosedLoopConfig drivePIDF;
  private ClosedLoopConfig turnPIDF;

  /** Creates a new SwerveModule. */
  public SwerveModule(int driveMotorID, int turnMotorID, int encoderID) {
    driveMotor = new SparkFlex(driveMotorID, MotorType.kBrushless);
    turnMotor = new SparkMax(turnMotorID, MotorType.kBrushless);
    absoluteEncoder = new CANcoder(encoderID);

    drivePIDF = new ClosedLoopConfig().pidf(encoderID, driveMotorID, turnMotorID, encoderID);
    turnPIDF = new ClosedLoopConfig().pidf(encoderID, driveMotorID, turnMotorID, encoderID);

    

  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
