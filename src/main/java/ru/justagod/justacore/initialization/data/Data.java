package ru.justagod.justacore.initialization.data;

/**
 * Created by JustAGod on 10.12.17.
 */
public class Data {

    public final String[] dependencies;
    public final String clazz;

    public Data(String[] dependencies, String clazz) {
        this.dependencies = dependencies;
        this.clazz = clazz;
    }
}
