package ru.justagod.justacore.helper;

import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

/**
 * Created by JustAGod on 03.11.17.
 */
public final class DrawHelper {

    public static void drawCircle(double step, boolean useTexture) {

        Tessellator t = Tessellator.instance;
        t.startDrawing(GL11.GL_TRIANGLE_FAN);

        if (useTexture) {
            t.addVertexWithUV(0, 0, 0, 0.5, 0.5);
            t.addVertexWithUV(cos(toRadians(0)), sin(toRadians(0)), 0, cos(toRadians(0)), sin(toRadians(0)));

            for (int i = 1; i < 360 / step; i++) {
                t.addVertexWithUV(cos(toRadians(i * step)), sin(toRadians(i * step)), 0, cos(toRadians(0)), sin(toRadians(0)));
            }
        } else {
            t.addVertex(0, 0, 0);
            t.addVertex(cos(toRadians(0)), sin(toRadians(0)), 0);

            for (int i = 1; i < 360 / step; i++) {
                t.addVertex(cos(toRadians(i * step)), sin(toRadians(i * step)), 0);
            }
        }

        t.draw();
    }
}
