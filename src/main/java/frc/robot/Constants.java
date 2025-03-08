// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
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

  public final static double distToLimOffset = 4;

  public static final double shooterSpeed = 1;

  public static class elevatorConstants {
    public static final double elevatorGearRatio = 7.75;//62/8
    public static final double elevatorConversion=(207-93.66)/20.16;//elevator max to floor - elevator min to floor / encoder val at max height
    public static final double elevatorMaxHeight = -208; // highest point on elevator to lowest point on elevator in cm
  }
  
  
  public static class fieldConstants {
    //reef height in cm(rough, plz adjust)
    public static final double level4Height = -205;// angle: 1.5
    public static final double level3Height = -146;//angle 2.9
    public static final double level2Height = -104;//angle 3.8
    public static final double level1Height = -93.66;

    public static final double level4Angle = 1;
    public static final double level3Angle = 2.9;
    public static final double level2Angle = 3.8;
  }
  
  
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
  
  public static class MotorIDs {
    public static final int cancoderID = 0; // shoulder cancoder
  
    public static final int intakeDeviceID1 = 12;
    public static final int intakeDeviceID2 = 13;

    public static final int elevatorDeviceID1 = 10;
    public static final int elevatorDeviceID2 = 9;

    public static final int wristMotorID1 = 7;
    public static final int wristMotorID2 = 11;

  }

  public static class FieldSetpoints {
    public static class BlueAlliance {
      public static final Pose2d reefJ = new Pose2d(4.992, 5.253, new Rotation2d(Units.degreesToRadians(-118)));
      public static final Pose2d reefI = new Pose2d(5.353, 5.019, new Rotation2d(Units.degreesToRadians(120)));
      public static final Pose2d reefK = new Pose2d(3.978, 5.224, new Rotation2d(Units.degreesToRadians(-118)));
      public static final Pose2d reefL = new Pose2d(3.715, 5.039, new Rotation2d(Units.degreesToRadians(120)));
    }
    public static class RedAlliance{
      public static final Pose2d reefA = new Pose2d(14.282, 3.867, new Rotation2d(Units.degreesToRadians(180)));
      public static final Pose2d reefB = new Pose2d(14.282, 3.867, new Rotation2d(Units.degreesToRadians(180)));

      public static final Pose2d reefC = new Pose2d(13.86978199, 5.098366054, new Rotation2d(Units.degreesToRadians(-120)));
      public static final Pose2d reefD = new Pose2d(13.58461205, 5.263004333, new Rotation2d(Units.degreesToRadians(-120)));

      public static final Pose2d reefE = new Pose2d(12.63166582, 5.263020411, new Rotation2d(Units.degreesToRadians(-60)));
      public static final Pose2d reefF = new Pose2d(12.34647655, 5.098366308, new Rotation2d(Units.degreesToRadians(-60)));

      public static final Pose2d reefG = new Pose2d(11.86998815, 4.273064708, new Rotation2d(Units.degreesToRadians(0)));
      public static final Pose2d reefH = new Pose2d(11.86998815, 3.943904507, new Rotation2d(Units.degreesToRadians(0)));

      public static final Pose2d reefI = new Pose2d(12.34645115, 3.11850227, new Rotation2d(Units.degreesToRadians(60)));
      public static final Pose2d reefJ = new Pose2d(12.63163608, 2.953850684, new Rotation2d(Units.degreesToRadians(60)));

      public static final Pose2d reefK = new Pose2d(13.58461296, 2.953850125, new Rotation2d(Units.degreesToRadians(120)));
      public static final Pose2d reefL = new Pose2d(13.86976472, 3.118482587, new Rotation2d(Units.degreesToRadians(120)));


    }
    
  }
}