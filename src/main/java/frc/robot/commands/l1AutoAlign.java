package frc.robot.commands;

import com.pathplanner.lib.events.TriggerEvent;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ScheduleCommand;
import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants;
import frc.robot.Constants.FieldSetpoints;
import frc.robot.subsystems.CommandSwerveDrivetrain;

public class l1AutoAlign extends Command {
    private CommandSwerveDrivetrain drivetrain;
    private Pose2d closestFace = new Pose2d(1000, 1000, new Rotation2d(0));
    private PIDToPose pidToPose;

    private Pose2d [] L1Poses = Constants.FieldSetpoints.L1Poses;

    public l1AutoAlign(CommandSwerveDrivetrain _drivetrain){
        addRequirements(_drivetrain);
        drivetrain = _drivetrain;
    }
  
    public void initialize(){
        for(Pose2d pose: L1Poses){
            double dist1 = drivetrain.distanceToPose(closestFace);
            double dist2 = drivetrain.distanceToPose(pose);

            if(dist1>dist2){
                closestFace = pose;
            }

        }
        System.out.println(closestFace);
        drivetrain.driveToPose(closestFace, 2, 2,270,360).schedule();
    }
    public void execute(){}
    public void end(boolean interrupted){}
    public boolean isFinished(){
        return false;
    }
}
