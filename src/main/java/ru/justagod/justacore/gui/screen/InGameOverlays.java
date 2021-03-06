package ru.justagod.justacore.gui.screen;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import ru.justagod.justacore.gui.overlay.Overlay;
import ru.justagod.justacore.gui.overlay.ScaledOverlay;
import ru.justagod.justacore.gui.parent.AbstractOverlayParent;

import java.util.ConcurrentModificationException;

public final class InGameOverlays extends AbstractOverlayParent {

    private static InGameOverlays instance = new InGameOverlays();

    private InGameOverlays() {
        instance = this;
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    public static InGameOverlays getInstance() {
        return instance;
    }

    @SubscribeEvent
    public void onGui(RenderGameOverlayEvent e) {
        try {
            if (e.type == RenderGameOverlayEvent.ElementType.TEXT) {
                for (Overlay overlay : overlays) {
                    overlay.draw(e.partialTicks, -1, -1);
                    overlay.drawText(e.partialTicks, -1, -1);
                }
            } else {

            }
        } catch (ConcurrentModificationException ignored) {

        } catch (Throwable exc) {
            exc.printStackTrace();
        }

    }

    @SubscribeEvent
    public  void update(TickEvent.RenderTickEvent e) {
        try {
            for (ScaledOverlay overlay : overlays) {
                overlay.update();
            }
        } catch (ConcurrentModificationException ignored) {

        } catch (Throwable exc) {
            exc.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onExit(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        clear();
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

    protected ScaledResolution getResolution() {
        return new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    }

}
