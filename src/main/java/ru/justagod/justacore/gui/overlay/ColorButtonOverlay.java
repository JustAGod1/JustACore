package ru.justagod.justacore.gui.overlay;

import org.lwjgl.opengl.GL11;
import ru.justagod.justacore.helper.DrawHelper;

/**
 * Created by JustAGod on 04.11.17.
 */
public class ColorButtonOverlay extends AbstractButtonOverlay {

    private double red;
    private double green;
    private double blue;
    private double alpha;

    public ColorButtonOverlay(double x, double y, String text, Runnable onClick, double red, double green, double blue) {
        this(x, y, text, onClick, red, green, blue, 1);
    }

    public ColorButtonOverlay(double x, double y, String text, Runnable onClick, double red, double green, double blue, double alpha) {
        super(x, y, text, onClick);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public ColorButtonOverlay(double x, double y, double width, double height, String text, Runnable onClick, double red, double green, double blue) {
        this(x, y, width, height, text, onClick, red, green, blue, 1);
    }

    public ColorButtonOverlay(double x, double y, double width, double height, String text, Runnable onClick, double red, double green, double blue, double alpha) {
        super(x, y, width, height, text, onClick);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    @Override
    protected void doDraw(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {

    }

    @Override
    protected void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        pushAndTranslate(xPos, yPos);
        {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
            GL11.glColor4d(red, green, blue, alpha);
            DrawHelper.enableAlpha();
            t.startDrawingQuads();
            t.addVertex(0, 0, 0);
            t.addVertex(width, 0, 0);
            t.addVertex(width, height, 0);
            t.addVertex(0, height, 0);
            t.draw();

            drawCentredString(text, (int) (width / 2), 0, false);
            DrawHelper.disableAlpha();
        }
        pop();
    }
}
