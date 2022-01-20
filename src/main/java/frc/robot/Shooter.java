/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.Victor;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;

/**
 * Add your docs here.
 */
public class Shooter {
    private Victor ArmMC = new Victor(5);
    private double ArmSpeed = 0.0;
    private BallCollector collector;
    private double collectorSpeed;

    // pnematics
    private Compressor Compressor = new Compressor(0, PneumaticsModuleType.CTREPCM);
    private DoubleSolenoid StopPiston = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 7, 5);
    private DoubleSolenoid Brake = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 4, 6);

    //
    FlyWheels flyWheels = new FlyWheels();

    private ControllerManager cManager;

    public Shooter(ControllerManager cManager, BallCollector ballCollector) {
        this.cManager = cManager;
        this.collector = ballCollector;
    }

    public void OperatorControl() {
        setFlyWheelSpeed(cManager.getFlyWheelSpeed());
        moveArm(cManager.getArmInput());
        Shoot(cManager.getShootState());
        collectorSpeed = cManager.collectorInput();
    }

    // directly sets current flywheel speed
    private void setFlyWheelSpeed(double speed) {
        speed *= 50;
        flyWheels.setSpeed(speed);
        flyWheels.setArmed(cManager.getArmed());
    }

    /**
     * Controls the arm of the robot. The brake should be actuated when the arm is
     * stationary and moving down. The brake should be released when the arm is
     * moving up. There should be a 5% deadband that means if the speed is less than
     * 0.05 or more than -0.05 there should be no movement.
     * 
     * @param speed sets the speed of the motor controlling the arm's up down
     *              movement
     */
    public void moveArm(double speed) {
        if (speed < -0.05) {
            Brake.set(Value.kReverse);
        } else if (speed > 0.5) {
            Brake.set(Value.kForward);
        } else {
            Brake.set(Value.kReverse);
            speed = 0;
        }
        ArmSpeed = speed;
    }

    public void Shoot(int shootState) {
        switch (shootState) {
            case 1:
                // System.out.println("Running");
                // open piston
                StopPiston.set(Value.kReverse);
                // check if the fly wheels are spinning at the target rpm
                if (flyWheels.readyToFire() || cManager.ShootManualOverride()) {
                    collectorSpeed = 1;
                } else {
                    collectorSpeed = 0;
                }
                break;
            case 0:
                // System.out.println("Stopping");
                StopPiston.set(Value.kForward);
                break;
            case -1:
                // System.out.println("BackingUp");
                collectorSpeed = -1;
                break;
            default:
                System.out.println("Something is busted if this is running");
                break;
        }
    }

    /**
     * All motors will be set inside this method. A motor speed should never be set
     * outside it Motors will be set every frame of operation to prevent motor
     * safety timeout. variables inside the class will control motor speeds
     */
    public void UpdateMotors() {
        flyWheels.Debug();
        flyWheels.updateMotors();
        ArmMC.set(ArmSpeed);
        collector.moveCollector(collectorSpeed);
    }
}
