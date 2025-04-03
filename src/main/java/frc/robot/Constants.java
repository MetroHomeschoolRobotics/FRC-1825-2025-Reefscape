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
    public static final double level4Height = -207.5;// angle: 1.5
    public static final double level3Height = -146;//angle 2.9
    public static final double level2Height = -108;//angle 3.8
    public static final double level1Height = -93.66;

    public static final double level4Angle = 0.7;
    public static final double level3Angle = 2.9;
    public static final double level2Angle = 3.8;
    public static final double level1Angle = 1;

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
                                                              Units.degreesToRadians(0),
                                                              Units.degreesToRadians(10),
                                                              Units.degreesToRadians(-30)));
                                                              
    public static final Transform3d frontRightTranslation = new Transform3d(
                                                            Units.inchesToMeters(11.147131),
                                                            Units.inchesToMeters(-11.404959), // -11.29
                                                            Units.inchesToMeters(9.321816), 
                                                            new Rotation3d(
                                                              Units.degreesToRadians(0),
                                                              Units.degreesToRadians(10),
                                                              Units.degreesToRadians(30)));
                                                              
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
      public static final Pose2d reefA = new Pose2d(3.32272355, 4.1856333, new Rotation2d(Units.degreesToRadians(0)));
      public static final Pose2d reefB = new Pose2d(3.311527669, 3.81707825, new Rotation2d(Units.degreesToRadians(0)));

      public static final Pose2d reefC = new Pose2d(3.7737811326, 3.0893695, new Rotation2d(Units.degreesToRadians(60)));
      public static final Pose2d reefD = new Pose2d(4.085167845, 2.921781414, new Rotation2d(Units.degreesToRadians(60)));
      
      public static final Pose2d reefE = new Pose2d(4.969947888, 2.941575528, new Rotation2d(Units.degreesToRadians(120))); //x far from J
      public static final Pose2d reefF = new Pose2d(5.261939688, 3.159151296, new Rotation2d(Units.degreesToRadians(120)));
      
      public static final Pose2d reefG = new Pose2d(5.6696601894, 3.883143764, new Rotation2d(Units.degreesToRadians(180)));
      public static final Pose2d reefH = new Pose2d(5.6696601894, 4.1853208877, new Rotation2d(Units.degreesToRadians(180))); // x far from rA, bB, bG
      
      public static final Pose2d reefI = new Pose2d(5.214398024350, 4.9613547, new Rotation2d(Units.degreesToRadians(-120)));
      public static final Pose2d reefJ = new Pose2d(4.8514365201, 5.1208748116, new Rotation2d(Units.degreesToRadians(-120)));// x far from E
      
      public static final Pose2d reefK = new Pose2d(4.022437138, 5.114805594, new Rotation2d(Units.degreesToRadians(-60)));
      public static final Pose2d reefL = new Pose2d(3.795782721, 4.94210006, new Rotation2d(Units.degreesToRadians(-60)));
    }
    
    public static class RedAlliance{
      public static final Pose2d reefA = new Pose2d(14.24, 3.840, new Rotation2d(Units.degreesToRadians(180)));  //14.25468407, 3.849290216, new Rotation2d(Units.degreesToRadians(180)
      public static final Pose2d reefB = new Pose2d(14.24, 4.185, new Rotation2d(Units.degreesToRadians(180))); // 14.25368407, 4.2246333, new Rotation2d(Units.degreesToRadians(180) 

      public static final Pose2d reefC = new Pose2d(13.78666223, 4.9852731, new Rotation2d(Units.degreesToRadians(-120))); // X far from L
      public static final Pose2d reefD = new Pose2d(13.4887708, 5.1766442, new Rotation2d(Units.degreesToRadians(-120))); 

      public static final Pose2d reefE = new Pose2d(12.589619238, 5.14413342, new Rotation2d(Units.degreesToRadians(-60))); 
      public static final Pose2d reefF = new Pose2d(12.29341787, 4.9820127808, new Rotation2d(Units.degreesToRadians(-60))); 

      public static final Pose2d reefG = new Pose2d(11.8599482155, 4.120231787199, new Rotation2d(Units.degreesToRadians(0))); 
      public static final Pose2d reefH = new Pose2d(11.8575273585, 3.819012489, new Rotation2d(Units.degreesToRadians(0))); 

      public static final Pose2d reefI = new Pose2d(12.283160148, 3.087600667, new Rotation2d(Units.degreesToRadians(60))); // Y same as L
      public static final Pose2d reefJ = new Pose2d(12.64403212, 2.9046532387, new Rotation2d(Units.degreesToRadians(60)));

      public static final Pose2d reefK = new Pose2d(13.5113083, 2.90800257, new Rotation2d(Units.degreesToRadians(120))); 
      public static final Pose2d reefL = new Pose2d(13.83166223, 3.102600667, new Rotation2d(Units.degreesToRadians(120))); // X far from C, Y same as I

    }
    
    public static class sourcePositions {
      public static final Pose2d blue1 = new Pose2d(0.61087, 1.37541, new Rotation2d(Units.degreesToRadians(54)));
      public static final Pose2d blue2 = new Pose2d(0.77546, 1.25603, new Rotation2d(Units.degreesToRadians(54)));//
      public static final Pose2d blue3 = new Pose2d(0.93980, 1.13665, new Rotation2d(Units.degreesToRadians(54)));
      public static final Pose2d blue4 = new Pose2d(1.10414, 1.01727, new Rotation2d(Units.degreesToRadians(54)));
      public static final Pose2d blue5 = new Pose2d(1.26848, 0.89789, new Rotation2d(Units.degreesToRadians(54)));
      public static final Pose2d blue6 = new Pose2d(1.43307, 0.77851, new Rotation2d(Units.degreesToRadians(54)));
      public static final Pose2d blue7 = new Pose2d(1.59741, 0.65913, new Rotation2d(Units.degreesToRadians(54)));

      public static final Pose2d blue8 = new Pose2d(0.61087, 6.77520, new Rotation2d(Units.degreesToRadians(-54)));
      public static final Pose2d blue9 = new Pose2d(0.77546, 6.89458, new Rotation2d(Units.degreesToRadians(-54)));
      public static final Pose2d blue10 = new Pose2d(0.93980, 7.01396, new Rotation2d(Units.degreesToRadians(-54)));
      public static final Pose2d blue11 = new Pose2d(1.10414, 7.13334, new Rotation2d(Units.degreesToRadians(-54)));
      public static final Pose2d blue12 = new Pose2d(1.26848, 7.25297, new Rotation2d(Units.degreesToRadians(-54)));
      public static final Pose2d blue13 = new Pose2d(1.43307, 7.37235, new Rotation2d(Units.degreesToRadians(-54)));
      public static final Pose2d blue14 = new Pose2d(1.59741, 7.49173, new Rotation2d(Units.degreesToRadians(-54)));

      public static final Pose2d red1 = new Pose2d(16.9817, 1.37541, new Rotation2d(Units.degreesToRadians(126 )));
      public static final Pose2d red2 = new Pose2d(16.8166, 1.25603, new Rotation2d(Units.degreesToRadians(126)));
      public static final Pose2d red3 = new Pose2d(16.9070, 1.13665, new Rotation2d(Units.degreesToRadians(126)));
      public static final Pose2d red4 = new Pose2d(16.4884, 1.01727, new Rotation2d(Units.degreesToRadians(126)));
      public static final Pose2d red5 = new Pose2d(16.3241, 0.89789, new Rotation2d(Units.degreesToRadians(126)));
      public static final Pose2d red6 = new Pose2d(16.1597, 0.77851, new Rotation2d(Units.degreesToRadians(126)));
      public static final Pose2d red7 = new Pose2d(15.9951, 0.65913, new Rotation2d(Units.degreesToRadians(126)));

      public static final Pose2d red8 = new Pose2d(16.9817, 6.77520, new Rotation2d(Units.degreesToRadians(-126)));
      public static final Pose2d red9 = new Pose2d(16.8166, 6.89458, new Rotation2d(Units.degreesToRadians(-126)));
      public static final Pose2d red10 = new Pose2d(16.9070, 7.01396, new Rotation2d(Units.degreesToRadians(-126)));
      public static final Pose2d red11 = new Pose2d(16.4884, 7.13334, new Rotation2d(Units.degreesToRadians(-126)));
      public static final Pose2d red12 = new Pose2d(16.3241, 7.25297, new Rotation2d(Units.degreesToRadians(-126)));
      public static final Pose2d red13 = new Pose2d(16.1597, 7.37235, new Rotation2d(Units.degreesToRadians(-126)));
      public static final Pose2d red14 = new Pose2d(15.9951, 17.49173, new Rotation2d(Units.degreesToRadians(-126)));
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

  }
}