package ru.justagod.justacore.gui;

import org.lwjgl.opengl.GL11;
import ru.justagod.illnesses.client.gui.overlay.InGameOverlays;
import ru.justagod.illnesses.client.gui.overlay.ScaledOverlay;
import ru.justagod.illnesses.client.gui.overlay.TextOverlay;
import ru.justagod.illnesses.client.gui.overlay.animation.FadeOutAnimation;
import ru.justagod.illnesses.client.gui.overlay.animation.MovingToAnimation;
import ru.justagod.illnesses.client.gui.overlay.animation.OverlayAnimator;
import ru.justagod.illnesses.client.gui.overlay.animation.QueuedAnimation;
import ru.justagod.illnesses.client.gui.overlay.parent.PanelOverlay;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by JustAGod on 02.11.17.
 */
public class HUDMessages extends PanelOverlay{
    public HUDMessages() {
        super(0, 0, 40, 50);
        InGameOverlays.getInstance().addOverlay(this);
    }

    public void addMessage(String message) {
        TextOverlay overlay = new TextOverlay(0, 2 + overlays.size() * 7, message);

        addOverlay(overlay);
        setDoScissor(false);
    }

    @Override
    public synchronized void addOverlay(ScaledOverlay overlay) {
        super.addOverlay(overlay);
        if (overlays.size() == 1)
            overlay.addAnimator(buildAnimatorForFirst());
    }

    @Override
    public synchronized void removeOverlay(ScaledOverlay overlay) {
        super.removeOverlay(overlay);
        if (overlays.size() > 0) {
            ScaledOverlay o = overlays.get(0);

            o.setAnimator(buildAnimatorForFirst());
            for (int i = 1; i < overlays.size(); i++) {
                o = overlays.get(i);

                o.setAnimator(new MovingToAnimation(40, 0, 2 + i * 7));
            }
        }

    }

    @Override
    protected void doDraw(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        //realScissor(xPos, yPos, xPos + width, yPos + height);
        for (int i = 0; i < overlays.size() && i < 5; i++) {
            overlays.get(i).draw(partialTick, mouseX, mouseY);
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    protected void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        //realScissor(xPos, yPos, xPos + width, yPos + height);
        for (int i = 0; i < overlays.size() && i < 5; i++) {
            overlays.get(i).drawText(partialTick, mouseX, mouseY);
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private OverlayAnimator buildAnimatorForFirst() {
        return new QueuedAnimation(new ArrayBlockingQueue<OverlayAnimator>(2, true, Arrays.asList(new MovingToAnimation(40, 0, 2), new FadeOutAnimation())));
    }
}
