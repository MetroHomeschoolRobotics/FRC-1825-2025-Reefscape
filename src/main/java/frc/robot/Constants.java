// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
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

  public static final double shooterSpeed = -0.5;


  public static class elevatorConstants {
    public static final double elevatorGearRatio = 7.75;//62/8
    public static final double elevatorConversion=(207-93.66)/20.16;//elevator max to floor - elevator min to floor / encoder val at max height
    public static final double elevatorMaxHeight = -208; // highest point on elevator to lowest point on elevator in cm
  }



  
  
  public static class fieldConstants {
    //reef height in cm(rough, plz adjust)
    public static final double level4Height = -205;// angle: 1.5
    public static final double level3Height = -146;//angle 2.9
    public static final double level2Height = -108;//angle 3.8
    public static final double level1Height = -93.66;

    public static final double level4Angle = 0.5;
    public static final double level3Angle = 2.9;
    public static final double level2Angle = 3.8;
    public static final double level1Angle = 1;

    public static final double UpperAlgaeAngle = 2;
    public static final double UpperAlgaeHeight = -125;


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

    public static final int deAlgaeMotorId = 14;
    public static final int climberMotorId = 15;

  }

  public static class FieldSetpoints {

    public static final AprilTagFieldLayout aprilTagFieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2025ReefscapeWelded);

    public static class BlueAlliance {
      public static final Pose2d reefA = new Pose2d(3.3004125, 4.273064708, new Rotation2d(Units.degreesToRadians(0)));
      public static final Pose2d reefB = new Pose2d(3.3004125, 3.94378975, new Rotation2d(Units.degreesToRadians(0)));

      public static final Pose2d reefC = new Pose2d(3.776900447, 3.11848787, new Rotation2d(Units.degreesToRadians(60)));
      public static final Pose2d reefD = new Pose2d(4.062060939, 2.953850404, new Rotation2d(Units.degreesToRadians(60)));
      
      public static final Pose2d reefE = new Pose2d(5.014992053, 2.953824014, new Rotation2d(Units.degreesToRadians(120)));
      public static final Pose2d reefF = new Pose2d(5.300194251, 3.118485584, new Rotation2d(Units.degreesToRadians(120)));
      
      public static final Pose2d reefG = new Pose2d(5.26868619, 3.943789217, new Rotation2d(Units.degreesToRadians(180)));
      public static final Pose2d reefH = new Pose2d(5.77668619, 4.273053328, new Rotation2d(Units.degreesToRadians(180)));
      
      public static final Pose2d reefI = new Pose2d(5.300198239, 5.098366054, new Rotation2d(Units.degreesToRadians(-120)));
      public static final Pose2d reefJ = new Pose2d(5.01507219, 5.262983657, new Rotation2d(Units.degreesToRadians(-120)));
      
      public static final Pose2d reefK = new Pose2d(4.06204268, 5.262992979, new Rotation2d(Units.degreesToRadians(-60)));
      public static final Pose2d reefL = new Pose2d(3.776892903, 5.098361685, new Rotation2d(Units.degreesToRadians(-60)));
    }
    
    public static class RedAlliance{
      public static final Pose2d reefA = new Pose2d(14.282, 3.867, new Rotation2d(Units.degreesToRadians(180)));
      public static final Pose2d reefB = new Pose2d(14.282, 4.203329004, new Rotation2d(Units.degreesToRadians(180))); 

      public static final Pose2d reefC = new Pose2d(13.76958, 4.946058, new Rotation2d(Units.degreesToRadians(-120))); 
      public static final Pose2d reefD = new Pose2d(13.480737, 5.1194531, new Rotation2d(Units.degreesToRadians(-120)));

      public static final Pose2d reefE = new Pose2d(12.607443, 5.0870176, new Rotation2d(Units.degreesToRadians(-60))); 
      public static final Pose2d reefF = new Pose2d(12.282781568, 4.9238677, new Rotation2d(Units.degreesToRadians(-60))); 

      public static final Pose2d reefG = new Pose2d(11.899349076, 4.1559300311, new Rotation2d(Units.degreesToRadians(0))); 
      public static final Pose2d reefH = new Pose2d(11.899349076, 3.821292, new Rotation2d(Units.degreesToRadians(0))); 

      public static final Pose2d reefI = new Pose2d(12.34645115, 3.11850227, new Rotation2d(Units.degreesToRadians(60))); 
      public static final Pose2d reefJ = new Pose2d(12.63163608, 2.953850684, new Rotation2d(Units.degreesToRadians(60)));

      public static final Pose2d reefK = new Pose2d(13.53648389, 2.94683796305, new Rotation2d(Units.degreesToRadians(120))); 
      public static final Pose2d reefL = new Pose2d(13.86976472, 3.118482587, new Rotation2d(Units.degreesToRadians(120))); 
    }
    
    public static final Pose2d[] rightReefBranches = {
      BlueAlliance.reefB, 
      BlueAlliance.reefD, 
      BlueAlliance.reefF, 
      BlueAlliance.reefH, 
      BlueAlliance.reefJ, 
      BlueAlliance.reefL,
      RedAlliance.reefB, 
      RedAlliance.reefD, 
      RedAlliance.reefF, 
      RedAlliance.reefH, 
      RedAlliance.reefJ, 
      RedAlliance.reefL
    };

    public static final Pose2d[] leftReefBranches = {
      BlueAlliance.reefA,
      BlueAlliance.reefC,
      BlueAlliance.reefE,
      BlueAlliance.reefG,
      BlueAlliance.reefI,
      BlueAlliance.reefK,
      RedAlliance.reefA,
      RedAlliance.reefC,
      RedAlliance.reefE,
      RedAlliance.reefG,
      RedAlliance.reefI,
      RedAlliance.reefK,
    };

  }
}