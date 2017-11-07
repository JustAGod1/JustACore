package ru.justagod.justacore.gui.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import ru.justagod.justacore.gui.model.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by JustAGod on 03.11.17.
 */
public final class DrawHelper {

    private static final Map<CursorType, Cursor> loadedCursors = new HashMap<>();
    private static final int BYTES_PER_PIXEL = 4;

    public static int loadTexture(BufferedImage image) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) (pixel >> 16 & 0xFF));
                buffer.put((byte) (pixel >> 8 & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) (pixel >> 24 & 0xFF));
            }
        }

        buffer.flip();

        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        return textureID;
    }

    public static void reloadCursors() {
        try {

            Field field = Minecraft.class.getDeclaredField("mcResourceManager");
            field.setAccessible(true);
            IReloadableResourceManager resourceManager = (IReloadableResourceManager) field.get(Minecraft.getMinecraft());
            loadedCursors.put(CursorType.NORMAL, loadCursor(ImageIO.read(resourceManager.getResource(new ResourceLocation("jac", "textures/cursors/cursor.png")).getInputStream())));
            loadedCursors.put(CursorType.CLICKER, loadCursor(ImageIO.read(resourceManager.getResource(new ResourceLocation("jac", "textures/cursors/clicker.png")).getInputStream())));
            loadedCursors.put(CursorType.DRAG, loadCursor(ImageIO.read(resourceManager.getResource(new ResourceLocation("jac", "textures/cursors/drag.png")).getInputStream())));
            loadedCursors.put(CursorType.HOURGLASS, loadCursor(ImageIO.read(resourceManager.getResource(new ResourceLocation("jac", "textures/cursors/hourglass.png")).getInputStream())));
            loadedCursors.put(CursorType.MOVE, loadCursor(ImageIO.read(resourceManager.getResource(new ResourceLocation("jac", "textures/cursors/move.png")).getInputStream())));
            loadedCursors.put(CursorType.TEXT, loadCursor(ImageIO.read(resourceManager.getResource(new ResourceLocation("jac", "textures/cursors/text.png")).getInputStream())));
            loadedCursors.put(CursorType.ZOOM_IN, loadCursor(ImageIO.read(resourceManager.getResource(new ResourceLocation("jac", "textures/cursors/zoom-in.png")).getInputStream())));
            loadedCursors.put(CursorType.ZOOM_OUT, loadCursor(ImageIO.read(resourceManager.getResource(new ResourceLocation("jac", "textures/cursors/zoom-out.png")).getInputStream())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public static void enableAlpha() {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
    }

    public static void disableAlpha() {
        glDisable(GL_BLEND);
    }

    public static Cursor loadCursor(BufferedImage img) throws LWJGLException {
        int w = img.getWidth();
        int h = img.getHeight();

        int rgbData[] = new int[w * h];

        for (int i = 0; i < rgbData.length; i++) {
            int x = i % w;
            int y = h - 1 - i / w;

            rgbData[i] = img.getRGB(x, y);
        }

        IntBuffer buffer = BufferUtils.createIntBuffer(w * h);
        buffer.put(rgbData);
        buffer.rewind();


        return new Cursor(w, h, 2, h - 2, 1, buffer, null);
    }

    public static void bindColor(Color color) {
        if (color.getAlpha() != 1) {
            enableAlpha();
        }
        glColor4d(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static void bindCursor(CursorType cursorType) {
        Cursor cursor = loadedCursors.get(cursorType);
        if (cursor == null) {
            throw new NullPointerException(String.format("Курсор %s почему-то не был загружен", cursorType.name()));
        }

        try {
            Mouse.setNativeCursor(cursor);
        } catch (LWJGLException e) {
            throw new RuntimeException("Ошибка во время привязки курсора", e);
        }
    }

    public enum CursorType {
        NONE, NORMAL, CLICKER, DRAG, HOURGLASS, MOVE, TEXT, ZOOM_IN, ZOOM_OUT
    }
}
