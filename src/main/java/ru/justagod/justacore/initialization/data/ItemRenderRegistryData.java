package ru.justagod.justacore.initialization.data;

/**
 * Created by JustAGod on 10.12.17.
 */
public class ItemRenderRegistryData {

    public final boolean customRegistry;
    public final String itemName;
    public final String clazz;

    public ItemRenderRegistryData(boolean customRegistry, String itemName, String clazz) {
        this.customRegistry = customRegistry;
        this.itemName = itemName;
        this.clazz = clazz;
    }
}
