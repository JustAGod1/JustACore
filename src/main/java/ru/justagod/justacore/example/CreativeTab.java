package ru.justagod.justacore.example;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

/**
 * Created by JustAGod on 03.11.17.
 */
public class CreativeTab extends CreativeTabs {

    public CreativeTab() {
        super("justacore");
    }

    @Override
    public Item getTabIconItem() {
        return Items.blaze_rod;
    }
}
