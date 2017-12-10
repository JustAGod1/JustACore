package ru.justagod.justacore.initialization.data;

/**
 * Created by JustAGod on 10.12.17.
 */
public class RegistryContainerData extends Data {

    public final boolean customRegistry;

    public RegistryContainerData(String[] dependencies, String clazz, boolean customRegistry) {
        super(dependencies, clazz);
        this.customRegistry = customRegistry;
    }
}
