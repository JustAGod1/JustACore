package ru.justagod.justacore;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by JustAGod on 03.11.17.
 */
@Mod(modid = "jac", name = "JustACore", version = "0.1", dependencies = "after:hel")
public class Main {


    @EventHandler
    public void init(FMLInitializationEvent e) {
    }

    @EventHandler
    public void init(FMLPreInitializationEvent e) {
    }

    @EventHandler
    public void init(FMLPostInitializationEvent e) {
    }


}
