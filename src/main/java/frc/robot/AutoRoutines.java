package frc.robot;

import choreo.auto.AutoFactory;
import choreo.auto.AutoRoutine;
import choreo.auto.AutoTrajectory;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.RetractElevator;
import frc.robot.commands.RunOuttake;
import frc.robot.commands.Score;
import frc.robot.commands.StaggerMotors;
import frc.robot.commands.shoulderToIntake;
import frc.robot.commands.autoCommands.ShoulderToIntake;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.ShoulderPID;

public class AutoRoutines {
    private final AutoFactory m_factory;
    private final ShoulderPID m_shoulder;
    private final Elevator m_elevator;
    private final Intake m_intake;

   
    

    public AutoRoutines(AutoFactory factory, ShoulderPID shoulder, Elevator elevator, Intake intake) {
        m_factory = factory;
        m_shoulder = shoulder;
        m_elevator = elevator;
        m_intake = intake;

        
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
        routine.active().onTrue(
            taxiWithCommand.resetOdometry().andThen(taxiWithCommand.cmd())
            .andThen(new SequentialCommandGroup(new Score(m_elevator, m_shoulder, m_intake, 4), new RunOuttake(m_intake),new RetractElevator(m_elevator, m_shoulder)))
            .andThen(BackwardsAfterTaxi.cmd()).andThen(new SequentialCommandGroup(new shoulderToIntake(m_shoulder, m_elevator),new StaggerMotors(m_intake))
            )
            .andThen(TaxiAgain.cmd()).andThen(new SequentialCommandGroup(new Score(m_elevator, m_shoulder, m_intake, 4), new RunOuttake(m_intake),new RetractElevator(m_elevator, m_shoulder)))
        );
        return routine;
    }
    public AutoRoutine taxi(){
        final AutoRoutine routine = m_factory.newRoutine("taxi");
        final AutoTrajectory taxi = routine.trajectory("TaxiAgain");
        routine.active().onTrue(
            taxi.resetOdometry().andThen(taxi.cmd()).andThen(new SequentialCommandGroup(new Score(m_elevator, m_shoulder, m_intake, 4), new RunOuttake(m_intake),new RetractElevator(m_elevator, m_shoulder)))
                    );
        return routine;
    }

    public AutoRoutine simplePathAuto() {
        final AutoRoutine routine = m_factory.newRoutine("SimplePath Auto");
        final AutoTrajectory simplePath = routine.trajectory("SimplePath");

        routine.active().onTrue(
            simplePath.resetOdometry()
                .andThen(simplePath.cmd())
        );
        return routine;
    }
}
