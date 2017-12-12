package ru.justagod.justacore.example.initializationexample;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import ru.justagod.justacore.initialization.annotation.Module;
import ru.justagod.justacore.initialization.annotation.ModuleEventHandler;

/**
 * Created by JustAGod on 12.12.17.
 */
@Module(dependencies = {"Waila", "pich"})
public abstract class SimpleModule {
    @ModuleEventHandler
    private static void onInit(FMLInitializationEvent e) {
        System.out.println("Hello, world!");
    }
}
