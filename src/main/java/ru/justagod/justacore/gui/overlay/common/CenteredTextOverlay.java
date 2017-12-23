package ru.justagod.justacore.gui.overlay.common;

/**
 * Created by JustAGod on 16.10.17.
 */
public class CenteredTextOverlay extends TextOverlay {
    public CenteredTextOverlay(int x, int y) {
        super(x, y);
    }

    public CenteredTextOverlay(int x, int y, String text) {
        super(x, y, text);
    }

    @Override
    protected void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        drawCentredString(text, (int) xPos, (int) yPos, false);
    }


}
