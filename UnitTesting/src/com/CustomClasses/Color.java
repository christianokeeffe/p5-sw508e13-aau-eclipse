package com.CustomClasses;

public class Color {
    private int red = 0;
    private int green = 0;
    private int blue = 0;

    public Color() { }

    public final void setColor(char input) {
        if (input == 'w') {
            red = 255;
            green = 255;
            blue = 255;
        } else if (input == 'r') {
            red = 255;
            green = 65;
            blue = 65;
        } else if (input == 'g') {
            red = 200;
            green = 250;
            blue = 200;
        } else if (input == 'b') {
            red = 160;
            green = 190;
            blue = 250;
        }
    }

    public final int getRed() {
        return red;
    }

    public final int getGreen() {
        return green;
    }

    public final int getBlue() {
        return blue;
    }
}
