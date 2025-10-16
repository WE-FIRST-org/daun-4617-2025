/*
 * to-do
 * get gear ratio & figure out how move elevator moves per rotation ----->* 3 rotations:1 gear turn

 * trail & error PID values
 * get position values for L1, L2, and L3
 * 
 * 3 rotations:1 gear turn
 */

package frc.robot;

import static edu.wpi.first.units.Units.Volts;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
// teleop imports
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj.Timer;

public class Elevator {
    public static final double MAXPOWER = 0.4;
    public static final double MAXHEIGHT = 1; // NVM
    public static final double TIMELIMIT = 2; // TODO
    private static final double P = 1.2;
    private static final double I = 0;
    private static final double D = 0;
    private static final double L1 = 2;
    private static final double L2 = 3;
    private static final double L3 = 4;

    private TalonFX motor =  new TalonFX(20); // LEFT SIDE
    private TalonFX motorFollower  =  new TalonFX(21); // RIGHT SIDE
    private DutyCycleOut driveOut = new DutyCycleOut(0);
    private PositionVoltage posVolt = new PositionVoltage(0).withSlot(0); 

    private double targetPos = 0;
    private Timer burnoutTimer = new Timer();
    public String posName = "DOWN";
    public double tStart = 0;

    public Elevator() {
        TalonFXConfiguration config = new TalonFXConfiguration();

        config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        config.MotorOutput.NeutralMode  = NeutralModeValue.Brake;

        config.Slot0.kP = P;
        config.Slot0.kI = I;
        config.Slot0.kD = D;

        config.Voltage.withPeakForwardVoltage(Volts.of(10)).withPeakReverseVoltage(Volts.of(-10));

        motor.getConfigurator().apply(config);
        motorFollower.getConfigurator().apply(config);

        motorFollower.setControl(new Follower(motor.getDeviceID(), true));
    }

    // method needed or not needed?
    // public double inchesToEnc(double inches) {
    //     return 1;
    // }

    public void run() {
        if(Timer.getTimestamp() - tStart > TIMELIMIT) {
            posName = "DOWN";

            if(motor.getPosition().getStatus().value <= 1) {
                driveOut.Output = 0;
                motor.setControl(driveOut);
                return;
            }

            motor.setControl(posVolt.withPosition(1));
            return;
        }
        
        motor.setControl(posVolt.withPosition(targetPos));
    } 

    public void down() {
        targetPos = 0;
        driveOut.Output = 0;
        motor.setControl(driveOut);
    }

    public void L1() {
        tStart = Timer.getTimestamp();
        targetPos = L1;
        posName = "L1";
    }

    public void L2() {
        tStart = Timer.getTimestamp();
        targetPos = L2;
        posName = "L2";
    }

    public void L3() {
        tStart = Timer.getTimestamp();
        targetPos = L3;
        posName = "L3";
    }

    /**
     * DO NOT USE: RISK OF MOTOR BURNOUT

    public void dumbControl(double power) {
        power *= MAXPOWER;
        driveOut.Output = power;
        motor.setControl(driveOut);
    }
    */

    }
