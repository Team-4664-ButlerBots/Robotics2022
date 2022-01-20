// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.motorcontrol.Spark;

/** This class will handle tracking current state of balls inside Collector
 * it will stop the collector from moving if the balls are bunched up. Legally
 * our robot can also only hold 3 balls at a time, so this class will ensure we can't
 * pick up more than that. 
*/
public class BallCollector {
    private Spark CollectorMC = new Spark(4);
    //switch that is positioned at stopper piston. Pressed when collector will jam
    private LimitSwitch jamSwitch = new LimitSwitch(9);
    //switch at input, pressed when a new ball is fed into the collector

    //tracks how many balls are currenty in the robot
    private int currentBallCount = 0;

    
    //manual overide move collector.
    public void moveCollector(double speed){
        //if we are moving the belt forward
        if(speed > 0){
            //check if the balls won't jam
            if(!ballsWillJam()){
                //if the balls don't jam then proceed with moving the collector forward
                CollectorMC.setSpeed(speed);
            }else{
                //otherwise stop the collector
                CollectorMC.setSpeed(0);
            }
        }else{
            CollectorMC.setSpeed(speed);
        }
        
    }

    public void resetBallCount(){
        currentBallCount = 0;
    }

    public boolean ballsFull(){
        return currentBallCount > 2;
    }

    public void ballCollected(){
        currentBallCount++;
    }

    public int getBallcount(){
        return currentBallCount;
    }

    /**
     * this is run when a ball is shot, it decrements the ball count by one and handles any other post-shooting code
     */
    public void ballShot(){
        currentBallCount--;
    }

    /**
     * returns true when two balls are up against the shooting mechanism. 
     * in this case the belt cannont move forward without first shooting. 
     * @return
     */
    public boolean ballsWillJam(){
        //return jamSwitch.getSwitchState();
        return false;
    }

}
