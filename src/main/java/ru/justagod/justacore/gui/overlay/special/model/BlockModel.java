package ru.justagod.justacore.gui.overlay.special.model;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import ru.justagod.justacore.gui.helper.DrawHelper;
import ru.justagod.justacore.gui.model.Color;

/**
 * Created by JustAGod on 15.11.17.
 */
public class BlockModel implements Model {

    private static final RenderBlocks render = new RenderBlocks(Minecraft.getMinecraft().theWorld);

    private Block block;

    public BlockModel(Block block) {
        this.block = block;
    }

    @Override
    public double getWidth() {
        return 20;
    }

    @Override
    public double getHeight() {
        return 20;
    }

    @Override
    public void draw() {
        DrawHelper.drawRect(20, 20, new Color(1, 0, 0));
        //render.renderBlockAllFaces(block, 0, 0, 0);
    }
}
