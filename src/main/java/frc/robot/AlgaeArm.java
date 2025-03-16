package frc.robot;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

public class AlgaeArm {
    private static final double antiGravity = 0.15;
    private static final double maxPos=Math.PI/2, minPos=0.18;
    private static final double rad2Enc = 1/0.695;
    // 8.75:1 gear ratio

    private SparkMax armPosMotor;
    SparkClosedLoopController armAuto;
    RelativeEncoder armEncoder;
    SparkMaxConfig configSM;

    private boolean stowed = false;

    public AlgaeArm() {
        armPosMotor = new SparkMax(7, MotorType.kBrushless);
        armAuto = armPosMotor.getClosedLoopController();
        armEncoder = armPosMotor.getEncoder();

        if (!stowed) armEncoder.setPosition(radToEnc(minPos));
        else armEncoder.setPosition(radToEnc(maxPos));
        
        configSM = new SparkMaxConfig();
        configSM.smartCurrentLimit(50).idleMode(IdleMode.kBrake);
        
        configSM.encoder.positionConversionFactor(1);
        armPosMotor.configure(configSM, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    private double encToRad(double enc) {
        return enc*0.695;
    }

    private double radToEnc(double rad) {
        return rad*rad2Enc;
    }

    private void deployArm() {
        if (armEncoder.getPosition()>=radToEnc(minPos)+0.2) {
            armPosMotor.set(-0.05 + (antiGravity*Math.cos(encToRad(armEncoder.getPosition()))));
        } else armPosMotor.set(0);
    }

    private void stowArm() {
        if (armEncoder.getPosition()<=radToEnc(maxPos)-0.2) {
            armPosMotor.set(0.05 + (antiGravity*Math.cos(encToRad(armEncoder.getPosition()))));
        } else armPosMotor.set(0);
    }

    public void setStowed(boolean newStow) {
        stowed = newStow;
    }

    public void runArm() {
        if (stowed) stowArm();
        else deployArm();
    }
}
