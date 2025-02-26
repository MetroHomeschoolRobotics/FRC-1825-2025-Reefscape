package frc.robot.subsystems;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Shoulder extends SubsystemBase {

  private SparkMax wristMotor1 = new SparkMax(Constants.wristMotorID1, MotorType.kBrushless);
  
  private DutyCycleEncoder rotationEncoder = new DutyCycleEncoder(3);

  /** Creates a new Shoulder. */
  public Shoulder() {}

  public void setSpeed(double speed) {
    wristMotor1.getEncoder().getPosition();
    if (rotationEncoder.get() >= -8 && rotationEncoder.get() <= 45) {
      wristMotor1.set(speed);
     
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