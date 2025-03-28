// path planner

package frc.robot;

// teleop imports
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

// auto imports
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;

public class Drivetrain {
    private static final double RATIO = 8.45;
    private static final double WHEEL = 6 * Math.PI;
    private static final double IN2ENC = RATIO / WHEEL;
    private static final double P = 0.025;
    private static final double I = 0;
    private static final double D = 0;
    private static final double BOOSTRATIO = 0.5;

    private SparkMax rightMotor1, rightMotor2, leftMotor1, leftMotor2;
    SparkMaxConfig globalConfig = new SparkMaxConfig();
    SparkMaxConfig rightMotor1Config = new SparkMaxConfig();
    SparkMaxConfig rightMotor2Config = new SparkMaxConfig();
    SparkMaxConfig leftMotor1Config = new SparkMaxConfig();
    SparkMaxConfig leftMotor2Config = new SparkMaxConfig();

    // auto motors
    SparkClosedLoopController rAuto; // right motor auto
    SparkClosedLoopController lAuto; // left motor auto
    RelativeEncoder rEncoder;
    RelativeEncoder lEncoder;

    public enum DriveTrainMode { // mode switch var
        QUAD,
        CUBIC,
        LINEAR
    }

    DriveTrainMode driveProfile = DriveTrainMode.QUAD;
    boolean boostModeOn = true;
    double boostFactor = 1;

    public Drivetrain() {
        // Right Motors
        rightMotor1 = new SparkMax(2, MotorType.kBrushless);
        rightMotor2 = new SparkMax(4, MotorType.kBrushless);
        // Left Motors
        leftMotor1 = new SparkMax(1, MotorType.kBrushless);
        leftMotor2 = new SparkMax(3, MotorType.kBrushless);

        // auto controllers
        rAuto = rightMotor1.getClosedLoopController();
        lAuto = leftMotor1.getClosedLoopController();
        rEncoder = rightMotor1.getEncoder();
        lEncoder = leftMotor1.getEncoder();

        globalConfig.smartCurrentLimit(50).idleMode(IdleMode.kBrake).closedLoopRampRate(1);
        rightMotor1Config.apply(globalConfig).inverted(true);
        rightMotor2Config.apply(globalConfig).inverted(true).follow(rightMotor1);
        leftMotor1Config.apply(globalConfig);
        leftMotor2Config.apply(globalConfig).follow(leftMotor1);

        // auto configs
        rightMotor1Config.encoder.positionConversionFactor(1);
        leftMotor1Config.encoder.positionConversionFactor(1);
        rightMotor1Config.closedLoop
                .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
                .p(P)
                .i(I)
                .d(D)
                .outputRange(-1, 1);
        leftMotor1Config.closedLoop
                .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
                .p(P)
                .i(I)
                .d(D)
                .outputRange(-1, 1);

        leftMotor1.configure(leftMotor1Config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        leftMotor2.configure(leftMotor2Config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        rightMotor1.configure(rightMotor1Config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        rightMotor2.configure(rightMotor2Config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    }

    // Control Methods (3: quadratic, cubic, boost)
    // public void quadControl(double throttle, double turn) {
    // rightMotor1.set(
    // ((throttle < 0 ? -1 : 1) * throttle * throttle)
    // - turn);

    // leftMotor1.set(((throttle < 0 ? -1 : 1) * throttle * throttle) + turn);
    // }
    // public void cubicControl(double throttle, double turn) {
    // rightMotor1.set(
    // (throttle * throttle * throttle)
    // - turn);

    // leftMotor1.set((throttle * throttle * throttle) + turn);
    // }
    public void setMode(IdleMode mode) {
        leftMotor1.configure(leftMotor1Config.idleMode(mode),
            ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        leftMotor2.configure(leftMotor2Config.idleMode(mode), 
            ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        rightMotor1.configure(rightMotor1Config.idleMode(mode),
            ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        rightMotor2.configure(rightMotor2Config.idleMode(mode), 
            ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    // public void getBoost() {
    //     return this.boostModeOn;
    // }

    public void setBoost(boolean newBoost) {
        if (newBoost) this.boostModeOn=!this.boostModeOn;
        // this.boostModeOn = newBoost;
    }

    public void setBoostFactor(double boostValue) {
        if (this.boostModeOn) {
            this.boostFactor = BOOSTRATIO + ((1. - BOOSTRATIO)*boostValue);
        } else this.boostFactor = 1;
    }

    // private double applyBoost(double value) {
    //     return value * (this.boostModeOn ? 1 : 1.6);
    // }

    public void setDriveProfile(DriveTrainMode newDriveProfile) {
        this.driveProfile = newDriveProfile;
    }

    public void drive(double throttle, double turn) {
        switch (this.driveProfile) {
            case LINEAR:
                rightMotor1.set((throttle - turn)*this.boostFactor);
                leftMotor1.set((throttle + turn)*this.boostFactor);
                break;
            case QUAD:
                rightMotor1.set(((throttle < 0 ? -1 : 1) * Math.pow(throttle, 2) - turn)*this.boostFactor);
                leftMotor1.set(((throttle < 0 ? -1 : 1) * Math.pow(throttle, 2) + turn)*this.boostFactor);
                break;
            case CUBIC:
                rightMotor1.set((Math.pow(throttle, 3) - turn)*this.boostFactor);
                leftMotor1.set((Math.pow(throttle, 3) + turn)*this.boostFactor);
                break;
        }
    }

    private double distanceToEnc(double inches) {
        return inches * IN2ENC;
    }

    public void autoInit() {
        rEncoder.setPosition(0);
        lEncoder.setPosition(0);

    }

    public boolean driveAuto(double ldistance, double rdistance) {
        rAuto.setReference(distanceToEnc(rdistance), ControlType.kPosition, ClosedLoopSlot.kSlot0);
        lAuto.setReference(distanceToEnc(ldistance), ControlType.kPosition, ClosedLoopSlot.kSlot0);
        
        double err = rEncoder.getPosition() - distanceToEnc(rdistance);
        double err2 = lEncoder.getPosition() - distanceToEnc(ldistance);

        if (Math.abs(err) <= 1 && Math.abs(err2) <= 1) {
            return true;
        }
        return false;
    }

    // public boolean driveAuto(double rpmR, double rpmL) {
    //     rAuto.setReference(rpmR, ControlType.kVelocity, ClosedLoopSlot.kSlot0);
    //     lAuto.setReference(rpmL, ControlType.kVelocity, ClosedLoopSlot.kSlot0);
        
    //     double err = rEncoder.getPosition() - distanceToEnc(rpmR);
    //     double err2 = lEncoder.getPosition() - distanceToEnc(rpmL);

    //     if (Math.abs(err) <= 1 && Math.abs(err2) <= 1) {
    //         return true;
    //     }
    //     return false;
    // }
}
