// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static final double elevatorGearRatio = 7.75;//62/8
  public static final double elevatorGearConversion=190/10.08;


  public final static double distToLimOffset = 4;

  public static final int intakeDeviceID1 = 12;
  public static final int intakeDeviceID2 = 13;
  public static final int elevatorDeviceID1 = 3;
  public static final int elevatorDeviceID2 = 1;
  public static final int wristMotorID1 = 5;

  public static final double shooterSpeed = 1;

  public static final double elevatorMaxHeight = -190; // highest point on elevator to lowest point on
  //elevator in cm

  //reef height in cm(rough, plz adjust)
  public static final double level4Height = -165;
  public static final double level3Height = -101;
  public static final double level2Height = -60;
  public static final double level1Height = -7;
  
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
  }
}
