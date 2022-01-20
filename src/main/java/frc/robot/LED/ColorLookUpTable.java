/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.LED;

import edu.wpi.first.wpilibj.util.Color;

public class ColorLookUpTable {
    private Color[] keys;
    private double offset = 0;
    //brightness mutliplier. 
    private double brightness = 0.5;
    private double scale = 1;

    public enum InterpolationType {
        linear, closest
    }
    private InterpolationType interType = InterpolationType.linear;

    public enum AnimationType {
        addOffset, subOffest, pulse
    }

    public ColorLookUpTable(int keyAmt) {
        keys = new Color[keyAmt];
    }

    public void setKey(int index, Color color) {
        keys[index] = color;
    }

    public void SetInterpolationType(InterpolationType type){
        interType = type;
    }

    public void setBrightness(double brightness){
        this.brightness = brightness; 
    }

    public void setScale(double scale){
        this.scale = scale;
    }

    private Color Interpolate(Color clr1, Color clr2, double g) { // ((light I'm at)/last light)) x (Number of keys)
                                                                  // casted to int = lowest key
        switch (interType) {
        case linear:
            Color c = new Color((clr2.red - clr1.red) * g + clr1.red, (clr2.green - clr1.green) * g + clr1.green,
                    (clr2.blue - clr1.blue) * g + clr1.blue);
            return c;
        case closest:
            if(g < 0.5){
                return clr1;
            }else{
                return clr2;
            }

        default:
            System.out.println("Error=====Invalid Interpolation Type======Error");
            return new Color(0, 0, 255);
        }

    }

    public Color getColor(double input) {
        input = input - (int) input; //Make sure input is just decimal. 
        input *= scale; //scale the input decimal
        input += 1000; //add to prevent negative numbers
        input += offset -(int)offset; //add the offset to the decimal
        input = input - (int) input; //make sure the input is only a decimal again to allow for looping
        double position = keys.length * input;
        Color color1 = keys[(int) position];

        Color color2;
        // check to see if the next color wraps around to index 0
        if (((int) position + 1) > keys.length - 1) {
            color2 = keys[0];
        } else {
            color2 = keys[(int) position + 1];
        }
        double distance = position - (int) position;
        Color c = Interpolate(color1, color2, distance);
        c = new Color(c.red * brightness, c.green * brightness, c.blue * brightness);
        return c;
    }

    private double brightTrack = 0;
    public void animate(AnimationType type, double speed) {
        switch (type) {
        case addOffset:
            offset += speed;
            break;
        case pulse:
            brightness = (Math.sin(brightTrack += speed) + 1) / 2;
            break;
        default:
            break;
        }
    }

    /**
     * alternates keys between two colors
     */
    public void setGrid(Color col1, Color col2) {
        for (int i = 0; i < keys.length; i++) {
            if ((i % 2) == 0)
                keys[i] = col1;
            else
                keys[i] = col2;
        }
    }

    //sets a color look up table to a rainbow. The hue is from 0 to 1 degrees. hRange describes what percent
    //is mapped to the table. 
    public void setRainbow(double start, double hRange){
        double h = start;
        for (int i = 0; i < keys.length; i++) {
            int rgb = java.awt.Color.HSBtoRGB((float)h, (float)1, (float)1);
            keys[i] = new Color(((rgb>>16)&0xFF) / 255.0, ((rgb>>8)&0xFF)/255.0, (rgb&0xFF)/255.0);
            h += hRange / keys.length;
            //System.out.println("H = " + h);
            //System.out.println("Color is: " + keys[i].red + " " + keys[i].green +" " + keys[i].blue  );
        }
    }

    public void setSolidColor(Color color){
        for (int i = 0; i < keys.length; i++) {
            keys[i] = color;
        }
    }

}