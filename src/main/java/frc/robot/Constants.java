// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.util.Units;


/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static final int cancoderID = 0;

  public static final double elevatorGearRatio = 7.75;
  public static final double elevatorConversion=(207-93.66)/20.16;//elevator max to floor - elevator min to floor / encoder val at max height


  public final static double distToLimOffset = 4;

  public static final int intakeDeviceID1 = 12;
  public static final int intakeDeviceID2 = 13;
  public static final int elevatorDeviceID1 = 10;
  public static final int elevatorDeviceID2 = 9;
  public static final int wristMotorID1 = 7;
  public static final int wristMotorID2 = 11;


  public static final double shooterSpeed = -0.5;

  public static final double elevatorMaxHeight = -208; // highest point on elevator to lowest point on
  //elevator in cm

  //reef height in cm(rough, plz adjust)
  public static final double level4Height = -205;// angle: 1.5
  public static final double level3Height = -146;//angle 2.9
  public static final double level2Height = -104;//angle 3.8
  public static final double level1Height = -93.66;

  public static final double level4Angle = 1;
  public static final double level3Angle = 2.9;
  public static final double level2Angle = 3.8;
  
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
    public static final double joystickDeadband = 0.05;
  }
  public static class DrivetrainConstants {
    public static final double maxSpeed = 5.5;
  }


    
    public static class CameraPositions {
    public static final Transform3d frontLeftTranslation = new Transform3d(
                                                            Units.inchesToMeters(11.147131),
                                                            Units.inchesToMeters(11.404959),
                                                            Units.inchesToMeters(9.321819), 
                                                            new Rotation3d(
                                                              Units.degreesToRadians(0),
                                                              Units.degreesToRadians(10),
                                                              Units.degreesToRadians(-30)));
    public static final Transform3d backTranslation = new Transform3d(0,0,0, new Rotation3d(0,0,0));
    public static final Transform3d leftTranslation = new Transform3d(0,0,0, new Rotation3d(0,0,0));
    public static final Transform3d rightTranslation = new Transform3d(0,0,0, new Rotation3d(0,0,0));
  }
    public static class MotorIDs {}
}