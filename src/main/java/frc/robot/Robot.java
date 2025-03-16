// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.autoroutines.*;

import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

// import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.XboxController;

/**
 * The methods in this cla`ss are called automatically corresponding to each
 * mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the
 * package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends TimedRobot {
  XboxController driver, operator;
  Drivetrain drivetrain;
  AlgaeIntake algaeIntake;
  AlgaeArm algaeArm;
  // int autoTask = 1;
  double throttle, steer;

  Action[] testActions;
  Sequences testSequence;

  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  
  private double joystickDeadband(double input) {
    if (input * input < 0.001) {
      return 0.0;
    }
    return input;
  }

  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  public Robot() {
    driver = new XboxController(0);
    operator = new XboxController(1);
    drivetrain = new Drivetrain();
    algaeIntake = new AlgaeIntake();
    algaeArm = new AlgaeArm();

    testActions = new Action[] {
      //new MoveAction(15, Math.PI/8, drivetrain),
      new MoveAction(25, 0, drivetrain)
    };
    testSequence = new Sequences(testActions);

    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items
   * like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different
   * autonomous modes using the dashboard. The sendable chooser code works with
   * the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the
   * chooser code and
   * uncomment the getString line to get the auto name from the text box below the
   * Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure
   * below with additional strings. If using the SendableChooser make sure to add
   * them to the
   * chooser code above as well.
   */

  @Override
  public void autonomousInit() {
    drivetrain.autoInit();
    drivetrain.setMode(IdleMode.kBrake);

    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
 

  @Override
  public void autonomousPeriodic() {
    // testSequence.run();
    testActions[0].run();
    // drivetrain.driveAuto(25, 25);
    
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    drivetrain.setMode(IdleMode.kBrake);
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    // driver code
    throttle = joystickDeadband(-(driver.getLeftY()) * Math.abs(driver.getLeftY()));
    steer = 0.25 * joystickDeadband(driver.getRightX() * Math.abs(driver.getRightX()));

    drivetrain.setBoost(driver.getRightBumperButton());
    drivetrain.drive(throttle, steer);

    // operator code
    // algae intake
    if (operator.getAButtonPressed()) {
      if (operator.getBButton()) {
        algaeIntake.reverseIntake();
      } else {
        algaeIntake.startIntake();
      }
    } else if (operator.getAButtonReleased()) {
      algaeIntake.stopIntake();
    }
    if (operator.getAButton()) {
      algaeIntake.checkIntake();
    }

    // algae arm
    if (operator.getYButtonPressed()) algaeArm.setStowed(true);
    if (operator.getXButtonPressed()) algaeArm.setStowed(false);
    algaeArm.runArm();
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {
    drivetrain.setMode(IdleMode.kCoast);
  }

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {
    drivetrain.setMode(IdleMode.kBrake);
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
  }

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {
    drivetrain.setMode(IdleMode.kBrake);
  }

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {
  }
}
