package ru.justagod.justacore.gui.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

/**
 * Created by JustAGod on 06.11.17.
 */
public final class ScreenHelper  {

    private static ScaledResolution getResolution() {
        return new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    }

    public static int getMinecraftWidth() {
        return getResolution().getScaledWidth();
    }

    public static int getMinecraftHeight() {
        return getResolution().getScaledHeight();
    }

    public static double getXFactor() {
        return Minecraft.getMinecraft().displayWidth / getMinecraftWidth();
    }

    public static double getYFactor() {
        return Minecraft.getMinecraft().displayHeight / getMinecraftHeight();
    }

    public static int transformToScreenX(int minecraftX) {
        return (int) (minecraftX * getXFactor());
    }

    public static int transformToScreenY(int minecraftY) {
        minecraftY = getMinecraftHeight() - minecraftY;
        minecraftY *= getYFactor();
        return minecraftY;
    }
}
