package ru.justagod.justacore.helper;

import java.util.Random;

/**
 * Created by JustAGod on 02.11.17.
 */
public class MathHelper {

    public static final double EPSILON = 0.0001;
    public static final Random RANDOM = new Random();

    public static boolean compareDouble(double first, double second) {
        return compareDouble(first, second, EPSILON);
    }

    public static boolean compareDouble(double first, double second, double epsilon) {
        return Math.abs(first - second) < epsilon;
    }

    public static int randomInt(int min, int max) {
        if (max <= min) throw new RuntimeException("max smaller than min");
        return RANDOM.nextInt(max - min) + min;
    }

    public static float randomFloat(float min, float max) {
        return randomFloat(min, max, 100);
    }

    public static float randomFloat(float min, float max, int precision) {
        if (max <= min) throw new RuntimeException("max smaller than min");
        int randomInt = randomInt((int) (min * precision), (int) (max * precision));
        return randomInt / ((float) precision);
    }

    public static int clampInt(int target, int min, int max) {
        return net.minecraft.util.MathHelper.clamp_int(target, min, max);
    }

    public static float clampFloat(float target, float min, float max) {
        return net.minecraft.util.MathHelper.clamp_float(target, min, max);
    }

    public static double clampDouble(double target, double min, double max) {
        return net.minecraft.util.MathHelper.clamp_double(target, min, max);
    }

    public static Vector getRandomNormalizedVector() {
        return getRandomVector(-1000, -1000, 1000, 1000).normalize();
    }

    public static Vector getRandomVector(float minX, float minY, float maxX, float maxY) {
        return new Vector(randomFloat(minX, maxX), randomFloat(minY, maxY));
    }

}
