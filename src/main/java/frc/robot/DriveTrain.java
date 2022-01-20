/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.Victor;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * Add your docs here.
 */
public class DriveTrain {
    private Victor m_leftMC = new Victor(6);
    private Victor m_rightMC = new Victor(7);
    private DifferentialDrive m_drive = new DifferentialDrive(m_leftMC, m_rightMC);
    private ControllerManager cManager;

    public DriveTrain(ControllerManager cManager) {
        this.cManager = cManager;
        m_rightMC.setInverted(true);  
    }

    public void operatorDrive() {
        if (cManager.TargetingMode()) {
            m_drive.arcadeDrive(0, cManager.getTargetingDriveInput());
        }else{
            double[] input = cManager.getDriveInput();
            if (cManager.speedToggle())
                m_drive.tankDrive(-input[1] / 2, -input[0] / 2);
            else
                m_drive.tankDrive(-input[1], -input[0]);
        }
    }

    public void operatorJoystickDrive(){
        double[] input = cManager.getJoystickDriveInput();
        m_drive.arcadeDrive(-input[0], input[1]);
        
    }

    public DifferentialDrive getDiffDrive() {
        return m_drive;
    }

}
