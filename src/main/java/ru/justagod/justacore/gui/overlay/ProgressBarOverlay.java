package ru.justagod.justacore.gui.overlay;

import net.minecraft.util.MathHelper;

/**
 * Created by JustAGod on 21.10.17.
 */
public class ProgressBarOverlay extends ScaledOverlay {
    private float progress = 0;
    private boolean drawText = true;

    public ProgressBarOverlay(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public ProgressBarOverlay(double x, double y, double width, double height, boolean scalePosition, boolean scaleSize) {
        super(x, y, width, height, scalePosition, scaleSize);
    }

    public ProgressBarOverlay(double x, double y, float progress) {
        super(x, y);
        setProgress(progress);
    }

    public ProgressBarOverlay(double x, double y, double width, double height, float progress) {
        super(x, y, width, height);
        setProgress(progress);
    }

    public ProgressBarOverlay(double x, double y, double width, double height, boolean scalePosition, boolean scaleSize, float progress) {
        super(x, y, width, height, scalePosition, scaleSize);
        setProgress(progress);
    }

    @Override
    protected void doDraw(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {

    }

    @Override
    protected void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        pushAndTranslate(xPos, yPos);
        {
            t.startDrawingQuads();
            t.addVertexWithUV(0, 0, 0, 0, 0);
            t.addVertexWithUV(width * progress, 0, 0, 1, 0);
            t.addVertexWithUV(width * progress, height, 0, 1, 1);
            t.addVertexWithUV(0, height, 0, 0, 1);
            t.draw();
            if (isDrawText()) {
                drawCentredString(String.format("%d%%", (int) (progress * 100)), (int) (width / 2), (int) (0 + (this.height - 8) / 2), true);
            }
        }
        pop();
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = MathHelper.clamp_float(progress, 0, 1);
    }

    public boolean isDrawText() {
        return drawText;
    }

    public void setDrawText(boolean drawText) {
        this.drawText = drawText;
    }
}
