package ru.justagod.justacore.gui.overlay;

import org.lwjgl.opengl.GL11;
import ru.justagod.justacore.helper.DrawHelper;

/**
 * Created by JustAGod on 02.11.17.
 */
public class ColorOverlay extends ScaledOverlay {

    private double red;
    private double green;
    private double blue;
    private double alpha;

    public ColorOverlay(double x, double y, double red, double green, double blue) {
        this(x, y, red, green, blue, 1);
    }

    public ColorOverlay(double x, double y, double red, double green, double blue, double alpha) {
        super(x, y);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public ColorOverlay(double x, double y, double width, double height, double red, double green, double blue) {
        this(x, y, width, height, red, green, blue, 1);
    }

    public ColorOverlay(double x, double y, double width, double height, double red, double green, double blue, double alpha) {
        super(x, y, width, height);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    @Override
    protected void doDraw(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
        DrawHelper.enableAlpha();
        GL11.glColor4d(red, green, blue, alpha);
        pushAndTranslate(xPos, yPos);
        {
            t.startDrawingQuads();
            t.addVertex(0, 0, 0);
            t.addVertex(width, 0, 0);
            t.addVertex(width, height, 0);
            t.addVertex(0, height, 0);
            t.draw();
        }
        pop();
        DrawHelper.disableAlpha();
    }

    @Override
    protected void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {

    }

    public void setRed(int red) {
        this.red = red;
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
}
