package frc.robot.subsystems;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;





//subsystem for intake
public class Intake extends SubsystemBase {
    //TO/DO make motor object(s)
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
      //this will use the .set(speed) method on the motor object
      //TO/DO write above
    }
  
    //public double getDistance() {
      
        //this will use the .getEncoder().getPosition() 
        //method on the motor object
        //TO/DO write above
    //}
  
    //public double getSpeed() {
        //this will use the .getEncoder().getVelocity()
      //TO/DO write above
    //}
  }
