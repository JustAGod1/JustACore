package ru.justagod.justacore.gui.overlay;

/**
 * Created by JustAGod on 04.11.17.
 */
public class ColorButtonOverlay extends AbstractButtonOverlay {

    public ColorButtonOverlay(double x, double y, String text, Runnable onClick) {
        super(x, y, text, onClick);
    }

    public ColorButtonOverlay(double x, double y, double width, double height, String text, Runnable onClick) {
        super(x, y, width, height, text, onClick);
    }

    @Override
    protected void doDraw(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {

    }

    @Override
    protected void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        pushAndTranslate(xPos, yPos);
        {

        }
        pop();
    }
}
