package ru.justagod.justacore.initialization.language;

import ru.justagod.justacore.initialization.core.config.Config;
import ru.justagod.justacore.initialization.obj.ModModule;

/**
 * Created by JustAGod on 04.01.2018.
 */
public interface LanguageAdapter {

    ModModule buildModule(Class clazz) throws Exception;

    Object buildInstance(Class clazz) throws Exception;

    void registerFields(Class clazz);

    void callCustomRegistry(Object o) throws Exception;

    boolean tryToStaticRegister(Class clazz);

    void setConfig(Class clazz, Config config);
}
