package ru.justagod.justacore.gui.overlay;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by JustAGod on 17.10.17.
 */
public class ImageOverlay extends ScaledOverlay {

    private final ResourceLocation image;

    public ImageOverlay(int x, int y, ResourceLocation image) {
        super(x, y);
        this.image = image;
    }

    public ImageOverlay(int x, int y, int width, int height, ResourceLocation image) {
        super(x, y, width, height);
        this.image = image;
    }

    @Override
    protected void doDraw(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {


            glDisable(GL_CULL_FACE);
            glDisable(GL_DEPTH_TEST);
            Tessellator t = Tessellator.instance;




            double x = width / 2;
            double y = height / 2;



            double hiX = xPos + x;
            double hiY = yPos + y;
            double lowX = xPos - x;
            double lowY = yPos - y;

            double xMinus = 5;
            double yMinus = 5;
            GL11.glColor3f(1, 1, 1);
            //bindGuiTexture("view_picture_border.png");


            bindTexture(image);
            t.startDrawingQuads();
            {
                t.addVertexWithUV(lowX, lowY, -0, 0, 0);
                t.addVertexWithUV(hiX, lowY, -0, 1, 0);
                t.addVertexWithUV(hiX, hiY, -0, 1, 1);
                t.addVertexWithUV(lowX, hiY, -0, 0, 1);
            }
            t.draw();
            glEnable(GL_CULL_FACE);
            glEnable(GL_DEPTH_TEST);

    }

    @Override
    protected void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {

    }
}
