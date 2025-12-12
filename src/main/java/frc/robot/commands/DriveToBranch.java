// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

//import com.pathplanner.lib.events.TriggerEvent;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.robotToM4;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class DriveToBranch extends Command {

  private CommandSwerveDrivetrain drivetrain;
  private String LeftOrRightBranch;

  private Pose2d closestBranch = new Pose2d(1000, 1000, new Rotation2d());

  private Pose2d [] leftBranches = Constants.FieldSetpoints.leftReefBranches;
  private Pose2d [] rightBranches = Constants.FieldSetpoints.rightReefBranches;

  private PIDToPose pidToPose;

  /** Creates a new DriveToBranch command. 
   *  Which will choose the nearest branch to drive to
   *  and continually command the drivetrain to drive to it
   *  until the command is no longer scheduled.
   * @param _LOrRBranch Whether you want to drive to the nearest Left or Right branch
   * @param _drivetrain The drivetrain object
  */
  public DriveToBranch(String _LOrRBranch, CommandSwerveDrivetrain _drivetrain) {
    drivetrain = _drivetrain;
    LeftOrRightBranch = _LOrRBranch;

    

    addRequirements(_drivetrain);
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    robotToM4.changeMode("SCOREPRE");
    if(LeftOrRightBranch == "L") {
      // find the closest left reef branch
      for(Pose2d branches : leftBranches) {
        double apriltag1Dist = drivetrain.distanceToPose(closestBranch);
        double apriltag2Dist = drivetrain.distanceToPose(branches);

        if(apriltag1Dist > apriltag2Dist) {
          closestBranch = branches;
        }
      }      
    }

    if(LeftOrRightBranch == "R") {
        // find the closest right reef branch
        for(Pose2d branches : rightBranches) {
          double apriltag1Dist = drivetrain.distanceToPose(closestBranch);
          double apriltag2Dist = drivetrain.distanceToPose(branches);
          if(apriltag1Dist > apriltag2Dist) {
            closestBranch = branches;
          }
        }
    }


    drivetrain.driveToPose(closestBranch, 2, 2,180,360).schedule();

  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    System.out.println("BEEp");
  }
  @Override
  public boolean isFinished() {
    //return (Math.abs(drivetrain.distanceToPose(closestBranch)) <= 0.000);
    return false;
  }
  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    System.out.println("done :)");
  }

  // Returns true when the command should end.
  
}
