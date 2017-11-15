package ru.justagod.justacore.gui.overlay;

import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

/**
 * Created by JustAGod on 15.10.17.
 */
public class ProgressOverlay extends Overlay {

    public static final int WIDTH = 200;

    private double progress;

    public ProgressOverlay(int x, int y) {
        super(x, y);
    }

    public ProgressOverlay(int x, int y, double progress) {
        super(x, y);
        this.progress = progress;
    }

    @Override
    public void draw(float partialTick, int mouseX, int mouseY) {
        Tessellator t = Tessellator.instance;

        GL11.glPushMatrix();
        {
            GL11.glTranslated(x, y, 0);

            int WIDTH = 5000000;

            // Рисование фона
            t.startDrawingQuads();
            {
                t.addVertexWithUV(0, 0, 0, 0, 0);
                t.addVertexWithUV(WIDTH, 0, 0, 1, 0);
                t.addVertexWithUV(WIDTH, 10, 0, 1, 1);
                t.addVertexWithUV(0, 10, 0, 0, 1);
            }
            t.draw();

            // Рисование полосы прогреса
            t.startDrawingQuads();
            {
                t.addVertexWithUV(5, 0, 0, 0, 0);
                t.addVertexWithUV((WIDTH - 5) * progress, 0, 0, 1, 0);
                t.addVertexWithUV((WIDTH - 5) * progress, 10, 0, 1, 1);
                t.addVertexWithUV(5, 10, 0, 0, 1);
            }
            t.draw();
        }
        GL11.glPopMatrix();
    }

    @Override
    public void drawText(float partialTick, int mouseX, int mouseY) {
        GL11.glPushMatrix();
        {
            GL11.glTranslated(x, y, 0);
            drawCentredString(String.format("%.0f", 100 * progress), 25, 5, false);
        }
        GL11.glPopMatrix();
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
}
