package ru.justagod.justacore.initialization.obj;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by JustAGod on 10.12.17.
 */
public abstract class ModModule {

    public void onPreInit(FMLPreInitializationEvent e) {}
    public void onInit(FMLInitializationEvent e) {}
    public void onPostInit(FMLPostInitializationEvent e) {}
}
