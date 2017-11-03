package ru.justagod.justacore.example;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import ru.justagod.justacore.CommonProxy;

/**
 * Created by JustAGod on 03.11.17.
 */
public class CreativeTab extends CreativeTabs {

    public CreativeTab() {
        super("justacore");
    }

    @Override
    public Item getTabIconItem() {
        return Item.getItemFromBlock(CommonProxy.gui_block);
    }
}
