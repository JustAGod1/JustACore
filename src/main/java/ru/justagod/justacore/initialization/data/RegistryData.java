package ru.justagod.justacore.initialization.data;

/**
 * Created by JustAGod on 10.12.17.
 */
public class RegistryData extends Data {

    public final String registryId;
    public final boolean customRegistry;
    public final String itemBlock;

    public RegistryData(String[] dependencies, String clazz, String registryId, boolean customRegistry, String itemBlock) {
        super(dependencies, clazz);
        this.registryId = registryId;
        this.customRegistry = customRegistry;
        this.itemBlock = itemBlock;
    }
}
