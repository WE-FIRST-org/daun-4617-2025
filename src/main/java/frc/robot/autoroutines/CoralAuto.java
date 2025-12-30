package frc.robot.autoroutines;

import frc.robot.AlgaeIntake;

public class CoralAuto extends Action {
    AlgaeIntake intake;

    public CoralAuto(AlgaeIntake intake) {
        this.intake = intake;
    }

    public boolean run() {
        this.intake.reverseIntake();
        return true;
    }
    
}
