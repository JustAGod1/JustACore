package ru.justagod.justacore.initialization.data;

import java.util.ArrayList;

/**
 * Внутреняя кухня
 * @author JustAGod
 */
public class RegistryData extends Data {

    public final String registryId;
    public final boolean customRegistry;
    public final String itemBlock;

    public RegistryData(ArrayList<String> dependencies, String clazz, String registryId, boolean customRegistry, String itemBlock, String configDependency) {
        super(dependencies, clazz, configDependency);
        this.registryId = registryId;
        this.customRegistry = customRegistry;
        this.itemBlock = itemBlock;
    }
}
