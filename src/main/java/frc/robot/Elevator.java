/*
 * to-do
 * get gear ratio & figure out how move elevator moves per rotation ----->* 3 rotations:1 gear turn

 * trail & error PID values
 * get position values for L1, L2, and L3
 * 
 * 3 rotations:1 gear turn
 */

package frc.robot;

// teleop imports
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

// auto imports
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor; // needed or not needed?

public class Elevator {
    // private static final double antiGravity = 0.5; // needed or not needed?
    private static final double P = 0.05;
    private static final double I = 0;
    private static final double D = 0;

    String currentLevel = "stowed";

    private SparkMax elevatorMotor1;
    private SparkMax elevatorMotor2;
    SparkMaxConfig configM = new SparkMaxConfig();
    RelativeEncoder elevatorEncoder1, elevatorEncoder2;
    SparkClosedLoopController elevatorAuto;

    public Elevator() {
        elevatorMotor1 = new SparkMax(10, MotorType.kBrushless);
        elevatorMotor2 = new SparkMax(11, MotorType.kBrushless);

        elevatorEncoder1 = elevatorMotor1.getEncoder();
        elevatorEncoder2 = elevatorMotor2.getEncoder();
        elevatorAuto = elevatorMotor1.getClosedLoopController();

        configM.smartCurrentLimit(50).idleMode(IdleMode.kBrake);
    
        configM.encoder.positionConversionFactor(1);
        configM.closedLoop
                .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
                .p(P)
                .i(I)
                .d(D)
                .outputRange(-1, 1);

        elevatorMotor1.configure(configM, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        elevatorMotor2.configure(configM.follow(elevatorMotor1).inverted(true), ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    // method needed or not needed?
    // public double inchesToEnc(double inches) {
    //     return 1;
    // }

    public double getEncPos() {
        return elevatorEncoder1.getPosition();
    }
    
    public void stowElevator() {
        elevatorAuto.setReference(0, ControlType.kPosition, ClosedLoopSlot.kSlot0);
        currentLevel = "stowed";
    }

    public void L1() {
        elevatorAuto.setReference(1, ControlType.kPosition, ClosedLoopSlot.kSlot0);
        currentLevel = "L1";
    }

    public void L2() {
        elevatorAuto.setReference(2, ControlType.kPosition, ClosedLoopSlot.kSlot0);
        currentLevel = "L2";
    }

    public void L3() {
        elevatorAuto.setReference(3, ControlType.kPosition, ClosedLoopSlot.kSlot0);
        currentLevel = "L3";
    }
}
