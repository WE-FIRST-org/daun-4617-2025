package frc.robot;

// teleop imports
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj.Timer;

// auto imports
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;

public class Drivetrain {
    public static final double RATIO = 8.45;
    public static final double WHEEL = 6 * Math.PI;
    public static final double IN2ENC = RATIO / WHEEL;
    public static final double P = 0.1;
    public static final double I = 0;
    public static final double D = 0.01;

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

    DriveTrainMode driveProfile = DriveTrainMode.LINEAR;
    boolean boost = false;

    public Drivetrain() {
        // Right Motors
        rightMotor1 = new SparkMax(2, MotorType.kBrushless);
        rightMotor2 = new SparkMax(4, MotorType.kBrushless);
        // Left Motors
        leftMotor1 = new SparkMax(1, MotorType.kBrushless);
        leftMotor2 = new SparkMax(3, MotorType.kBrushless);

        // auto motors
        rAuto = rightMotor1.getClosedLoopController();
        lAuto = leftMotor1.getClosedLoopController();
        rEncoder = rightMotor1.getEncoder();
        lEncoder = leftMotor1.getEncoder();

        globalConfig.smartCurrentLimit(50).idleMode(IdleMode.kBrake);
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

    public void setBoost(boolean newBoost) {
        this.boost = newBoost;
    }

    private double applyBoost(double value) {
        return value * (this.boost ? 1 : 0.6);
    }

    public void setDriveProfile(DriveTrainMode newDriveProfile) {
        this.driveProfile = newDriveProfile;
    }

    public void drive(double throttle, double turn) {
        switch (this.driveProfile) {
            case LINEAR:
                rightMotor1.set(applyBoost(throttle) - turn);
                leftMotor1.set(applyBoost(throttle) + turn);
                break;
            case QUAD:
                rightMotor1.set(applyBoost((throttle < 0 ? -1 : 1) * Math.pow(throttle, 2)) - turn);
                leftMotor1.set(applyBoost((throttle < 0 ? -1 : 1) * Math.pow(throttle, 2)) + turn);
                break;
            case CUBIC:
                rightMotor1.set(applyBoost(Math.pow(throttle, 3)) - turn);
                leftMotor1.set(applyBoost(Math.pow(throttle, 3)) + turn);
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
}
