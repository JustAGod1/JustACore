package ru.justagod.justacore.gui.overlay.special;

import ru.justagod.justacore.gui.helper.DrawHelper;
import ru.justagod.justacore.gui.model.Color;
import ru.justagod.justacore.gui.model.Vector;
import ru.justagod.justacore.gui.overlay.ScaledOverlay;
import ru.justagod.justacore.gui.overlay.special.model.Model;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by JustAGod on 15.11.17.
 */
public class ModelOverlay extends ScaledOverlay {

    private Model model;

    private double xRotation = 0;
    private double yRotation = 0;

    public ModelOverlay(double x, double y,  Model model) {
        super(x, y);
        assert model != null;
        this.model = model;
        setDoScissor(true);
    }

    public ModelOverlay(double x, double y, double width, double height, Model model) {
        super(x, y, width, height);
        assert model != null;
        this.model = model;
        setDoScissor(true);
    }

    public ModelOverlay(double x, double y, double width, double height, boolean scalePosition, boolean scaleSize,  Model model) {
        super(x, y, width, height, scalePosition, scaleSize);
        assert model != null;
        this.model = model;
        setDoScissor(true);
    }

    @Override
    protected void doDraw(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        glPushMatrix();
        glTranslated(xPos, yPos, 100);

        DrawHelper.drawRect(width, height, new Color(0.5, 0.5, 0.5, 0.3));


        glTranslated(width / 2 - model.getWidth() / 2, height / 2 -model.getHeight() / 2 + 10, 0);
        glScaled((width - 20) / model.getWidth(), (height - 20) / model.getHeight(), 1);
        glRotated(1, xRotation, yRotation, 0);

        model.draw();

        glPopMatrix();
    }

    @Override
    protected boolean doMouseDrag(int lastMouseX, int lastMouseY, int mouseX, int mouseY) {
        Vector last = new Vector(lastMouseX, lastMouseY);
        Vector real = new Vector(mouseX, mouseY);

        Vector delta = real.subtract(last);
        delta = delta.multiply(0.1);

        xRotation += delta.getX();
        yRotation -= delta.getY();


        return true;
    }

    @Override
    protected void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {

    }

    public static void main(String[] args) {
        assert null != null;
    }

}
