package frc.robot;

import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

public class AlgaeIntake {
    private SparkFlex intakeMotor;
    SparkFlexConfig configF = new SparkFlexConfig();
    private boolean startPrime = false;


    public AlgaeIntake() {
        intakeMotor = new SparkFlex(5, MotorType.kBrushless);
        configF.smartCurrentLimit(50).idleMode(IdleMode.kBrake);
        intakeMotor.configure(configF, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    public void startIntake() {
        intakeMotor.set(0.25);
        startPrime = false;
    }

    public void checkIntake() {
        if (intakeMotor.getEncoder().getVelocity() > 150 && !startPrime){
            startPrime = true;
        }
        if (intakeMotor.getEncoder().getVelocity() < 100 && startPrime) {        
            stopIntake();
        }

    }
    public void stopIntake() {
        intakeMotor.set(0);
        startPrime = false;
    }
    public void reverseIntake() {
        intakeMotor.set(-0.35);
    }
    

}
