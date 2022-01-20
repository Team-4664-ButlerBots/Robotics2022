/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.LED.LedManager;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  /*
   * These classes can for the most part be put into two categories. Modules and
   * drivers. Modules are resources used by drivers, they are anything that
   * declare roboRio IO ports. For an example an ultrasonic sensor would be a
   * module because it requires the use of Digital IO ports on the rio. This is
   * important because you cannont have an IO port used by multiple resources.
   * What we need to do then is declare a module once and share it with any
   * drivers that wish to use it. For example Drive train is a module shared with
   * every class that controls the robots drive wheels.
   * 
   * Of course there are a few exeptions to the module driver approach in this
   * codebase but for the most part it is a helpful way to understand how it's
   * organized.
   */
  // Modules:
  ControllerManager cManager = new ControllerManager();
  LedManager ledManager = new LedManager(cManager);
  DriveTrain dTrain = new DriveTrain(cManager);
  BallCollector ballCollector = new BallCollector();
  Gyro gyro = new Gyro();
  Ultrasonic collectorUltra = new Ultrasonic(6, 5);
  LimitSwitch frontSwitch = new LimitSwitch(8);
  // Drivers:
  Shooter shooter = new Shooter(cManager, ballCollector);
  Vision visionSystem = new Vision(dTrain);
  VisionAI visionAI = new VisionAI(dTrain, ballCollector, collectorUltra, ledManager, gyro);

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    Ultrasonic.setAutomaticMode(true);
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  @Override
  public void disabledPeriodic() {
    // LED manager contains update methods that will animate leds in a certain
    // fasion when called in a loop. When this one is called in a loop it animates a
    // pulsing puple and red grid that translates forwards. 
    ledManager.DisabledUpdate();
  }

  // Ultra ultra = new Ultra();
  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // ultra.publishUltra();
    SmartDashboard.putNumber("Gyro", gyro.gyro.getAngle());
    SmartDashboard.putNumber("UltraSOnic", collectorUltra.getRangeInches());
    ledManager.PeriodicUpdate();
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString line to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure below with additional strings. If using the SendableChooser
   * make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    visionAI.AIInit();
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    visionAI.AIGetTarget();
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

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    if (cManager.lookAtBall()) {
      visionSystem.LookAtTarget();
    } else if (cManager.followBall()) {
      visionSystem.FollowTarget();
    } else {
      dTrain.operatorDrive();
    }
    shooter.OperatorControl();
    ledManager.TeleopUpdate();
    // shooter.UpdateMotors();
    shooter.UpdateMotors();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    dTrain.operatorJoystickDrive();
    ledManager.testUpdate2();
  }

}
