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

  public static final double shooterSpeed = -0.7;


  public static class elevatorConstants {
    public static final double elevatorGearRatio = 7.75;// 62/8
    public static final double elevatorConversion=(207-93.66)/20.16;//elevator max to floor - elevator min to floor / encoder val at max height
    public static final double elevatorMaxHeight = -208; // highest point on elevator to lowest point on elevator in cm
  }



  
  
  public static class fieldConstants {
    //reef height in cm(rough, plz adjust)
    public static final double level4Height = -207.5;// angle: 1.5
    public static final double level3Height = -146;//angle 2.9
    public static final double level2Height = -108;//angle 3.8
    public static final double level1Height = -93.66;

    public static final double level4Angle = 0.8;
    public static final double level3Angle = 2.9;
    public static final double level2Angle = 3.8;
    public static final double level1Angle = -53.5;

    public static final double UpperAlgaeAngle = 2; 
    public static final double UpperAlgaeHeight = -139;


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
        Units.inchesToMeters(11.404959), // 11.29
        Units.inchesToMeters(9.321819),
        new Rotation3d(
            Units.degreesToRadians(5.592),
            Units.degreesToRadians(10),
            Units.degreesToRadians(-29.5305)));

    public static final Transform3d frontRightTranslation = new Transform3d(
        Units.inchesToMeters(12.916),
        Units.inchesToMeters(-11.500), // -11.29
        Units.inchesToMeters(9.225750),
        new Rotation3d(
            Units.degreesToRadians(0),
            Units.degreesToRadians(0),
            Units.degreesToRadians(0)));
                                                              
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
      public static final Pose2d reefA = new Pose2d(3.239, 4.165, new Rotation2d(Units.degreesToRadians(0)));
      public static final Pose2d reefB = new Pose2d(3.239, 3.836, new Rotation2d(Units.degreesToRadians(0)));

      public static final Pose2d reefC = new Pose2d(3.744, 3.012, new Rotation2d(Units.degreesToRadians(60)));
      public static final Pose2d reefD = new Pose2d(4.028, 2.848, new Rotation2d(Units.degreesToRadians(60)));
      
      public static final Pose2d reefE = new Pose2d(4.994, 2.873, new Rotation2d(Units.degreesToRadians(120))); //x far from J
      public static final Pose2d reefF = new Pose2d(5.279, 3.037, new Rotation2d(Units.degreesToRadians(120)));
      
      public static final Pose2d reefG = new Pose2d(5.74, 3.887, new Rotation2d(Units.degreesToRadians(180)));
      public static final Pose2d reefH = new Pose2d(5.74, 4.216, new Rotation2d(Units.degreesToRadians(180))); // x far from rA, bB, bG
      
      public static final Pose2d reefI = new Pose2d(5.235, 5.04, new Rotation2d(Units.degreesToRadians(-120)));
      public static final Pose2d reefJ = new Pose2d(4.95, 5.204, new Rotation2d(Units.degreesToRadians(-120)));// x far from E
      
      public static final Pose2d reefK = new Pose2d(3.984, 5.179, new Rotation2d(Units.degreesToRadians(-60)));
      public static final Pose2d reefL = new Pose2d(3.7, 5.014, new Rotation2d(Units.degreesToRadians(-60)));
    }
    
    public static class RedAlliance{
      public static final Pose2d reefA = new Pose2d(14.31, 3.887, new Rotation2d(Units.degreesToRadians(180)));  //14.25468407, 3.849290216, new Rotation2d(Units.degreesToRadians(180)
      public static final Pose2d reefB = new Pose2d(14.31, 4.216, new Rotation2d(Units.degreesToRadians(180))); // 14.25368407, 4.2246333, new Rotation2d(Units.degreesToRadians(180) 

      public static final Pose2d reefC = new Pose2d(13.805, 5.04, new Rotation2d(Units.degreesToRadians(-120))); // X far from L
      public static final Pose2d reefD = new Pose2d(13.52, 5.204, new Rotation2d(Units.degreesToRadians(-120))); 

      public static final Pose2d reefE = new Pose2d(12.554, 5.179, new Rotation2d(Units.degreesToRadians(-60))); 
      public static final Pose2d reefF = new Pose2d(12.269, 5.014, new Rotation2d(Units.degreesToRadians(-60))); 

      public static final Pose2d reefG = new Pose2d(11.808, 4.165, new Rotation2d(Units.degreesToRadians(0))); 
      public static final Pose2d reefH = new Pose2d(11.808, 3.836, new Rotation2d(Units.degreesToRadians(0))); 

      public static final Pose2d reefI = new Pose2d(12.313, 3.012, new Rotation2d(Units.degreesToRadians(60))); // Y same as L
      public static final Pose2d reefJ = new Pose2d(12.598, 2.848, new Rotation2d(Units.degreesToRadians(60)));

      public static final Pose2d reefK = new Pose2d(13.564, 2.873, new Rotation2d(Units.degreesToRadians(120))); 
      public static final Pose2d reefL = new Pose2d(13.849, 3.037, new Rotation2d(Units.degreesToRadians(120))); // X far from C, Y same as I

    }
    
    public static class sourcePositions {
      public static final Pose2d blue1 = new Pose2d(0.621, 1.332, new Rotation2d(Units.degreesToRadians(54)));
      public static final Pose2d blue2 = new Pose2d(0.785, 1.213, new Rotation2d(Units.degreesToRadians(54)));//
      public static final Pose2d blue3 = new Pose2d(0.949, 1.093, new Rotation2d(Units.degreesToRadians(54)));
      public static final Pose2d blue4 = new Pose2d(1.114, 0.974, new Rotation2d(Units.degreesToRadians(54)));
      public static final Pose2d blue5 = new Pose2d(1.278, 0.854, new Rotation2d(Units.degreesToRadians(54)));
      public static final Pose2d blue6 = new Pose2d(1.442, 0.735, new Rotation2d(Units.degreesToRadians(54)));
      public static final Pose2d blue7 = new Pose2d(1.607, 0.615, new Rotation2d(Units.degreesToRadians(54)));

      public static final Pose2d blue8 = new Pose2d(0.579, 6.690, new Rotation2d(Units.degreesToRadians(-54)));
      public static final Pose2d blue9 = new Pose2d(0.744, 6.809, new Rotation2d(Units.degreesToRadians(-54)));
      public static final Pose2d blue10 = new Pose2d(0.908, 6.929, new Rotation2d(Units.degreesToRadians(-54)));
      public static final Pose2d blue11 = new Pose2d(1.073, 7.048, new Rotation2d(Units.degreesToRadians(-54)));
      public static final Pose2d blue12 = new Pose2d(1.237, 7.168, new Rotation2d(Units.degreesToRadians(-54)));
      public static final Pose2d blue13 = new Pose2d(1.402, 7.287, new Rotation2d(Units.degreesToRadians(-54)));
      public static final Pose2d blue14 = new Pose2d(1.566, 7.406, new Rotation2d(Units.degreesToRadians(-54)));

      public static final Pose2d red1 = new Pose2d(16.969, 1.362, new Rotation2d(Units.degreesToRadians(126 )));
      public static final Pose2d red2 = new Pose2d(16.804, 1.242, new Rotation2d(Units.degreesToRadians(126)));
      public static final Pose2d red3 = new Pose2d(16.640, 1.123, new Rotation2d(Units.degreesToRadians(126)));
      public static final Pose2d red4 = new Pose2d(16.475, 1.004, new Rotation2d(Units.degreesToRadians(126)));
      public static final Pose2d red5 = new Pose2d(16.311, 0.884, new Rotation2d(Units.degreesToRadians(126)));
      public static final Pose2d red6 = new Pose2d(16.147, 0.765, new Rotation2d(Units.degreesToRadians(126)));
      public static final Pose2d red7 = new Pose2d(15.982, 0.645, new Rotation2d(Units.degreesToRadians(126)));

      public static final Pose2d red8 = new Pose2d(16.928, 6.720, new Rotation2d(Units.degreesToRadians(-126)));
      public static final Pose2d red9 = new Pose2d(16.763, 6.839, new Rotation2d(Units.degreesToRadians(-126)));
      public static final Pose2d red10 = new Pose2d(16.598, 6.959, new Rotation2d(Units.degreesToRadians(-126)));
      public static final Pose2d red11 = new Pose2d(16.435, 7.078, new Rotation2d(Units.degreesToRadians(-126)));
      public static final Pose2d red12 = new Pose2d(16.270, 7.197, new Rotation2d(Units.degreesToRadians(-126)));
      public static final Pose2d red13 = new Pose2d(16.106, 7.317, new Rotation2d(Units.degreesToRadians(-126)));
      public static final Pose2d red14 = new Pose2d(15.941, 7.436, new Rotation2d(Units.degreesToRadians(-126)));
    }
    public static class redL1Positions{
      public static final Pose2d redA = new Pose2d(14.464,3.977,new Rotation2d(Units.degreesToRadians(28.9)));
      public static final Pose2d redB = new Pose2d(13.719,2.773,new Rotation2d(Units.degreesToRadians(-32.9)));
      public static final Pose2d redC = new Pose2d(12.315,2.822,new Rotation2d(Units.degreesToRadians(-92.9)));
      public static final Pose2d redD = new Pose2d(11.654,4.074,new Rotation2d(Units.degreesToRadians(-152.9)));
      public static final Pose2d redE = new Pose2d(12.399,5.255,new Rotation2d(Units.degreesToRadians(148.9)));
      public static final Pose2d redF = new Pose2d(13.803,5.206,new Rotation2d(Units.degreesToRadians(88.9)));
      
    }
    public static class blueL1Positions{
      public static final Pose2d blueA = new Pose2d(3.085,3.977,new Rotation2d(Units.degreesToRadians(-152.9)));
      public static final Pose2d blueB = new Pose2d(3.745,5.218,new Rotation2d(Units.degreesToRadians(148.9)));
      public static final Pose2d blueC = new Pose2d(5.15,5.267,new Rotation2d(Units.degreesToRadians(88.9)));
      public static final Pose2d blueD = new Pose2d(5.894,4.074,new Rotation2d(Units.degreesToRadians(28.9)));
      public static final Pose2d blueE = new Pose2d(5.234,2.834,new Rotation2d(Units.degreesToRadians(-32.9)));
      public static final Pose2d blueF = new Pose2d(3.829,2.785,new Rotation2d(Units.degreesToRadians(-92.9)));
      
    }

    public static final Pose2d[] sourcePoses = {
      sourcePositions.blue1,
      sourcePositions.blue2, 
      sourcePositions.blue3,
      sourcePositions.blue4,
      sourcePositions.blue5,
      sourcePositions.blue6,
      sourcePositions.blue7,
      sourcePositions.blue8,
      sourcePositions.blue9,
      sourcePositions.blue10,
      sourcePositions.blue11,
      sourcePositions.blue12,
      sourcePositions.blue13,
      sourcePositions.blue14,
      sourcePositions.red1,
      sourcePositions.red2,
      sourcePositions.red3,
      sourcePositions.red4,
      sourcePositions.red5,
      sourcePositions.red6,
      sourcePositions.red7,
      sourcePositions.red8,
      sourcePositions.red9,
      sourcePositions.red10,
      sourcePositions.red11,
      sourcePositions.red12,
      sourcePositions.red13,
      sourcePositions.red14
    };

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
    public static final Pose2d[] L1Poses = {
      redL1Positions.redA,
      blueL1Positions.blueA,
      redL1Positions.redB,
      redL1Positions.redC,
      redL1Positions.redD,
      redL1Positions.redE,
      redL1Positions.redF,
      blueL1Positions.blueB,
      blueL1Positions.blueC,
      blueL1Positions.blueD,
      blueL1Positions.blueE,
      blueL1Positions.blueF,
    };
    
  }
}