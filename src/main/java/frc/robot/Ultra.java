/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Ultrasonic;

/**
 * Add your docs here.
 */
public class Ultra {
    private Ultrasonic frontUltra = new Ultrasonic(5, 6);
    private NetworkTableInstance ntist = NetworkTableInstance.getDefault();
    private NetworkTable ultraTable;
    private NetworkTableEntry FrontDistance;

    public Ultra(){
        ultraTable = ntist.getTable("UltraSonicSensors");
        FrontDistance = ultraTable.getEntry("FrontUltraDistance");
    }

    //publishes the ultrasonic values to the network tables. 
    public void publishUltra(){
        FrontDistance.setDouble(GetUltraMeters());
        //System.out.println(GetUltraMeters());
    }

    public double GetUltraMeters(){
        return frontUltra.getRangeMM() / 1000;
    }

}
