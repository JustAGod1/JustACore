package ru.justagod.justacore.gui.helper;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import ru.justagod.justacore.gui.model.Rect;

import java.util.Stack;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by JustAGod on 04.11.17.
 *
 * Не рассказывайте мне про матрицы и про то что можно было сувать в стэк матрицы а не сцизор и транслэйт. И без вас знаю!
 */
public class ScissorHelper {

    private static Stack<Scissor> scissors = new Stack<>();
    private static Stack<Translation> translations = new Stack<>();
    private static Scissor currentScissor = null;
    private static Translation currentTranslation = new Translation(0, 0);

    public static void relativeScissor(double x, double y, double width, double height) {
        int displayWidth = Minecraft.getMinecraft().displayWidth;
        int displayHeight = Minecraft.getMinecraft().displayHeight;


        int realWidth = (int) (width * displayWidth);
        int realHeight = (int) (height * displayHeight);
        int realX = (int) (x * displayWidth);
        int realY = (int) (displayHeight - (y * displayHeight) - realHeight);

        realScissor(realX, realY, realWidth, realHeight);


    }

    public static boolean isInScissorRect(Rect rect) {
        if (currentScissor != null) {
            Scissor rScissor = new Scissor(rect);
            rScissor= new Scissor(rScissor.getX() + currentTranslation.x, rScissor.getY() - currentTranslation.y, rScissor.getWidth(), rScissor.getHeight());
            return currentScissor.intersection(rScissor) != null;
        } return true;
    }

    public static void realScissor(int x, int y, int width, int height) {

        Scissor scissor;
        if (scissors.size() > 0) {
            scissor = scissors.peek();
            scissor = scissor.intersection(new Scissor(x + currentTranslation.x, y - currentTranslation.y, width, height));

            if (scissor == null) {
                currentScissor = scissors.peek();
                return;
            }
        } else {
            scissor = new Scissor(x, y, width, height);
        }

        glScissor(scissor.getX(), scissor.getY(), scissor.getWidth(), scissor.getHeight());

        currentScissor = scissor;


        GL11.glEnable(GL_SCISSOR_TEST);
    }

    public static void translate(int x, int y) {
        currentTranslation.x += x * ScreenHelper.getXFactor();
        currentTranslation.y += y * ScreenHelper.getYFactor();
    }

    public static void pushTranslation() {
         translations.push((Translation) currentTranslation.clone());
    }

    public static void popTranslation() {
        if (translations.size() > 0) {
            currentTranslation = translations.pop();
        } else {
            currentTranslation = new Translation(0, 0);
        }

    }

    public static void push() {
        if (currentScissor != null) {
            scissors.push(currentScissor);
        }

    }


    public static void pop() {
        if (scissors.size() > 0) {
            Scissor scissor = scissors.pop();

            realScissor(scissor.getX(), scissor.getY(), scissor.getWidth(), scissor.getHeight());
        } else {
            glDisable(GL_SCISSOR_TEST);
            currentScissor = null;
        }

    }


    private static class Translation {
        private int x;
        private int y;

        public Translation(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public Object clone() {
            return new Translation(x, y);
        }
    }


}
