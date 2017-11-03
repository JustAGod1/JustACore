package ru.justagod.justacore;

import cpw.mods.fml.common.registry.GameRegistry;
import ru.justagod.justacore.example.CreativeTab;
import ru.justagod.justacore.example.block.GuiBlock;

/**
 * Created by JustAGod on 03.11.17.
 */
public class CommonProxy {

    public static final CreativeTab tab = new CreativeTab();

    public static final GuiBlock gui_block = new GuiBlock();

    public void init() {
        GameRegistry.registerBlock(gui_block, "gui_block");

        gui_block.setCreativeTab(tab);
    }
}
