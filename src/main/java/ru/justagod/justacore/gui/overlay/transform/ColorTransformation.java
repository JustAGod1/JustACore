package ru.justagod.justacore.gui.overlay.transform;

import org.lwjgl.opengl.GL11;
import ru.justagod.justacore.gui.overlay.ScaledOverlay;

/**
 * Created by JustAGod on 02.11.17.
 */
public class ColorTransformation implements Transformation {
    private double red;
    private double green;
    private double blue;
    private double alpha;

    public ColorTransformation(double red, double green, double blue, double alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public double getRed() {
        return red;
    }

    public void setRed(double red) {
        this.red = red;
    }

    public double getGreen() {
        return green;
    }

    public void setGreen(double green) {
        this.green = green;
    }

    public double getBlue() {
        return blue;
    }

    public void setBlue(double blue) {
        this.blue = blue;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }


    @Override
    public void preTransform(ScaledOverlay scaledOverlay) {
        GL11.glColor4d(red, green, blue, alpha);
    }
}
