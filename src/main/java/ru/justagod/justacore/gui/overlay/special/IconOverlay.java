package ru.justagod.justacore.gui.overlay.special;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.IIcon;
import ru.justagod.justacore.gui.overlay.ScaledOverlay;

/**
 * Created by JustAGod on 25.11.17.
 */
@SuppressWarnings("unused")
public class IconOverlay extends ScaledOverlay {

    private final IIcon icon;

    public IconOverlay(double x, double y, IIcon icon) {
        super(x, y);
        this.icon = icon;
    }

    public IconOverlay(double x, double y, double width, double height, IIcon icon) {
        super(x, y, width, height);
        this.icon = icon;
    }

    public IconOverlay(double x, double y, double width, double height, boolean scalePosition, boolean scaleSize, IIcon icon) {
        super(x, y, width, height, scalePosition, scaleSize);
        this.icon = icon;
    }

    @Override
    protected void doDraw(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        pushAndTranslate(xPos, yPos);
        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();

        TextureManager manager = Minecraft.getMinecraft().renderEngine;
        manager.bindTexture(manager.getResourceLocation(1));

        t.startDrawingQuads();
        t.addVertexWithUV(0, 0, 0, minU, minV);
        t.addVertexWithUV(width, 0, 0, maxU, minV);
        t.addVertexWithUV(width, height, 0, maxU, maxV);
        t.addVertexWithUV(0, height, 0, minU, maxV);
        t.draw();
        pop();
    }

    @Override
    protected void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {

    }
}
