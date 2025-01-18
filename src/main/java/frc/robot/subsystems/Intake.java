package frc.robot.subsystems;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Constants;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.SparkLowLevel;




//subsystem for intake
public class Intake extends SubsystemBase {
    
    SparkMax intakeMotor = new SparkMax(Constants.intakeDeviceID, SparkLowLevel.MotorType.kBrushless);
    
    //TO/DO make beambreak object
    
  
    /** Creates a new Intake. */
    public Intake() {}
    
    @Override
    public void periodic() {
      SmartDashboard.putBoolean("coral in Intake", coralInIntake() );
      // This method will be called once per scheduler run
    }
  
    public Boolean coralInIntake() {
      return false;
      //TO/DO assign this when beambreak is ready/other solution is ready
    }  
  
    public void setSpeed(double speed) {
      
      intakeMotor.set(speed);
      //mayhaps this got changed since last year, stick with for now
      
    }
  
    public double getDistance() {
        return intakeMotor.getEncoder().getPosition();
        //I have no idea if we are using this
        
    }
  
    public double getSpeed() {
      return intakeMotor.getEncoder().getVelocity();
        
    }
  }
