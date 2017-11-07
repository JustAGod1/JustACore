package ru.justagod.justacore.gui.overlay;

import org.lwjgl.opengl.GL11;
import ru.justagod.justacore.gui.helper.DrawHelper;
import ru.justagod.justacore.gui.model.Rect;

/**
 * Created by JustAGod on 03.11.17.
 */
public class ColorCircleOverlay extends ScaledOverlay {

    private double red;
    private double green;
    private double blue;
    private double alpha;

    public ColorCircleOverlay(double x, double y, double red, double green, double blue) {
        this(x, y, red, green, blue, 1);
    }

    public ColorCircleOverlay(double x, double y, double red, double green, double blue, double alpha) {
        super(x, y);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public ColorCircleOverlay(double x, double y, double width, double height, double red, double green, double blue) {
        this(x, y, width, height, red, green, blue, 1);
    }

    public ColorCircleOverlay(double x, double y, double width, double height, double red, double green, double blue, double alpha) {
        super(x, y, width, height);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public ColorCircleOverlay(double x, double y, double width, double height, boolean scalePosition, boolean scaleSize, double red, double green, double blue) {
        this(x, y, width, height, scalePosition, scaleSize, red, green, blue, 1);
    }

    public ColorCircleOverlay(double x, double y, double width, double height, boolean scalePosition, boolean scaleSize, double red, double green, double blue, double alpha) {
        super(x, y, width, height, scalePosition, scaleSize);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    @Override
    protected void doDraw(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        pushAndTranslate(xPos, yPos);
        {
            GL11.glColor4d(red, green, blue, 0);
            //GL11.glScaled(width / 2, height / 2, 1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
            DrawHelper.drawCircle(0.5, false);
        }
        pop();
    }

    @Override
    protected void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {

    }

    @Override
    public Rect getMouseRect() {
        return new Rect(getScaledX() - getScaledWidth() / 2, getScaledY() - getScaledHeight() / 2, getScaledX() + getScaledWidth() / 2, getScaledY() + getScaledHeight() / 2);
    }
}
