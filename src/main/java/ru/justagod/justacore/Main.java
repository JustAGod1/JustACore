package ru.justagod.justacore;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

/**
 * Created by JustAGod on 03.11.17.
 */
@Mod(modid = "jac", name = "JustACore", version = "0.1")
public class Main {


    @EventHandler
    public void init(FMLInitializationEvent e) {
        switch (e.getSide()) {
            case SERVER:
                new CommonProxy().init();
                break;
            case CLIENT:
                new ClientProxy().init();
                break;

        }
    }
}
