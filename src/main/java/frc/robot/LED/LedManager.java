/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.LED;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.ControllerManager;
import frc.robot.LED.ColorLookUpTable.AnimationType;
import frc.robot.LED.ColorLookUpTable.InterpolationType;

/**
 * Add your docs here. this class handles different led animation states and
 * networking
 */
public class LedManager {
    private NetworkTableInstance ntist = NetworkTableInstance.getDefault();
    private NetworkTable ledTable;
    private NetworkTableEntry nBrightness, nSpeed, nScale;

    private ColorLookUpTable test = new ColorLookUpTable(8);
    private ColorLookUpTable test2 = new ColorLookUpTable(8);
    private LEDstrip UnderBodyLED = new LEDstrip(0, 60);

    ControllerManager cManager;

    public LedManager(ControllerManager cManager) {
        NetSetup();
        this.cManager = cManager;
        test2.SetInterpolationType(InterpolationType.closest);
    }

    public void NetSetup() {
        ledTable = ntist.getTable("led");
        // instantiate the values
        nBrightness = ledTable.getEntry("brightness");
        nSpeed = ledTable.getEntry("speed");
        nScale = ledTable.getEntry("scale");
        // attempt to grab existing values from network tables otherwise use default
        // values. This populates them with data if
        // they don't already have any which makes them visible and editable.
        nBrightness.setDouble(nBrightness.getDouble(1));
        nSpeed.setDouble(nSpeed.getDouble(1));
        nScale.setDouble(nScale.getDouble(1));
    }

    public void DisabledUpdate() {
        test.setScale(1);
        // test.setSolidColor(new Color(1,1,1));
        test.SetInterpolationType(InterpolationType.linear);
        test.setGrid(Color.kFirstRed, Color.kBlueViolet);
        test.animate(AnimationType.pulse, 0.05);
        test.animate(AnimationType.addOffset, -0.001);
        UnderBodyLED.mapLookupTable(test);
    }

    public void TeleopUpdate() {
        test.setScale(0.35);
        test2.setScale(0.35);
        test.SetInterpolationType(InterpolationType.closest);
        test2.SetInterpolationType(InterpolationType.closest);
        test.setBrightness(Math.abs(cManager.getDriveInput()[1]) / 2 + 0.5);
        test2.setBrightness(Math.abs(cManager.getDriveInput()[0]) / 2 + 0.5);
        test.setGrid(Color.kBlack, Color.kBlue);
        test2.setGrid(Color.kBlack, Color.kBlue);
        test.animate(AnimationType.addOffset, cManager.getDriveInput()[1] * 0.015);
        test2.animate(AnimationType.addOffset, -cManager.getDriveInput()[0] * 0.015);

        UnderBodyLED.mapLookupTable(test, 0, 30);
        UnderBodyLED.mapLookupTable(test2, 30, 60);
    }

    public void AutoUpdate(boolean targetAquired, double xPos, double size){
        test.setScale(0.35);
        test2.setScale(0.35);
        test.SetInterpolationType(InterpolationType.closest);
        test2.SetInterpolationType(InterpolationType.closest);
        test.setBrightness(Math.abs(clamp((xPos - 1) * 3, -1, 0)));
        test2.setBrightness(Math.abs(clamp((xPos + 1) * 3, 0, 1)));
        if(targetAquired){
            test.setGrid(Color.kBlack, Color.kGreen);
            test2.setGrid(Color.kBlack, Color.kGreen);
        }else{
            test.setGrid(Color.kBlack, Color.kRed);
            test2.setGrid(Color.kBlack, Color.kRed);
        }
        test.animate(AnimationType.pulse, size * 0.0015);
        test.animate(AnimationType.addOffset, 0.01);
        test2.animate(AnimationType.pulse, size * 0.0015);
        test2.animate(AnimationType.addOffset, 0.01);

        UnderBodyLED.mapLookupTable(test, 0, 30);
        UnderBodyLED.mapLookupTable(test2, 30, 60);
    }

    public void PeriodicUpdate() {

    }

    public void TestUpdate() {
        // test.setRainbow(0,0.5);
        test.setRainbow(0.5, 1);
        ;
        test.setBrightness(nBrightness.getDouble(1));
        test.setScale(nScale.getDouble(1));
        test.animate(AnimationType.addOffset, 0.002 * nSpeed.getDouble(1));
        test2.setGrid(Color.kFirstRed, Color.kAliceBlue);
        test2.setBrightness(nBrightness.getDouble(1));
        test2.setScale(nScale.getDouble(1));
        test2.animate(AnimationType.addOffset, 0.002 * nSpeed.getDouble(1));
        UnderBodyLED.mapLookupTable(test, 30, 59);
        UnderBodyLED.mapLookupTable(test2, 0, 29);
    }

    public void testUpdate2() {
        test.setScale(1);
        // test.setSolidColor(new Color(1,1,1));
        test.SetInterpolationType(InterpolationType.linear);
        test.setGrid(Color.kYellow, Color.kBlack);
        test.animate(AnimationType.pulse, 0.05);
        test.animate(AnimationType.addOffset, -0.001);
        UnderBodyLED.mapLookupTable(test);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

}
