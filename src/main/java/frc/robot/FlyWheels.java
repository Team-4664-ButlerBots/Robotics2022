// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.motorcontrol.Victor;
import edu.wpi.first.wpilibj.PIDBase.Tolerance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Should have a variable that tracks set speed. Should have an amrmed state.
 * When armed wheels run at full speed, when not at 1/4th should make each
 * flywheel go to specified speed should have method that returns if ready to
 * fire
 */
public class FlyWheels {
    private double speed = 0;
    //speed after arm
    private double TargetSpeed = 0;
    private boolean armed;
    private double rpmTolerance = 4;

    // fly wheels Right encoder screwing
    private Encoder Rencoder = new Encoder(2, 1);
    private Encoder Lencoder = new Encoder(4, 3);
    private Victor LeftShootMC = new Victor(8);
    private Victor RightShootMC = new Victor(9);

    public FlyWheels() {
        Lencoder.setDistancePerPulse(1 / 2048.0);
        Rencoder.setDistancePerPulse(1 / 2048.0);
    }

    public void setArmed(boolean armed) {
        this.armed = armed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean readyToFire() {
        double leftDiff, rightDiff;
        leftDiff = speed - Lencoder.getRate();
        rightDiff = speed - Rencoder.getRate();
        boolean ready = leftDiff < rpmTolerance && rightDiff < rpmTolerance;
        SmartDashboard.putBoolean("TARGET SPEED REACHED", ready);
        return ready;
    }

    public void updateMotors() {
        if (armed) {
            TargetSpeed = speed;
        } else {
            TargetSpeed = speed / 4;
        }

        constantSpeed();
        //encoderTrack();
    }

    private void constantSpeed(){
        TargetSpeed /= 50;
        LeftShootMC.set(-TargetSpeed * 1.4);
        RightShootMC.set(TargetSpeed);
    }

    private void encoderTrack() {
        double pMultiplier = 0.5;
        SmartDashboard.putNumber("TargetSpeed", TargetSpeed);
        double leftSpeed = clamp(((TargetSpeed - Lencoder.getRate()) * pMultiplier), 0, 1);
        LeftShootMC.set(leftSpeed);
        double rightSpeed = clamp(((TargetSpeed - Rencoder.getRate()) * pMultiplier), 0, 1);
        RightShootMC.set(rightSpeed);
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

    public void Debug() {
        readEncoder();
    }

    void readEncoder() {
        SmartDashboard.putNumber("REncodeSpeed", Rencoder.getRate());
        SmartDashboard.putNumber("LEncodeDistance", Lencoder.getDistance());
    }
}
