package ru.justagod.justacore;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ru.justagod.justacore.initialization.InitHandler;

/**
 * Created by JustAGod on 03.11.17.
 */
@Mod(modid = "jac", name = "JustACore", version = "0.1")
public class Main {



    @EventHandler
    public void init(FMLInitializationEvent e) {
        InitHandler.init(this, e);
    }

    @EventHandler
    public void init(FMLPreInitializationEvent e) {
        InitHandler.preInit(this, e);
    }

    @EventHandler
    public void init(FMLPostInitializationEvent e) {
        InitHandler.postInit(this, e);
    }

    @EventHandler
    public void init(FMLConstructionEvent e) {
        InitHandler.start(this, e);
    }

    public static void main(String[] args) {

    }
}
