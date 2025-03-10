package frc.robot.subsystems;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Constants;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkLowLevel;





public class Intake extends SubsystemBase {
    
    private SparkMax intakeMotor1 = new SparkMax(Constants.MotorIDs.intakeDeviceID1, SparkLowLevel.MotorType.kBrushless);
    private SparkMax intakeMotor2 = new SparkMax(Constants.MotorIDs.intakeDeviceID2, SparkLowLevel.MotorType.kBrushless);
    private DigitalInput beambreak = new DigitalInput(0);
   
    //private SparkBaseConfig config = new SparkMaxConfig().inverted(true);
  
    /** Creates a new Intake. */
    public Intake() {
     // intakeMotor2.configure(
       //     config, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);
    }
    
    @Override
    public void periodic() {
      SmartDashboard.putBoolean("coral in Intake", coralInIntake() );
      // This method will be called once per scheduler run
    }
  
    public Boolean coralInIntake() {
      return !beambreak.get();
      
    }  
  
    public void setSpeed(double speed) {
      //check inverts
      intakeMotor1.set(speed);
      intakeMotor2.set(speed);
      
    }
    public void setSpeedSideways(double speed){
      intakeMotor1.set(speed);
      intakeMotor2.set(0);
    }
    public double getDistance() {
        return intakeMotor1.getEncoder().getPosition();
        
        //I have no idea if we are using this
        
    }
  
    public double getSpeed() {
      return intakeMotor1.getEncoder().getVelocity();
        
    }
  }
