package ru.justagod.justacore.initialization.data;

import java.util.ArrayList;

/**
 * Внутреняя кухня
 * @author JustAGod
 */
public class Data {

    public final ArrayList<String> dependencies;
    public final String clazz;

    public Data(ArrayList<String> dependencies, String clazz) {
        this.dependencies = dependencies;
        this.clazz = clazz;
    }
}
