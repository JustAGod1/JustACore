package ru.justagod.justacore.gui.overlay;

import org.lwjgl.opengl.GL11;
import ru.justagod.justacore.gui.helper.DrawHelper;
import ru.justagod.justacore.gui.helper.MathHelper;
import ru.justagod.justacore.gui.helper.ScissorHelper;
import ru.justagod.justacore.gui.model.Color;
import ru.justagod.justacore.gui.model.Dimensions;
import ru.justagod.justacore.gui.parent.AbstractPanelOverlay;

import static org.lwjgl.opengl.GL11.glColor4d;

/**
 * Created by JustAGod on 07.11.17.
 */
public class HorizontalScrollingOverlay extends AbstractPanelOverlay {

    public static final int CARRIAGE_HEIGHT = 10;

    private Dimensions dimensions;
    private Color background;
    private double position;

    public HorizontalScrollingOverlay(double x, double y, Dimensions dimensions) {
        this(x, y, dimensions, new Color(0, 0, 0, 0));
    }

    public HorizontalScrollingOverlay(double x, double y, Dimensions dimensions, Color background) {
        super(x, y);
        this.dimensions = dimensions;
        this.background = background;
    }

    public HorizontalScrollingOverlay(double x, double y, double width, double height, Dimensions dimensions) {
        this(x, y, width, height, dimensions, new Color(0, 0, 0, 0));
    }

    public HorizontalScrollingOverlay(double x, double y, double width, double height, Dimensions dimensions, Color background) {
        super(x, y, width, height);
        this.dimensions = dimensions;
        this.background = background;
    }

    public HorizontalScrollingOverlay(double x, double y, double width, double height, boolean scalePosition, boolean scaleSize, Dimensions dimensions) {
        this(x, y, width, height, scalePosition, scaleSize, dimensions, new Color(0, 0, 0, 0));
    }

    public HorizontalScrollingOverlay(double x, double y, double width, double height, boolean scalePosition, boolean scaleSize, Dimensions dimensions, Color background) {
        super(x, y, width, height, scalePosition, scaleSize);
        this.dimensions = dimensions;
        this.background = background;
    }

    @Override
    protected synchronized void doDraw(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
        DrawHelper.bindColor(background);
        t.setTranslation(0, 0, 0);
        t.startDrawingQuads();
        t.addVertex(xPos, yPos, 0);
        t.addVertex(xPos + width, yPos, 0);
        t.addVertex(xPos + width, yPos + height, 0);
        t.addVertex(xPos, yPos + height, 0);
        t.draw();

        GL11.glTranslated(getTranslationValue(), 0, 0);
        DrawHelper.disableAlpha();

        ScissorHelper.pushTranslation();
        ScissorHelper.translate((int) getTranslationValue(), 0);
        super.doDraw(xPos, yPos, width, height, partialTick, (int) (mouseX - getTranslationValue()), mouseY, mouseInBounds);
        ScissorHelper.popTranslation();
        GL11.glTranslated(-getTranslationValue(), 0, 0);
        DrawHelper.disableAlpha();
        pushAndTranslate(xPos, yPos);
        drawCarriage();
        pop();
    }

    protected void drawCarriage() {
        double width = getCarriageWidth();
        double y = getScaledHeight() - CARRIAGE_HEIGHT - 1;
        double x = MathHelper.clampDouble((getScaledWidth() - width) * getCarriagePos() / (100 - (getScaledWidth() / dimensions.getWidth() * 100)), 0, getScaledWidth() - width);



        if (isFocused) {
            glColor4d(0.8, 0.8, 0.8, 1);
        } else {
            glColor4d(0.6, 0.6, 0.6, 1);
        }
        t.startDrawingQuads();
        {
            t.addVertex(x, y, 0);
            t.addVertex(x, y + CARRIAGE_HEIGHT, 0);
            t.addVertex(x + width, y + CARRIAGE_HEIGHT, 0);
            t.addVertex(x + width, y, 0);
        }
        t.draw();
    }


    private double getCarriageWidth() {
        // TODO: 05.11.17 Добавить зависимость каретки от внутреней ширины
        return getScaledWidth() * 0.2;
    }

    @Override
    protected synchronized void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
        DrawHelper.bindColor(background);
        t.setTranslation(0, 0, 0);
        t.startDrawingQuads();
        t.addVertex(xPos, yPos, 0);
        t.addVertex(xPos + width, yPos, 0);
        t.addVertex(xPos + width, yPos + height, 0);
        t.addVertex(xPos, yPos + height, 0);
        t.draw();
        GL11.glTranslated(getTranslationValue(), 0, 0);
        DrawHelper.disableAlpha();

        ScissorHelper.pushTranslation();
        ScissorHelper.translate((int) getTranslationValue(), 0);
        super.doDrawText(xPos, yPos, width, height, partialTick, (int) (mouseX - getTranslationValue()), mouseY, mouseInBounds);
        ScissorHelper.popTranslation();
        GL11.glTranslated(-getTranslationValue(), 0, 0);
    }

    public double getTranslationValue() {
        double translation = dimensions.getHeight() * position / 100;
        translation = MathHelper.clampDouble(translation, 0, dimensions.getHeight() - getScaledHeight());
        return -translation;
    }


    private boolean moveCarriage(int lastMouseX, int lastMouseY, int mouseX, int mouseY) {
        double carriageWidth = getCarriageWidth();
        double carriageX = getScaledX() + MathHelper.clampDouble((getScaledWidth() - width) * getCarriagePos() / (100 - (getScaledWidth() / dimensions.getWidth() * 100)), 0, getScaledWidth() - width);
        double carriageY = getScaledY() + getScaledHeight() - CARRIAGE_HEIGHT - 1;

        if (lastMouseX >= carriageX && lastMouseX <= carriageX + carriageWidth && lastMouseY >= carriageY && lastMouseY <= carriageY + CARRIAGE_HEIGHT) {
            position = (mouseX - getScaledX() - carriageWidth / 2) * (100 - (getScaledWidth() / dimensions.getWidth() * 100)) / (getScaledWidth() - carriageWidth);
            return true;
        }
        return false;
    }

    @Override
    protected boolean doMouseDrag(int lastMouseX, int lastMouseY, int mouseX, int mouseY) {
        if (moveCarriage(lastMouseX, lastMouseY, mouseX, mouseY)) return true;


        for (int i = overlays.size() - 1; i >= 0; i--) {
            ScaledOverlay overlay = overlays.get(i);


            if (overlay.onMouseDrag((int) (lastMouseX - getTranslationValue()), lastMouseY, (int) (mouseX - getTranslationValue()), mouseY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected synchronized boolean doClick(int x, int y) {
        for (int i = overlays.size() - 1; i >= 0; i--) {
            ScaledOverlay overlay = overlays.get(i);
            if (overlay.onClick((int) (x - getTranslationValue()), y)) {
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
        return scrollAmount / dimensions.getWidth() * 2;
    }

    @Override
    public double getParentHeight() {
        return getScaledHeight() - CARRIAGE_HEIGHT - 1;
    }

    @Override
    public double getParentWidth() {
        return dimensions.getWidth();
    }

    public double getCarriagePos() {
        return position;
    }
}
