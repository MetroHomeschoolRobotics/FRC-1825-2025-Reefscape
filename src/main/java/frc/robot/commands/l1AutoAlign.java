package frc.robot.commands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.CommandSwerveDrivetrain;

public class l1AutoAlign extends Command {
    private CommandSwerveDrivetrain drivetrain;
    private Pose2d closestFace = new Pose2d(1000, 1000, new Rotation2d(0));
    private PIDToPose pidToPose;

    private Pose2d [] L1Poses = Constants.FieldSetpoints.L1Poses;
    /**
     * Create a new L1AutoAlign command.
     * Which will choose the nearest L1 face
     * and continually command the drive train to drive
     * to it until the command is no longer scheduled
     * 
     * @param _drivetrain The drivetrain object
     */
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
