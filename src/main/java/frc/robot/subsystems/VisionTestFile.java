package frc.robot.subsystems;

import java.util.List;

import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;


public class VisionTestFile extends SubsystemBase {
    private AprilTagFieldLayout k_FieldLayout = Constants.FieldSetpoints.aprilTagFieldLayout;
    private Transform3d cameraPosition;

    private PhotonCamera camera;
    private PhotonPoseEstimator poseEstimator;
    private List<PhotonPipelineResult> targetList;
public VisionTestFile(String cameraName, Transform3d _cameraPosition){
    camera = new PhotonCamera(cameraName);
    cameraPosition = _cameraPosition;
    poseEstimator = new PhotonPoseEstimator(k_FieldLayout, PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, cameraPosition);
}
public void periodic(){
    targetList = getAllUnreadResults();
}

public List<PhotonPipelineResult> getAllUnreadResults(){
    return camera.getAllUnreadResults();
}

public Boolean hasTargets(){
    if(!targetList.isEmpty()){
        PhotonPipelineResult target = targetList.get(targetList.size()-1);
        return target.hasTargets();
    }else{
        return false;
    }
}

public PhotonPipelineResult getLatestResult(){
    if(!targetList.isEmpty()){
       return targetList.get(targetList.size()-1);
    }
    return null;
}
public PhotonTrackedTarget getBestTarget(){
    return getLatestResult().getBestTarget();
}
}
