package ru.justagod.justacore.gui.overlay.special;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import ru.justagod.justacore.gui.helper.DrawHelper;
import ru.justagod.justacore.gui.overlay.ScaledOverlay;

import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by JustAGod on 17.10.17.
 */
public class BufferedImageOverlay extends ScaledOverlay {

    protected final BufferedImage image;
    protected int textureId = -1;


    public BufferedImageOverlay(double x, double y, double width, double height, BufferedImage image) {
        super(x, y, width, height);
        this.image = image;
    }



    private void bindImage() {
        if (textureId == -1) {
            textureId = DrawHelper.loadTexture(image);
        }
        GL11.glBindTexture(GL_TEXTURE_2D, textureId);
    }



    @Override
    protected void doDraw(double xPos, double yPos, double maxWidth, double maxHeight, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        Tessellator t = Tessellator.instance;



        double width = image.getWidth();
        double height = image.getHeight();


        maxHeight = maxHeight / 2;
        maxWidth = maxWidth / 2;

        double x;
        double y;

        double xScale = MathHelper.clamp_double(maxWidth / width, 0, 1);
        double yScale = MathHelper.clamp_double(maxHeight / height, 0, 1);

        if (xScale < yScale) {
            x = xScale * width;
            y = xScale * height;
        } else {
            x = yScale * width;
            y = yScale * height;
        }

        double hiX = xPos + x;
        double hiY = yPos + y;
        double lowX = xPos - x;
        double lowY = yPos - y;

        double xMinus = 5;
        double yMinus = 5;
        GL11.glColor3f(1, 1, 1);


        bindImage();
        t.startDrawingQuads();
        {
            t.addVertexWithUV(lowX, lowY, -0.1, 0, 0);
            t.addVertexWithUV(hiX, lowY, -0.1, 1, 0);
            t.addVertexWithUV(hiX, hiY, -0.1, 1, 1);
            t.addVertexWithUV(lowX, hiY, -0.1, 0, 1);
        }
        t.draw();
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
    }

    @Override
    protected void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {

    }

    @Override
    public void onDestroy() {
        glDeleteTextures(textureId);
    }

    public BufferedImage getImage() {
        return image;
    }
}
