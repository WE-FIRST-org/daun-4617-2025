package frc.robot;

import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;;

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
        intakeMotor.set(0.3);
        startPrime = false;
    }

    public void checkIntake() {
        if (intakeMotor.getEncoder().getVelocity() > 200 && !startPrime){
            startPrime = true;
        }
        if (intakeMotor.getEncoder().getVelocity() < 200 && startPrime) {        
            stopIntake();
        }

    }
    public void stopIntake() {
        intakeMotor.set(0);
    }
    public void reverseIntake() {
        intakeMotor.set(-0.3);
    }
    

}
