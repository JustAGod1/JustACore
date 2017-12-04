package ru.justagod.justacore.gui.overlay.special;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import ru.justagod.justacore.gui.overlay.ScaledOverlay;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by JustAGod on 14.11.17.
 */
public class StackOverlay extends ScaledOverlay {

    private static RenderItem renderer = new RenderItem();
    private final ItemStack stack;
    private ItemStack realStack;

    public StackOverlay(double x, double y, ItemStack stack) {
        super(x, y);
        this.stack = stack;
        realStack = ItemStack.copyItemStack(stack);
    }

    public StackOverlay(double x, double y, double width, double height, ItemStack stack) {
        super(x, y, width, height);
        this.stack = stack;
        realStack = ItemStack.copyItemStack(stack);
    }

    public StackOverlay(double x, double y, double width, double height, boolean scalePosition, boolean scaleSize, ItemStack stack) {
        super(x, y, width, height, scalePosition, scaleSize);
        this.stack = stack;
        realStack = ItemStack.copyItemStack(stack);
    }

    public void setShowSize(boolean showSize) {
        if (showSize) {
            realStack.stackSize = stack.stackSize;
        } else {
            realStack.stackSize = 1;
        }
    }

    @Override
    protected void doDraw(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        pushAndTranslate(xPos, yPos);
        glScaled(width / 16, height / 16, 0);
        drawItemStack(realStack);
        pop();
    }

    @Override
    protected void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {

    }

    private void drawItemStack(ItemStack stack)
    {
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        renderer.zLevel = 200.0F;
        FontRenderer font = null;
        if (stack != null) font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = getMinecraft().fontRenderer;
        renderer.renderItemAndEffectIntoGUI(font, getMinecraft().getTextureManager(), stack, 0, 0);
        renderer.renderItemOverlayIntoGUI(font, getMinecraft().getTextureManager(), stack, 0, 0, null);
        renderer.zLevel = 0.0F;
    }
}
