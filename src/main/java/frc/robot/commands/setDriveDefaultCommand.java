package frc.robot.commands;

import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import static edu.wpi.first.units.Units.*;

public class setDriveDefaultCommand extends Command{

    private CommandSwerveDrivetrain drivetrain;
    private CommandXboxController driverXbox;
    private SwerveRequest.FieldCentric drive;

    private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
  private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond);


    public setDriveDefaultCommand(CommandSwerveDrivetrain _drivetrain, CommandXboxController _driverXbox, SwerveRequest.FieldCentric _drive){
        drivetrain = _drivetrain;
        driverXbox = _driverXbox;
        drive = _drive;
    }
    public void initialize(){
        drivetrain.setDefaultCommand(
      drivetrain.applyRequest(() -> drive.withVelocityX(-Math.pow(driverXbox.getLeftY(), 3) * MaxSpeed) // Drive forward with negative Y (forward)
        .withVelocityY(-Math.pow(driverXbox.getLeftX(), 3) * MaxSpeed) // Drive left with negative X (left)
        .withRotationalRate(-Math.pow(driverXbox.getRightX(), 3) * MaxAngularRate) // Drive counterclockwise with negative X (left)
      )
    );
    }
    public boolean isFinished(){
        return true;
    }
    
}
