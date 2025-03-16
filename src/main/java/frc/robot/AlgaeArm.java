package frc.robot;

import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;

public class AlgaeArm {
    public static final double P = 0.1;
    public static final double I = 0;
    public static final double D = 0.01;
    public static final double gravityForce = 9.81;
    public static final double maxPos=1, minPos=-1;


    private SparkMax armPosMotor;
    SparkClosedLoopController armAuto;
    RelativeEncoder armEncoder;
    SparkMaxConfig configSM = new SparkMaxConfig();

    public AlgaeArm() {
        configSM.smartCurrentLimit(50).idleMode(IdleMode.kBrake);
        armPosMotor.configure(configSM, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        armPosMotor = new SparkMax(9, MotorType.kBrushless);
        armAuto = armPosMotor.getClosedLoopController();
        armEncoder = armPosMotor.getEncoder();

        configSM.encoder.positionConversionFactor(0);
        configSM.closedLoop.feedbackSensor(FeedbackSensor.kPrimaryEncoder)
        .p(P)
        .i(I)
        .d(D)
        .outputRange(-1, 1)
        ;

        armPosMotor.configure(configSM, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    public void movePID(double target) {
        armAuto.setReference(target, ControlType.kPosition, ClosedLoopSlot.kSlot0);
    }

    public void deployArm(double input) {
        // armPosMotor.set(input*0.6);
    }

    public void stowArm(double input) {
        // double slowStow = armEncoder.getPosition() - 
    }

    public void moveArm(double input) {
        // remember! getPostion() returns num of motor rotations, not the angle.
        if (armEncoder.getPosition()>=maxPos) {
            if (input<=0) armPosMotor.set(input);
        } else if (armEncoder.getPosition()<=minPos) {
            if (input>=0) armPosMotor.set(input);
        } else {
            armPosMotor.set(input + (gravityForce*Math.cos(armEncoder.getPosition())));
        }
    }
}
