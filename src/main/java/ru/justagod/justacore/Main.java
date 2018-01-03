package ru.justagod.justacore;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import ru.justagod.justacore.initialization.annotation.ConfigExtra;
import ru.justagod.justacore.initialization.annotation.ConfigHolder;
import ru.justagod.justacore.initialization.core.Config;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustAGod on 03.11.17.
 */
@ConfigExtra(name = "Simple.txt")
@Mod(modid = "jac", name = "JustACore", version = "0.1")
public class Main {

    @ConfigHolder
    public static final Config config = null;

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        List<Config.ConfigEntry> entries = new LinkedList<>();
        entries.add(new Config.ConfigEntry("Sample", "Sample value", "Just for example"));
        config.addDefaultValues(entries);
    }
}
