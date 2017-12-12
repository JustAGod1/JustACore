package ru.justagod.justacore.initialization.data;

import cpw.mods.fml.common.eventhandler.EventPriority;

import java.util.ArrayList;

/**
 * Created by JustAGod on 10.12.17.
 */
public class ModuleData extends Data {

    public final EventPriority priority;

    public ModuleData(ArrayList<String> dependencies, String clazz, EventPriority priority) {
        super(dependencies, clazz);
        this.priority = priority;
    }
}
