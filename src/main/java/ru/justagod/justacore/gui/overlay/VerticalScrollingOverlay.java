package ru.justagod.justacore.gui.overlay;


import org.lwjgl.opengl.GL11;
import ru.justagod.justacore.gui.parent.AbstractPanelOverlay;
import ru.justagod.justacore.gui.model.Dimensions;
import ru.justagod.justacore.gui.helper.DrawHelper;
import ru.justagod.justacore.gui.helper.MathHelper;
import ru.justagod.justacore.gui.helper.ScissorHelper;

import static org.lwjgl.opengl.GL11.glColor4d;

/**
 * Created by JustAGod on 04.11.17.
 */
public class VerticalScrollingOverlay extends AbstractPanelOverlay {

    public static final int CARRIAGE_WIDTH = 10;

    private Dimensions innerDimensions;
    private double position;


    public VerticalScrollingOverlay(double x, double y, Dimensions innerDimensions) {
        super(x, y);
        this.innerDimensions = innerDimensions;
    }

    public VerticalScrollingOverlay(double x, double y, double width, double height, Dimensions innerDimensions) {
        super(x, y, width, height);
        this.innerDimensions = innerDimensions;
        setDoScissor(true);
    }

    public VerticalScrollingOverlay(double x, double y, double width, double height, boolean scalePosition, boolean scaleSize, Dimensions innerDimensions) {
        super(x, y, width, height, scalePosition, scaleSize);
        this.innerDimensions = innerDimensions;
    }

    @Override
    protected synchronized void doDraw(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        DrawHelper.enableAlpha();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
        glColor4d(1, 0.1, 0.1, 0.3);
        t.setTranslation(0, 0, 0);
        t.startDrawingQuads();
        t.addVertex(xPos, yPos, 0);
        t.addVertex(xPos + width, yPos, 0);
        t.addVertex(xPos + width, yPos + height, 0);
        t.addVertex(xPos, yPos + height, 0);
        t.draw();

        GL11.glTranslated(0, getTranslationValue(), 0);
        DrawHelper.disableAlpha();

        ScissorHelper.pushTranslation();
        ScissorHelper.translate(0, (int) getTranslationValue());
        super.doDraw(xPos, yPos, width, height, partialTick, mouseX, (int) (mouseY - getTranslationValue()), mouseInBounds);
        ScissorHelper.popTranslation();
        GL11.glTranslated(0, -getTranslationValue(), 0);
        DrawHelper.disableAlpha();
        pushAndTranslate(xPos, yPos);
        drawCarriage();
        pop();
    }

    protected void drawCarriage() {
        double height = getCarriageHeight();
        double x = getScaledWidth() - CARRIAGE_WIDTH - 1;
        double y = MathHelper.clampDouble((getScaledHeight() - height) * getCarriagePos() / (100 - getScaledHeight() / innerDimensions.getHeight() * 100), 0, getScaledHeight() - height);



        if (isFocused) {
            glColor4d(0.8, 0.8, 0.8, 1);
        } else {
            glColor4d(0.6, 0.6, 0.6, 1);
        }
        t.startDrawingQuads();
        {
            t.addVertex(x, y, 0);
            t.addVertex(x, y + getCarriageHeight(), 0);
            t.addVertex(x + CARRIAGE_WIDTH, y + getCarriageHeight(), 0);
            t.addVertex(x + CARRIAGE_WIDTH, y, 0);
        }
        t.draw();
    }


    private double getCarriageHeight() {
        // TODO: 05.11.17 Добавить зависимость каретки от внутреней ширины
        return getScaledHeight() * 0.2;
    }

    @Override
    protected synchronized void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        DrawHelper.enableAlpha();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
        glColor4d(1, 0.1, 0.1, 0.3);
        t.setTranslation(0, 0, 0);
        t.startDrawingQuads();
        t.addVertex(xPos, yPos, 0);
        t.addVertex(xPos + width, yPos, 0);
        t.addVertex(xPos + width, yPos + height, 0);
        t.addVertex(xPos, yPos + height, 0);
        t.draw();
        GL11.glTranslated(0, getTranslationValue(), 0);
        DrawHelper.disableAlpha();

        ScissorHelper.pushTranslation();
        ScissorHelper.translate(0, (int) getTranslationValue());
        super.doDrawText(xPos, yPos, width, height, partialTick, mouseX, (int) (mouseY - getTranslationValue()), mouseInBounds);
        ScissorHelper.popTranslation();
        GL11.glTranslated(0, -getTranslationValue(), 0);
    }

    public double getTranslationValue() {
        double translation = innerDimensions.getHeight() * position / 100;
        translation = MathHelper.clampDouble(translation, 0, innerDimensions.getHeight() - getScaledHeight());
        return -translation;
    }


    private boolean moveCarriage(int lastMouseX, int lastMouseY, int mouseX, int mouseY) {
        double carriageHeight = getCarriageHeight();
        double carriageX = getScaledX() + getScaledWidth() - CARRIAGE_WIDTH - 1;
        double carriageY = getScaledY() + MathHelper.clampDouble((getScaledY() - carriageHeight) * getCarriagePos() / (100 - getScaledHeight() / innerDimensions.getHeight() * 100), 0, getScaledHeight() - carriageHeight);

        if (lastMouseX <= carriageX + CARRIAGE_WIDTH && lastMouseX >= carriageX && lastMouseY <= carriageY + carriageHeight && lastMouseY >= carriageY) {
            position = (mouseY - getScaledY() - carriageHeight / 2) * (100 - getScaledHeight() / innerDimensions.getHeight() * 100) / (getScaledY() - carriageHeight);
            return true;
        }
        return false;
    }

    @Override
    protected boolean doMouseDrag(int lastMouseX, int lastMouseY, int mouseX, int mouseY) {
        if (moveCarriage(lastMouseX, lastMouseY, mouseX, mouseY)) return true;


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
    protected synchronized boolean doMouseScroll(double mouseX, double mouseY, int scrollAmount) {
        if (super.doMouseScroll(mouseX, mouseY - getTranslationValue(), scrollAmount)) return true;
        position += calculateScrollMotion(-scrollAmount);
        position = MathHelper.clampDouble(position, 0, 100);
        return true;
    }

    private double calculateScrollMotion(int scrollAmount) {
        return scrollAmount / innerDimensions.getHeight() * 2;
    }

    @Override
    public double getParentHeight() {
        return innerDimensions.getHeight();
    }

    @Override
    public double getParentWidth() {
        return getScaledWidth() - CARRIAGE_WIDTH - 1;
    }

    public double getCarriagePos() {
        return position;
    }
}
