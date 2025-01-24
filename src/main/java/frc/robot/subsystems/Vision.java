// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Vision extends SubsystemBase {

  private AprilTagFieldLayout aprilTagFieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);

  private Transform3d cameraPosition;

  private PhotonCamera camera;
  private PhotonPoseEstimator photonPoseEstimator = new PhotonPoseEstimator(null, null, cameraPosition);

  /** Creates a new Vision. */
  public Vision(String cameraName, Transform3d cameraTransform) {
    camera = new PhotonCamera(cameraName);
    cameraPosition = cameraTransform;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public PhotonPipelineResult getLatestResult() {
    return camera.getAllUnreadResults().get(0);
  }
  public PhotonTrackedTarget getBestTarget() {
    return getLatestResult().getBestTarget();
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

  public double getPoseAmbiguity() {
    return getBestTarget().getPoseAmbiguity();
  }

  public int getApriltagID() {
    return getBestTarget().getFiducialId();
  }



}
