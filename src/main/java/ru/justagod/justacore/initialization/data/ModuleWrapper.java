package ru.justagod.justacore.initialization.data;

import ru.justagod.justacore.initialization.obj.ModModule;

/**
 * Created by JustAGod on 04.01.2018.
 */
public class ModuleWrapper {

    public final ModModule module;
    public final boolean mandatory;
    public final String name;

    public ModuleWrapper(ModModule module, boolean mandatory, String name) {
        this.module = module;
        this.mandatory = mandatory;
        this.name = name;
    }
}
