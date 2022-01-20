// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import frc.robot.LED.LedManager;

/**
 * An extension of Vision that adds basic AI routines to the basic tracking
 * functions. It was extend from vision as to not break the vision class while
 * adding functionality to the underlying vision system.
 */
public class VisionAI extends Vision {
    // modules that are dependancy injected into this class via the constructor.
    private BallCollector collector;
    private LedManager ledManager;
    // used to see balls that are underneat the collector. When the ultra sonic
    // value drops below a threshold collecting starts
    private Ultrasonic CollectorUltra;
    // used to track the current heading of the robot. This is important so that the
    // robot can return to it's original heading after collecting the last ball and
    // reach the other side of the field.
    private Gyro gyro;
    // the timer used to track how long the collect phase has gone
    private Timer time = new Timer();
    // the timer used to move the robot forwards on init.
    private Timer timeStart = new Timer();
    // tracks if the robot is currently collecting a ball or not
    private boolean collecting = false;
    // tracks if the robot has seen a target
    private boolean targetPresent = false;
    // the last x value that a target was seen. If this value changes from one loop
    // to another then target present will change to true until collecting is true
    private double lastX = 0;

    public VisionAI(DriveTrain dTrain, BallCollector collector, Ultrasonic ultra, LedManager ledManager, Gyro gyro) {
        super(dTrain);
        this.collector = collector;
        this.ledManager = ledManager;
        this.gyro = gyro;
        CollectorUltra = ultra;
    }

    /**
     * this method should be called inside of autonomous init. It intializes the AI
     * to it's start up state.
     */
    public void AIInit() {
        lastX = xCenter.getDouble(0);
        collector.resetBallCount();
        gyro.init();
        time.reset();
        timeStart.reset();
        timeStart.start();
        targetPresent = false;
    }

    /**
     * This method should be run in autonomous periodic. Running this method will
     * causes the robot to preform the following sequance: 1.Drive forward for a set
     * period of time 2.Start spinning until a target is seen on the camera 3.Drive
     * up to the target until the ultrasonic sensor detects it 4.Drive forwards
     * slowly while running the collector for a set amount of time. 5.Run steps 2
     * through 4 two more times 6.reset the robot heading to it's heading on enable
     * and drive forward until disabled.
     */
    public void AIGetTarget() {
        // if the time since starting the AI is still less than a set time just drive
        // forwards.
        if (timeStart.get() < 1.5) {
            dTrain.getDiffDrive().arcadeDrive(0.4, 0);
        } else {
            // run an LED update function to communicate AI state
            ledManager.AutoUpdate(TargetExists(), xCenter.getDouble(0.5), RectSize.getDouble(0));
            // if we've filled up the collector
            if (collector.ballsFull()) {
                // reset the heading to the inital gyro state and drive forwards
                goToGyro();
            } else {
                // if we are currently in a state of collecting
                if (collecting) {
                    // collect the ball
                    Collect();
                } else {
                    // if a target is on camera
                    if (TargetExists()) {
                        // follow it
                        FollowTarget();
                        // if we are close enough to the target
                        if (readyToSucc()) {
                            // try to collect the target
                            startCollecting();
                        }
                    } else {
                        // if no target is found spin in place
                        dTrain.getDiffDrive().arcadeDrive(0, 0.5);
                    }
                }
            }
        }
    }

    public boolean TargetExists() {
        if (lastX != xCenter.getDouble(0)) {
            targetPresent = true;
        }
        lastX = xCenter.getDouble(0);
        // TODO Auto-generated method stub
        return targetPresent;
    }

    // if the ball is close enough to the robot to start running the collecor
    private boolean readyToSucc() {
        // TargetSize.getDouble(1000) < RectSize.getDouble(0)
        return (CollectorUltra.getRangeInches() < 15);
    }

    /**
     * stars the collecting routine
     */
    private void startCollecting() {
        System.out.println("------ATTEMPTING TO COLLECT-------");
        // reset the timer that tracks how long we've been collecting for because we are
        // just starting.
        time.reset();
        // start said timer
        time.start();
        // set the collecting flag to true so that the Collect method is run in the
        // periodic loop
        collecting = true;
    }

    /**
     * Collects the ball by slowly driving forwards while running the collection
     * motor for a set period of time. then backs up for a set period of time
     */
    private void Collect() {
        // if the time is over 4.5 seconds stop the collecting
        if (time.get() > 4.5) {
            collecting = false;
            targetPresent = false;
            collector.ballCollected();
            collector.moveCollector(0);
            dTrain.getDiffDrive().arcadeDrive(0, 0);
            // if the time is over 3.5 seconds stop the collector and back the robot up
        } else if (time.get() > 3.5) {
            collector.moveCollector(0);
            dTrain.getDiffDrive().arcadeDrive(-0.6, 0);
        } else {
            // if the time is less than 3.5 drive forwards slowly while running the
            // collector
            dTrain.getDiffDrive().arcadeDrive(0.3, 0);
            collector.moveCollector(-1);
        }
    }

    /**
     * make the robot return the the inital angle that was stored into the gyro
     * object at init and then drives forward
     */
    private void goToGyro() {
        // set robot to turn to face target from published xPosition from raspberry pi;
        pid.setPID(kp.getDouble(0), ki.getDouble(0), kd.getDouble(0));
        // make the angles map from 0 to 1 and loop back to zero when over 360
        double currentAngle = gyro.gyro.getAngle() % 360;
        double initAngle = gyro.initalAngle % 360;
        // System.out.println("Current angle: " + currentAngle + "\t Init Angle: " +
        // initAngle);
        // dTrain.getDiffDrive().arcadeDrive(0, -clamp(pid.calculate(currentAngle,
        // initAngle), -0.5, 0.5));
        double speed = 0;
        // if the angle difference from the start angle is less than 15 degrees drive
        // forwards, otherwise keep aproaching it.
        if (Math.abs(currentAngle - initAngle) < 15) {
            speed = 0.7;
        } else {
            speed = 0;
        }
        // use a very basic proportion controller to approach the angle and go forward
        // at the previously designated speed
        dTrain.getDiffDrive().arcadeDrive(speed, -clamp((currentAngle - initAngle), -0.7, 0.7));

    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

}
