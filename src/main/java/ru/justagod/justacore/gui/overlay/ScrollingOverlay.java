package ru.justagod.justacore.gui.overlay;

import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import ru.justagod.justacore.gui.overlay.parent.AbstractPanelOverlay;
import ru.justagod.justacore.helper.Dimensions;
import ru.justagod.justacore.helper.DrawHelper;

import static java.lang.Math.max;

/**
 * Created by JustAGod on 04.11.17.
 */
public class ScrollingOverlay extends AbstractPanelOverlay {

    private Dimensions innerDimensions;
    private double position;


    public ScrollingOverlay(double x, double y, Dimensions innerDimensions) {
        super(x, y);
        this.innerDimensions = innerDimensions;
        setDoScissor(true);
    }

    public ScrollingOverlay(double x, double y, double width, double height, Dimensions innerDimensions) {
        super(x, y, width, height);
        this.innerDimensions = innerDimensions;
        setDoScissor(true);
    }

    public ScrollingOverlay(double x, double y, double width, double height, boolean scalePosition, boolean scaleSize, Dimensions innerDimensions) {
        super(x, y, width, height, scalePosition, scaleSize);
        this.innerDimensions = innerDimensions;
        setDoScissor(true);
    }

    @Override
    protected synchronized void doDraw(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        DrawHelper.enableAlpha();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
        GL11.glColor4d(0.1, 0.1, 0.1, 0.3);
        t.setTranslation(0, 0, 0);
        t.startDrawingQuads();
        t.addVertex(xPos, yPos, 0);
        t.addVertex(xPos + width, yPos, 0);
        t.addVertex(xPos + width, yPos + height, 0);
        t.addVertex(xPos, yPos + height, 0);
        t.draw();
        GL11.glTranslated(0, getTranslationValue(), 0);

        super.doDraw(xPos, yPos, width, height, partialTick, mouseX, (int) (mouseY - getTranslationValue()), mouseInBounds);
        GL11.glTranslated(0, -getTranslationValue(), 0);
    }

    @Override
    protected synchronized void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        DrawHelper.enableAlpha();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
        GL11.glColor4d(0.1, 0.1, 0.1, 0.3);
        t.setTranslation(0, 0, 0);
        t.startDrawingQuads();
        t.addVertex(xPos, yPos, 0);
        t.addVertex(xPos + width, yPos, 0);
        t.addVertex(xPos + width, yPos + height, 0);
        t.addVertex(xPos, yPos + height, 0);
        t.draw();
        GL11.glTranslated(0, getTranslationValue(), 0);

        super.doDrawText(xPos, yPos, width, height, partialTick, mouseX, (int) (mouseY - getTranslationValue()), mouseInBounds);
        GL11.glTranslated(0, -getTranslationValue(), 0);
    }

    public double getTranslationValue() {
        return -max(0, (position - height));
    }


    @Override
    protected boolean doMouseDrag(int lastMouseX, int lastMouseY, int mouseX, int mouseY) {


        for (int i = overlays.size() - 1; i >= 0; i--) {
            ScaledOverlay overlay = overlays.get(i);


            if (overlay.onMouseDrag(lastMouseX, (int) (lastMouseY - getTranslationValue()), mouseX, (int) (mouseY - getTranslationValue()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected synchronized boolean doClick(int x, int y) {
        for (int i = overlays.size() - 1; i >= 0; i--) {
            ScaledOverlay overlay = overlays.get(i);
            if (overlay.onClick(x, (int) (y - getTranslationValue()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected synchronized boolean doMouseScroll(double relativeMouseX, double relativeMouseY, int scrollAmount) {

        position += calculateScrollMotion(-scrollAmount);
        position = MathHelper.clamp_double(position, 0, innerDimensions.getHeight());
        return true;
    }

    private double calculateScrollMotion(int scrollAmount) {
        return scrollAmount / 50.0;
    }

    @Override
    public double getParentHeight() {
        return innerDimensions.getHeight();
    }
}
