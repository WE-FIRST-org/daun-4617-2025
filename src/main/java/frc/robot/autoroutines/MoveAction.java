package frc.robot.autoroutines;

import frc.robot.Drivetrain;

public class MoveAction extends Action {
    public static final double HALFWIDTH = 18.75/2;
    private double rdistance, ldistance;
    private Drivetrain drivetrain;

    public MoveAction(double distance, double angle, Drivetrain d) {
        this.rdistance = ((distance/angle) - HALFWIDTH)*angle;
        this.ldistance = ((distance/angle) + HALFWIDTH)*angle;
        this.drivetrain = d;
    }

    @Override
    public boolean run() {
        return this.drivetrain.driveAuto(this.ldistance, this.rdistance);
    }
}
