package frc.robot;

import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

public class CoralIntake {
    private SparkFlex coralIntakeMotor1;
    private SparkFlex coralIntakeMotor2;
    SparkFlexConfig configF = new SparkFlexConfig();

    public CoralIntake() {
        coralIntakeMotor1 = new SparkFlex(11, MotorType.kBrushless);
        coralIntakeMotor2 = new SparkFlex(12, MotorType.kBrushless);
        
        configF.smartCurrentLimit(50).idleMode(IdleMode.kBrake);
        coralIntakeMotor1.configure(configF, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        coralIntakeMotor2.configure(configF.inverted(true), ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    public void startIntake() {
        coralIntakeMotor1.set(.15);
        coralIntakeMotor2.set(.15);
    }

    public void reverseIntake() {
        coralIntakeMotor1.set(-.15);
        coralIntakeMotor2.set(-  .15);
    }
    public void stopIntake() { 
        coralIntakeMotor1.set(0);
        coralIntakeMotor2.set(0);
    }
}
