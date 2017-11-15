package ru.justagod.justacore.gui.screen;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import ru.justagod.justacore.gui.helper.DrawHelper;
import ru.justagod.justacore.gui.model.OverlaysCollection;
import ru.justagod.justacore.gui.overlay.CenteredTextOverlay;
import ru.justagod.justacore.gui.overlay.ScaledOverlay;
import ru.justagod.justacore.gui.parent.OverlayParent;

import java.util.Collection;

/**
 * Created by JustAGod on 17.10.17.
 */
public class PichGui extends GuiScreen implements OverlayParent {

    protected OverlaysCollection overlays;
    protected boolean pauseGame = true;
    private boolean released = true;
    private int lastMouseX = -1;
    private int lastMouseY = -1;

    {
        overlays = new OverlaysCollection(this);
    }

    public PichGui(boolean pauseGame) {

        this.pauseGame = pauseGame;
        DrawHelper.bindCursor(DrawHelper.CursorType.NORMAL);
    }

    public PichGui() {
        addOverlay(new CenteredTextOverlay(50, 50, "Пустой Pich GUI"));
        DrawHelper.bindCursor(DrawHelper.CursorType.NORMAL);
    }

    public static void openPichGui(PichGui gui) {


        Minecraft.getMinecraft().displayGuiScreen(gui);
        if (gui != null) {
            FMLCommonHandler.instance().bus().post(new OpenPichGuiEvent(gui));
        }
    }

    @Override
    public void setWorldAndResolution(Minecraft minecraft, int width, int height) {
        super.setWorldAndResolution(minecraft, width, height);

        overlays.iterate(o -> {
            o.onResize();
            return true;
        });
    }

    public boolean isPauseGame() {
        return pauseGame;
    }

    public void setPauseGame(boolean pauseGame) {
        this.pauseGame = pauseGame;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return pauseGame;
    }

    @Override
    protected void keyTyped(char c, int keyCode) {
        super.keyTyped(c, keyCode);
        overlays.iterate(overlay -> {
            overlay.onKey(c, keyCode);
            return true;
        });
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int scrollAmount = Mouse.getDWheel();
        if (scrollAmount != 0) {
            int mouseX = Mouse.getX();
            int mouseY = Mouse.getY();

            mouseY = Minecraft.getMinecraft().displayHeight - mouseY;

            mouseX /= Minecraft.getMinecraft().displayWidth / getResolution().getScaledWidth();
            mouseY /= Minecraft.getMinecraft().displayHeight / getResolution().getScaledHeight();

            int finalMouseX = mouseX;
            int finalMouseY = mouseY;
            overlays.iterate(overlay -> {
                overlay.onMouseScroll(finalMouseX, finalMouseY, scrollAmount);
                return true;
            });
        }
    }


    @Override
    protected void mouseClicked(int x, int y, int type) {
        super.mouseClicked(x, y, type);

        overlays.downIterate(overlay -> !overlay.onClick(x, y));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {

        doPreDraw(mouseX, mouseY, partialTick);

        overlays.iterate(overlay -> {
            overlay.draw(partialTick, mouseX, mouseY);
            overlay.drawText(partialTick, mouseX, mouseY);
            return true;
        });
        doPostDraw(mouseX, mouseY, partialTick);
    }

    protected void doPostDraw(int mouseX, int mouseY, float partialTick) {
    }

    protected void doPreDraw(int mouseX, int mouseY, float partialTick) {
        super.drawScreen(mouseX, mouseY, partialTick);
        drawDefaultBackground();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (!Mouse.isButtonDown(0)) {
            released = true;
        }

        overlays.update();


    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int lastButton, long time) {
        if (!released) {

            if (lastMouseX != -1 && lastMouseY != -1) {
                overlays.downIterate(overlay -> !overlay.onMouseDrag(lastMouseX, lastMouseY, mouseX, mouseY));


            }
        } else released = false;
        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    @Override
    public void addOverlay(ScaledOverlay overlay) {
        overlays.add(overlay);
    }

    @Override
    public void removeOverlay(ScaledOverlay overlay) {
        overlays.remove(overlay);
    }

    @Override
    public void addOverlays(Collection<ScaledOverlay> overlays) {
        for (ScaledOverlay overlay : overlays) {
            addOverlay(overlay);
        }
    }


    @Override
    public void moveUp(ScaledOverlay overlay) {
        overlays.moveUp(overlay);
    }

    @Override
    public void moveDown(ScaledOverlay overlay) {
        overlays.moveDown(overlay);
    }

    @Override
    public double getParentWidth() {
        return getResolution().getScaledWidth();
    }

    @Override
    public double getParentHeight() {
        return getResolution().getScaledHeight();
    }

    @Override
    public double getScaledX() {
        return 0;
    }

    @Override
    public double getScaledY() {
        return 0;
    }

    @Override
    public void clear() {
        overlays.clear();
    }

    public void close() {
        PichGui.openPichGui(null);
    }

    public void open() {
        PichGui.openPichGui(this);
    }

    protected ScaledResolution getResolution() {
        return new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    }

    @Override
    public void moveToFront(ScaledOverlay overlay) {

        overlays.moveToFront(overlay);


    }

    @Override
    public void moveToBackground(ScaledOverlay overlay) {

        overlays.moveToBackground(overlay);

    }

    public static class OpenPichGuiEvent extends Event {
        public PichGui pichGui;

        public OpenPichGuiEvent(PichGui pichGui) {
            this.pichGui = pichGui;
        }
    }

    @Override
    public String toString() {
        return "PichGui{" +
                "overlays=" + overlays +
                ", pauseGame=" + pauseGame +
                ", released=" + released +
                ", lastMouseX=" + lastMouseX +
                ", lastMouseY=" + lastMouseY +
                "} " + super.toString();
    }
}
