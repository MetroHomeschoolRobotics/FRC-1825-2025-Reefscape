package frc.robot;

import choreo.auto.AutoFactory;
import choreo.auto.AutoRoutine;
import choreo.auto.AutoTrajectory;

public class AutoRoutines {
    private final AutoFactory m_factory;

    public AutoRoutines(AutoFactory factory) {
        m_factory = factory;
    }
    public AutoRoutine taxiWithCommand(){
        final AutoRoutine routine = m_factory.newRoutine("taxiWithCommand");
        final AutoTrajectory taxiWithCommand = routine.trajectory("taxiWithCommand");
        
        routine.active().onTrue(
            taxiWithCommand.resetOdometry().andThen(taxiWithCommand.cmd().andThen(taxiWithCommand.spawnCmd()))
        );
        return routine;
    }
    public AutoRoutine taxi(){
        final AutoRoutine routine = m_factory.newRoutine("taxi");
        final AutoTrajectory taxi = routine.trajectory("taxi");
        routine.active().onTrue(
            taxi.resetOdometry().andThen(taxi.cmd())
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
