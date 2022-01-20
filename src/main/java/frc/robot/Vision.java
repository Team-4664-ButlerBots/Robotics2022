/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.controller.*;

/**
 * This class handles the vision data computed on the raspberry pi. It pulls target position data from the
 * network and moves the wheels on the robot to aim at and drive to a target.  
 */
public class Vision {
    //drive train module
    protected DriveTrain dTrain;
    //the network table instance that all of our tables are stored in. 
    private NetworkTableInstance ntist = NetworkTableInstance.getDefault();
    private NetworkTable visionTable;
    protected NetworkTableEntry xCenter, yCenter, RectSize, noTarget, kp, ki, kd, TargetSize, followSpeed;
    PIDController pid = new PIDController(0, 0, 0);

    // interval between calls to track target. This
    private double pidPeriod = 20;

    public Vision(DriveTrain dTrain) {
        this.dTrain = dTrain;
        visionTable = ntist.getTable("vision");
        xCenter = visionTable.getEntry("Xposition");
        yCenter = visionTable.getEntry("Yposition");
        RectSize = visionTable.getEntry("Size");
        noTarget = visionTable.getEntry("NoTarget");
        TargetSize = visionTable.getEntry("TargetSize");
        followSpeed = visionTable.getEntry("followSpeed");

        // instantiate the values
        kp = visionTable.getEntry("kp");
        ki = visionTable.getEntry("ki");
        kd = visionTable.getEntry("kd");
        // attempt to grab existing values from network tables otherwise use default of
        // 0
        kp.setDouble(kp.getDouble(0.588778189545199));
        ki.setDouble(ki.getDouble(0.336177474402731));
        kd.setDouble(kd.getDouble(0.136518781502499));
        pid.setPID(kp.getDouble(0), ki.getDouble(0), kd.getDouble(0));
        TargetSize.setDouble(TargetSize.getDouble(305));
        followSpeed.setDouble(followSpeed.getDouble(0.07));
    }

    // used to track time since tracking is lost
    long time;

    public void LookAtTarget() {
        if (!noTarget.getBoolean(true)) {
            // set robot to turn to face target from published xPosition from raspberry pi;
            pid.setPID(kp.getDouble(0), ki.getDouble(0), kd.getDouble(0));
            dTrain.getDiffDrive().arcadeDrive(0, -pid.calculate((xCenter.getDouble(0) - 0.5) * 2, 0));
        } else {
            dTrain.getDiffDrive().tankDrive(0, 0);
        }
    }

    public void FollowTarget() {
        if (!noTarget.getBoolean(true)) {
            double speed = (RectSize.getDouble(0) - TargetSize.getDouble(0)) * followSpeed.getDouble(0);
            speed = -clamp(speed, -0.55, 0.55);
            // set robot to turn to face target from published xPosition from raspberry pi;
            pid.setPID(kp.getDouble(0), ki.getDouble(0), kd.getDouble(0));
            dTrain.getDiffDrive().arcadeDrive(speed, -pid.calculate((xCenter.getDouble(0) - 0.5) * 2, 0));
        } else {
            dTrain.getDiffDrive().tankDrive(0, 0);
        }
    }

    public boolean TargetExists() {
        return !noTarget.getBoolean(true);
    }

    public double getDistance() {
        return 100 / RectSize.getDouble(100);
    }

    private double clamp(double in, double low, double high) {
        if (in < low) {
            return low;
        } else if (in > high) {
            return high;
        } else {
            return in;
        }
    }

}
