package ru.justagod.justacore.initialization.data;

import ru.justagod.justacore.initialization.annotation.RegistryObjectsContainer;

import java.util.ArrayList;

/**
 * Внутреняя кухня
 * @author JustAGod
 */
@RegistryObjectsContainer
public class RegistryData extends Data {

    public final String registryId;
    public final boolean customRegistry;
    public final String itemBlock;

    public RegistryData(ArrayList<String> dependencies, String clazz, String registryId, boolean customRegistry, String itemBlock) {
        super(dependencies, clazz);
        this.registryId = registryId;
        this.customRegistry = customRegistry;
        this.itemBlock = itemBlock;
    }
}
