package frc.robot.subsystems;

import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class deAlgae extends SubsystemBase {
    private SparkMax deAlgae = new SparkMax(14, SparkLowLevel.MotorType.kBrushless);
    public deAlgae(){}
    public void runDeAlgae(){
        deAlgae.set(1);
        System.out.println("deAlgae set to 1");
    }
    public void stopDeAlgae(){
        deAlgae.set(0);
        System.out.println("deAlgae set to 1");
    }
    public void periodic(){}
}
