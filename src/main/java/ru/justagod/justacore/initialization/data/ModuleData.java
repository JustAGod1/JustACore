package ru.justagod.justacore.initialization.data;

import cpw.mods.fml.common.eventhandler.EventPriority;

import java.util.ArrayList;

/**
 * Внутреняя кухня
 * @author JustAGod
 */
public class ModuleData extends Data {

    public final EventPriority priority;
    public final boolean mandatory;

    public ModuleData(ArrayList<String> dependencies, String clazz, EventPriority priority, boolean mandatory) {
        super(dependencies, clazz);
        this.priority = priority;
        this.mandatory = mandatory;
    }
}
