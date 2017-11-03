package ru.justagod.justacore.example.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import ru.justagod.justacore.example.gui.SimpleGui;

/**
 * Created by JustAGod on 03.11.17.
 */
public class GuiBlock extends Block {

    public GuiBlock() {
        super(Material.iron);
    }

    @Override
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return Blocks.bookshelf.getIcon(p_149691_1_, p_149691_2_);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int meta, float clickX, float clickY, float clickZ) {
        if (world.isRemote) new SimpleGui().open();
        return true;
    }
}
