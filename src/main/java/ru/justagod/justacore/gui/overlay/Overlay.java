package ru.justagod.justacore.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.justagod.justacore.gui.animation.OverlayAnimator;
import ru.justagod.justacore.gui.model.Vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by JustAGod on 15.10.17.
 */
public abstract class Overlay {

    protected Tessellator t = Tessellator.instance;

    protected double x;
    protected double y;

    protected List<OverlayAnimator> animators = new ArrayList<OverlayAnimator>();

    public Overlay(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void addAnimator(OverlayAnimator animator) {
        animator.init(this);
        animators.add(animator);
    }

    public abstract void draw(float partialTick, int mouseX, int mouseY);
    public abstract void drawText(float partialTick, int mouseX, int mouseY);

    public void update() {
        Iterator<OverlayAnimator> iterator = animators.iterator();
        while (iterator.hasNext()) {
            OverlayAnimator animator = iterator.next();
            if (animator.isDead()) {
                iterator.remove();
            } else {
                animator.update(this);
            }
        }
    }

    public void setAnimator(OverlayAnimator animator) {
        animators.clear();
        addAnimator(animator);
    }


    protected void bindGuiTexture(String path) {
        bindTexture(new ResourceLocation("illnesses", "textures/gui/" + path));
    }

    protected void bindTexture(String path) {
        bindTexture(new ResourceLocation("illnesses", path));
    }

    protected void bindTexture(ResourceLocation location) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(location);
    }

    protected void drawCentredString(String s, int x, int y, boolean shadow) {
        drawString(s, x - Minecraft.getMinecraft().fontRenderer.getStringWidth(s) / 2, y, shadow);
    }

    protected void drawString(String s, int x, int y, boolean shadow) {
        Minecraft.getMinecraft().fontRenderer.drawString(s, x, y, -1, shadow);
    }



    protected void pushAndTranslate(double x, double y) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
    }

    protected void pop() {
        GL11.glPopMatrix();
    }

    public Vector getPos() {
        return new Vector(x, y);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }


    public void setPos(Vector pos) {
        this.x = pos.getX();
        this.y = pos.getY();
    }

    @Override
    public String toString() {
        return "\nOverlay{" +
                "\nx=" + x +
                "\n, y=" + y +
                "\n, animators=" + animators +
                "\n}";
    }
}
