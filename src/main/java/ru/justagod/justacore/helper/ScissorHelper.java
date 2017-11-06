package ru.justagod.justacore.helper;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.util.Stack;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by JustAGod on 04.11.17.
 */
public class ScissorHelper {

    private static Stack<Scissor> scissors = new Stack<Scissor>();
    private static Scissor currentScissor = null;

    public static void relativeScissor(double x, double y, double width, double height) {
        int displayWidth = Minecraft.getMinecraft().displayWidth;
        int displayHeight = Minecraft.getMinecraft().displayHeight;


        int realWidth = (int) (width * displayWidth);
        int realHeight = (int) (height * displayHeight);
        int realX = (int) (x * displayWidth);
        int realY = (int) (displayHeight - (y * displayHeight) - realHeight);

        realScissor(realX, realY, realWidth, realHeight);


    }


    public static void realScissor(int x, int y, int width, int height) {

        Scissor scissor;
        if (scissors.size() > 0) {
            scissor = scissors.peek();
            scissor = scissor.intersection(new Scissor(x, y, width, height));

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





}
