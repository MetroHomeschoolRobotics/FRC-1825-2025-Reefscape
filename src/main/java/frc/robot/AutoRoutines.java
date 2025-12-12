package frc.robot;

import choreo.auto.AutoFactory;
import choreo.auto.AutoRoutine;
import choreo.auto.AutoTrajectory;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Command.InterruptionBehavior;
import frc.robot.commands.DriveToBranch;
import frc.robot.commands.DriveToBranchAuto;
import frc.robot.commands.DriveToSource;
import frc.robot.commands.RetractElevator;
import frc.robot.commands.RunOuttake;
import frc.robot.commands.Score;
import frc.robot.commands.StaggerMotors;
import frc.robot.commands.printDebug;
import frc.robot.commands.printDebug;
import frc.robot.commands.shoulderToIntake;
import frc.robot.commands.autoCommands.ShoulderToIntake;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.ShoulderPID;

public class AutoRoutines {
    private final AutoFactory m_factory;
    private final ShoulderPID m_shoulder;
    private final Elevator m_elevator;
    private final Intake m_intake;
    private final CommandSwerveDrivetrain m_drivetrain;

    public AutoRoutines(AutoFactory factory, ShoulderPID shoulder, Elevator elevator, Intake intake,CommandSwerveDrivetrain drivetrain) {
    
        m_factory = factory;
        m_shoulder = shoulder;
        m_elevator = elevator;
        m_intake = intake;
        m_drivetrain = drivetrain;
        
        
    }
    
    public AutoRoutine taxiWithCommand(){
        final AutoRoutine routine = m_factory.newRoutine("taxiWithCommand");
        /*Trajectories used in the routine */
        final AutoTrajectory taxiWithCommand = routine.trajectory("taxiWithCommand");
        final AutoTrajectory BackwardsAfterTaxi = routine.trajectory("BackwardsAfterTaxi");
        final AutoTrajectory TaxiAgain = routine.trajectory("TaxiAgain");
        /*Commands to be used in Command Groups */
        //  final shoulderToIntake shoulderToIntake  = new shoulderToIntake(m_shoulder, m_elevator);
        //  final StaggerMotors intake = new StaggerMotors(m_intake);
        //  final Score score = new Score(m_elevator, m_shoulder, m_intake, 4);
        // final RunOuttake runOuttake = new RunOuttake(m_intake);
        // final RetractElevator retractElevator = new RetractElevator(m_elevator, m_shoulder);
        

        /*Command Groups to be called in the routine */
         //final SequentialCommandGroup scoreL4Group = new SequentialCommandGroup(new Score(m_elevator, m_shoulder, m_intake, 4), new RunOuttake(m_intake),new RetractElevator(m_elevator, m_shoulder));
         //final SequentialCommandGroup scoreL4Group2 = new SequentialCommandGroup(score,runOuttake,retractElevator);

         //final SequentialCommandGroup intakeGroup = new SequentialCommandGroup(new shoulderToIntake(m_shoulder, m_elevator),new StaggerMotors(m_intake));

        /*Master Command group called in routine (this will probably be more useful with more
         complicated routines ) */
        //  final SequentialCommandGroup Master = new SequentialCommandGroup(
        //     taxiWithCommand.resetOdometry(),taxiWithCommand.cmd(),scoreL4Group,
        //     BackwardsAfterTaxi.cmd(),intakeGroup, TaxiAgain.cmd(), scoreL4Group2
        //     );
        // routine.active().onTrue(
        //     taxiWithCommand.resetOdometry().andThen(taxiWithCommand.cmd())
        //     .andThen(new SequentialCommandGroup(new Score(m_elevator, m_shoulder, m_intake, 4), new RunOuttake(m_intake),new RetractElevator(m_elevator, m_shoulder)))
        //     .andThen(BackwardsAfterTaxi.cmd()).andThen(new SequentialCommandGroup(new shoulderToIntake(m_shoulder, m_elevator),new StaggerMotors(m_intake))
        //     )
        //     .andThen(TaxiAgain.cmd()).andThen(new SequentialCommandGroup(new Score(m_elevator, m_shoulder, m_intake, 4), new RunOuttake(m_intake),new RetractElevator(m_elevator, m_shoulder)))
        // );
        //routine.active().onTrue(taxiWithCommand.resetOdometry().andThen(null))
        // routine.active().onTrue(
        //     taxiWithCommand.resetOdometry().andThen(taxiWithCommand.cmd())
        //     .andThen(new SequentialCommandGroup(new Score(m_elevator, m_shoulder, m_intake, 4), new RunOuttake(m_intake),new RetractElevator(m_elevator, m_shoulder)))
        //     .andThen(BackwardsAfterTaxi.cmd()).andThen(new SequentialCommandGroup(new shoulderToIntake(m_shoulder, m_elevator),new StaggerMotors(m_intake))
        //     )
        //     .andThen(TaxiAgain.cmd()).andThen(new SequentialCommandGroup(new Score(m_elevator, m_shoulder, m_intake, 4), new RunOuttake(m_intake),new RetractElevator(m_elevator, m_shoulder)))
        // );
        //routine.active().onTrue(taxiWithCommand.resetOdometry().andThen(null))
        return routine;
    }
    public AutoRoutine Taxi(){
        final AutoRoutine routine = m_factory.newRoutine("taxi");
        final AutoTrajectory taxi = routine.trajectory("taxi");
        routine.active().onTrue(
            //taxi.resetOdometry().andThen(taxi.cmd()).andThen(new printDebug()).andThen( new DriveToBranch("R",m_drivetrain)).andThen(new printDebug()).andThen(new Score(m_elevator, m_shoulder, m_intake, 4)).andThen(new RunOuttake(m_intake)).andThen(new RetractElevator(m_elevator,m_shoulder))
            taxi.resetOdometry().andThen(Commands.waitSeconds(5)).andThen(taxi.cmd()).andThen(new SequentialCommandGroup( new Score(m_elevator, m_shoulder, m_intake, 4)),new RunOuttake(m_intake),new RetractElevator(m_elevator, m_shoulder))
                    
            //taxi.resetOdometry().andThen( Commands.waitSeconds(0)).andThen(taxi.cmd()).andThen(new DriveToBranch("L", m_drivetrain)).alongWith(new SequentialCommandGroup(Commands.waitSeconds(1.2),new Score(m_elevator, m_shoulder, m_intake, 4), new RunOuttake(m_intake),new RetractElevator(m_elevator, m_shoulder)))
                    );
        return routine;
    }

    public AutoRoutine Left() {
        final AutoRoutine routine = m_factory.newRoutine("Left");
        final AutoTrajectory StartToI = routine.trajectory("LeftToI");
        final AutoTrajectory IToSource = routine.trajectory("LeftIToSource");
        final AutoTrajectory SourceToK = routine.trajectory("LeftSourceToK");
       
        
       //parallel race group fixes everything?
        routine.active().onTrue(
            StartToI.resetOdometry()
                .andThen(StartToI.cmd()).alongWith( new SequentialCommandGroup( Commands.waitSeconds(1.5),
                new Score(m_elevator, m_shoulder, m_intake, 4), new RunOuttake(m_intake),new RetractElevator(m_elevator, m_shoulder)))
                .andThen(IToSource.cmd()).andThen(new SequentialCommandGroup(new shoulderToIntake(m_shoulder, m_elevator),new StaggerMotors(m_intake))).andThen(new ParallelCommandGroup(SourceToK.cmd(),(new SequentialCommandGroup(Commands.waitSeconds(1.9),new Score(m_elevator, m_shoulder, m_intake, 4), new RunOuttake(m_intake),new RetractElevator(m_elevator, m_shoulder)))))
        );
        return routine;
    }
    public AutoRoutine Right(){
        final AutoRoutine routine = m_factory.newRoutine("Right");
        final AutoTrajectory StartToE = routine.trajectory("RightToE");
        final AutoTrajectory EToSource = routine.trajectory("RightEToSource");
        final AutoTrajectory SourceToD = routine.trajectory("RightSourceToD");

        routine.active().onTrue(
            StartToE.resetOdometry().andThen(StartToE.cmd()).alongWith(new SequentialCommandGroup(Commands.waitSeconds(1.5),new Score(m_elevator, m_shoulder, m_intake, 4), new RunOuttake(m_intake),new RetractElevator(m_elevator, m_shoulder)) ).andThen(
EToSource.cmd() ).andThen(new SequentialCommandGroup(new shoulderToIntake(m_shoulder, m_elevator),new StaggerMotors(m_intake))).andThen(new ParallelCommandGroup(SourceToD.cmd(),(new SequentialCommandGroup(Commands.waitSeconds(1.9),new Score(m_elevator, m_shoulder, m_intake, 4), new RunOuttake(m_intake),new RetractElevator(m_elevator, m_shoulder)))))
        );
        return routine;
    }
}
