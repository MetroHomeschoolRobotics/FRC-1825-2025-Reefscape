
// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.List;
import java.util.Optional;

import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonUtils;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Vision extends SubsystemBase {

  private AprilTagFieldLayout aprilTagFieldLayout = Constants.FieldSetpoints.aprilTagFieldLayout;
  // private AprilTagFieldLayout aprilTagFieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2025ReefscapeAndyMark);

  private Transform3d cameraPosition;

  private PhotonCamera camera;
  private PhotonPoseEstimator photonPoseEstimator;

  /** Creates a new Vision. */
  public Vision(String cameraName, Transform3d cameraTransform) {
    camera = new PhotonCamera(cameraName);
    cameraPosition = cameraTransform;
    photonPoseEstimator = new PhotonPoseEstimator(aprilTagFieldLayout, PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, cameraPosition);
  }

  @Override
  public void periodic() {
    // SmartDashboard.putNumber("ApriltagDistance", getApriltagDistance());
    // This method will be called once per scheduler run
  }

  public List<PhotonPipelineResult> getAllUnreadResults() {
    return camera.getAllUnreadResults();
  }
  

  public PhotonPipelineResult getLatestResult() {
    return camera.getLatestResult(); // TODO remove the depricated function
  }
  public PhotonTrackedTarget getBestTarget() {
    return getLatestResult().getBestTarget();
  }

  public Boolean hasTargets() {
    if(!getAllUnreadResults().isEmpty()){
      PhotonPipelineResult target = getAllUnreadResults().get(getAllUnreadResults().size()-1);

      return target.hasTargets();
    } else {
      return false;
    }
    
  }

  public double getYaw() {
    return getBestTarget().getYaw();
  }
  public double getSkew() {
    return getBestTarget().getSkew();
  }
  public double getPitch() {
    return getBestTarget().getPitch();
  }
  public double getArea() {
    return getBestTarget().getArea();
  }

  public Transform3d getRobotTransform() {
    return getBestTarget().getBestCameraToTarget();
  }

  public double getApriltagDistance(Pose2d robotPose, int apriltagID) {
    double tagDist = 100000;
    if(hasTargets() && getBestTarget() != null) {

      tagDist = PhotonUtils.getDistanceToPose(robotPose, Constants.FieldSetpoints.aprilTagFieldLayout.getTagPose(apriltagID).get().toPose2d());
      
    }

    return tagDist;

  }

  public double getPoseAmbiguity() {
    return getBestTarget().getPoseAmbiguity();
  }

  public int getApriltagID() {
    return getBestTarget().getFiducialId();
  }

  public void getTagPose() {
    aprilTagFieldLayout.getTagPose(getApriltagID());
  }

  public Optional<EstimatedRobotPose> getVisionBasedPose() {
    return photonPoseEstimator.update(getLatestResult());
  }



}
