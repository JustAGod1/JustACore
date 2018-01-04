package ru.justagod.justacore.initialization.core.config;

/**
 * Created by JustAGod on 04.01.2018.
 */
public enum ConfigType {
    JSON(JsonConfig.class, "json"), TEXT(TextConfig.class, "txt");

    private final Class<? extends Config> clazz;
    private final String postfix;

    public Class<? extends Config> getClazz() {
        return clazz;
    }

    public String getPostfix() {
        return postfix;
    }

    ConfigType(Class<? extends Config> clazz, String postfix) {
        this.clazz = clazz;
        this.postfix = postfix;
    }
}
