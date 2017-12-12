package ru.justagod.justacore.initialization.data;

import java.util.ArrayList;

/**
 * Внутреняя кухня
 * @author JustAGod
 */
public class RegistryContainerData extends Data {

    public final boolean customRegistry;

    public RegistryContainerData(ArrayList<String> dependencies, String clazz, boolean customRegistry) {
        super(dependencies, clazz);
        this.customRegistry = customRegistry;
    }
}
