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

    private InitHandler initHandler = new InitHandler(this);

    @EventHandler
    public void init(FMLInitializationEvent e) {
        initHandler.init(e);
    }

    @EventHandler
    public void init(FMLPreInitializationEvent e) {
        initHandler.preInit(e);
    }

    @EventHandler
    public void init(FMLPostInitializationEvent e) {
        initHandler.postInit(e);
    }

    @EventHandler
    public void init(FMLConstructionEvent e) {
        initHandler.start(e);
    }

}
