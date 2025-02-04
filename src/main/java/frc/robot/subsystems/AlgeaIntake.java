// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

/*
 * This is based on the design from the following link:
 * https://www.youtube.com/watch?v=AV82mcNgAT8
 * 
 * The wheels at the top need to move to intake the algea.
 * There needs to be rotation. The rotation axis will be at the bottom of the robot, so that the intake drops onto the algea, picking up the algea.
 * 
 * The rotation motor will start somewhere around 45°
 * To pickup the algea, the angle will need to go to somewhere around 90° (probably less, but should only be activated when over the algea on the ground (presets))
 * Removed algea falls directly onto intake????
 */

package frc.robot.subsystems;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class AlgeaIntake extends SubsystemBase {

  private SparkMax algeaIntakeMotor = new SparkMax(Constants.algeaIntakeID, MotorType.kBrushless);

  /** Creates a new AlgeaIntake. */
  public AlgeaIntake() {

  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void setSpeed(double speed) {
    algeaIntakeMotor.set(speed);
  }

  public double getDistance() {
    return algeaIntakeMotor.getEncoder().getPosition();
  }

  public double getSpeed() {
    return algeaIntakeMotor.getEncoder().getVelocity();
  }
}
