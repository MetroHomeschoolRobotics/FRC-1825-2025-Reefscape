package frc.robot.subsystems;

import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class deAlgae extends SubsystemBase {
    private SparkMax deAlgae = new SparkMax(Constants.MotorIDs.deAlgaeMotorId, SparkLowLevel.MotorType.kBrushless);

/**
 * Creates a deAlgae subsystem
 * <p>
 * Controls the deAlgae motor
 */    
    public deAlgae(){}
    public void runDeAlgae(){
        deAlgae.set(1);
        
    }
    public void stopDeAlgae(){
        deAlgae.set(0);
        
    }
    public void periodic(){}
}
