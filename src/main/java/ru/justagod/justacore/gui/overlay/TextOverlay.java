package ru.justagod.justacore.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

/**
 * Created by JustAGod on 15.10.17.
 */
public class TextOverlay extends ScaledOverlay {

    protected String text = "";

    public TextOverlay(int x, int y) {
        super(x, y);
    }

    public TextOverlay(int x, int y, String text) {
        super(x, y);
        this.text = text;
    }


    public TextOverlay localize() {
        text = I18n.format(text);
        return this;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    protected void doDraw(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {

    }

    @Override
    protected void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        drawString(text, (int)xPos, (int)yPos, false);
    }

    @Override
    public double getScaledWidth() {
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
    }

    @Override
    protected double getScaledHeight() {
        return 8;
    }
}
