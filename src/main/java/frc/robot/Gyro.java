// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;

/** Add your docs here. */
public class Gyro {
    public ADXRS450_Gyro gyro = new ADXRS450_Gyro();
    public double initalAngle = 0;

    public void init(){
        initalAngle = gyro.getAngle();
    }
}
