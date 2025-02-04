// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Shoulder extends SubsystemBase {

  private SparkMax wristMotor1 = new SparkMax(Constants.wristMotorID1, MotorType.kBrushless);
  private SparkMax wristMotor2 = new SparkMax(Constants.wristMotorID2, MotorType.kBrushless);
  private DutyCycleEncoder rotationEncoder = new DutyCycleEncoder(3);

  /** Creates a new Shoulder. */
  public Shoulder() {}

  public void setSpeed(double speed) {
    
    if (rotationEncoder.get() >= -8 && rotationEncoder.get() <= 45) {
      wristMotor1.set(speed);
      wristMotor2.set(speed);
    }
  }

  public double getAbsoluteAngle() {
    return rotationEncoder.get()*(360/1)-215.1;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
