package ru.justagod.justacore.initialization.obj;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by JustAGod on 10.12.17.
 */
public interface ModModule {

    void onPreInit(FMLPreInitializationEvent e);

    void onInit(FMLInitializationEvent e);

    void onPostInit(FMLPostInitializationEvent e);
}
