package ru.justagod.justacore.gui.screen;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import ru.justagod.justacore.gui.overlay.CenteredTextOverlay;
import ru.justagod.justacore.gui.overlay.Overlay;
import ru.justagod.justacore.gui.overlay.ScaledOverlay;
import ru.justagod.justacore.gui.overlay.parent.OverlayParent;
import ru.justagod.justacore.gui.overlay.set.OverlaySet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by JustAGod on 17.10.17.
 */
public abstract class PichGui extends GuiScreen implements OverlayParent {

    protected List<ScaledOverlay> overlays = new ArrayList<ScaledOverlay>();

    public PichGui() {
        addOverlay(new CenteredTextOverlay(50, 50, "Пустой Pich GUI"));

    }

    public static void openPichGui(PichGui gui) {

        Minecraft.getMinecraft().displayGuiScreen(gui);
        if (gui != null) {
            FMLCommonHandler.instance().bus().post(new OpenPichGuiEvent(gui));
        }
    }

    @Override
    protected void keyTyped(char c, int keyCode) {
        super.keyTyped(c, keyCode);
        for (int i = 0; i < overlays.size(); i++) {
            ScaledOverlay overlay = overlays.get(i);

            overlay.onKey(c, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int type) {
        super.mouseClicked(x, y, type);

        for (int i = 0; i < overlays.size(); i++) {
            ScaledOverlay overlay = overlays.get(i);
            if (overlay.onClick(x, y)) return;
        }
    }

    @Override
    public synchronized void drawScreen(int mouseX, int mouseY, float partialTick) {
        super.drawScreen(mouseX, mouseY, partialTick);
        drawDefaultBackground();

        for (int i = 0; i < overlays.size(); i++) {
            Overlay overlay = overlays.get(i);
            overlay.draw(partialTick, mouseX, mouseY);
            overlay.drawText(partialTick, mouseX, mouseY);
        }
        doDraw();
    }

    protected abstract void doDraw();

    @Override
    public void updateScreen() {
        super.updateScreen();

        for (Overlay overlay : overlays) {
            overlay.update();
        }
    }




    @Override
    public synchronized void addOverlay(ScaledOverlay overlay) {
        overlays.add(overlay);
        overlay.setParent(this);
    }

    @Override
    public synchronized void removeOverlay(ScaledOverlay overlay) {
        if (overlays.remove(overlay)) {
            overlay.setParent(null);
        }
    }

    @Override
    public Collection<ScaledOverlay> getOverlays() {
        return overlays;
    }

    @Override
    public void setOverlays(OverlaySet overlaySet) {
        clear();
        appendOverlays(overlaySet);
    }

    @Override
    public void addOverlays(Collection<ScaledOverlay> overlays) {
        for (ScaledOverlay overlay : overlays) {
            addOverlay(overlay);
        }
    }

    @Override
    public void appendOverlays(OverlaySet overlaySet) {
        Collection<ScaledOverlay> overlays = overlaySet.getOverlays();

        for (ScaledOverlay overlay : overlays) {
            addOverlay(overlay);
        }
    }

    @Override
    public void moveUp(ScaledOverlay overlay) {
        int position = overlays.indexOf(overlay);

        if (position != -1) {
            overlays.remove(position);
            overlays.add(position + 1, overlay);
        }
    }

    @Override
    public void moveDown(ScaledOverlay overlay) {
        int position = overlays.indexOf(overlay);

        if (position != -1) {
            overlays.remove(position);
            overlays.add(position - 1, overlay);
        }
    }

    @Override
    public double getScaledWidth() {
        return getResolution().getScaledWidth();
    }

    @Override
    public double getScaledHeight() {
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
    public synchronized void clear() {
        Iterator<ScaledOverlay> iterator = overlays.iterator();

        while (iterator.hasNext()) {
            ScaledOverlay overlay = iterator.next();
            overlay.setParent(null);
            iterator.remove();
        }
    }

    protected void close() {
        PichGui.openPichGui(null);
    }

    public void open() {
        PichGui.openPichGui(this);
    }
    protected ScaledResolution getResolution() {
        return new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    }

    public static class OpenPichGuiEvent extends Event {
        public PichGui pichGui;

        public OpenPichGuiEvent(PichGui pichGui) {
            this.pichGui = pichGui;
        }
    }
}
