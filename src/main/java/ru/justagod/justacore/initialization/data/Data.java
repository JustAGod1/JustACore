package ru.justagod.justacore.initialization.data;

import java.util.ArrayList;

/**
 * Created by JustAGod on 10.12.17.
 */
public class Data {

    public final ArrayList<String> dependencies;
    public final String clazz;

    public Data(ArrayList<String> dependencies, String clazz) {
        this.dependencies = dependencies;
        this.clazz = clazz;
    }
}
