package ru.justagod.justacore.gui.overlay;

import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import ru.justagod.justacore.gui.overlay.parent.AbstractPanelOverlay;
import ru.justagod.justacore.helper.Dimensions;

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
        pushAndTranslate(xPos, yPos);
        {
            GL11.glTranslated(0, -Math.max(0, (position - height)), 0);
            super.doDraw(xPos, yPos, width, height, partialTick, mouseX, mouseY, mouseInBounds);
        }
        pop();
    }

    @Override
    protected synchronized void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        pushAndTranslate(xPos, yPos);
        {
            GL11.glTranslated(0, -Math.max(0, (position - height)), 0);
            super.doDrawText(xPos, yPos, width, height, partialTick, mouseX, mouseY, mouseInBounds);
        }
        pop();
    }

    @Override
    protected synchronized boolean doMouseScroll(double relativeMouseX, double relativeMouseY, int scrollAmount) {
        position += calculateScrollMotion(-scrollAmount);
        position = MathHelper.clamp_double(position, 0, innerDimensions.getHeight());
        return true;
    }

    private double calculateScrollMotion(int scrollAmount) {
        return scrollAmount / innerDimensions.getHeight();
    }

    @Override
    public double getParentHeight() {
        return innerDimensions.getHeight();
    }
}
