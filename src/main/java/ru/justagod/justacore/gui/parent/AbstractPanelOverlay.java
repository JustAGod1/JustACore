package ru.justagod.justacore.gui.parent;


import ru.justagod.justacore.gui.model.OverlaysCollection;
import ru.justagod.justacore.gui.overlay.ScaledOverlay;

import java.util.Collection;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

/**
 * Created by JustAGod on 20.10.17.
 */
public abstract class AbstractPanelOverlay extends ScaledOverlay implements OverlayParent {

    protected OverlaysCollection overlays = new OverlaysCollection(this);

    public AbstractPanelOverlay(double x, double y) {
        super(x, y);
        setDoScissor(true);
    }

    public AbstractPanelOverlay(double x, double y, double width, double height) {
        super(x, y, width, height);
        setDoScissor(true);
    }

    public AbstractPanelOverlay(double x, double y, double width, double height, boolean scalePosition, boolean scaleSize) {
        super(x, y, width, height, scalePosition, scaleSize);
        setDoScissor(true);
    }

    @Override
    public synchronized void addOverlay(ScaledOverlay overlay) {
        overlays.add(overlay);
    }

    @Override
    public synchronized void removeOverlay(ScaledOverlay overlay) {
        overlays.remove(overlay);
    }

    @Override
    public synchronized void update() {
        super.update();
        overlays.update();
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
    public void moveToFront(ScaledOverlay overlay) {
        overlays.moveToFront(overlay);
    }

    @Override
    public void moveToBackground(ScaledOverlay overlay) {
        overlays.moveToBackground(overlay);
    }

    @Override
    public double getParentWidth() {
        return super.getScaledWidth();
    }

    @Override
    public double getParentHeight() {
        return super.getScaledHeight();
    }

    @Override
    public double getScaledX() {
        return super.getScaledX();
    }

    @Override
    public double getScaledY() {
        return super.getScaledY();
    }


    @Override
    protected boolean doMouseScroll(double mouseX, double mouseY, int scrollAmount) {
        overlays.iterate(overlay -> !overlay.onMouseScroll(mouseX, mouseY, scrollAmount));
        return true;
    }

    @Override
    protected boolean doClick(int mouseX, int mouseY) {
        overlays.iterate(overlay -> !overlay.onClick(mouseX, mouseY));
        return true;
    }

    @Override
    protected void doDraw(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        glPushMatrix();
        overlays.iterate(overlay -> {
            overlay.draw(partialTick, mouseX, mouseY);
            return true;
        });
        glPopMatrix();
    }

    @Override
    protected void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        glPushMatrix();
        overlays.iterate(overlay -> {
            overlay.drawText(partialTick, mouseX, mouseY);
            return true;
        });
        glPopMatrix();
    }

    @Override
    protected boolean doMouseDrag(int lastMouseX, int lastMouseY, int mouseX, int mouseY) {
        overlays.downIterate(overlay ->
                !overlay.onMouseDrag(lastMouseX, lastMouseY, mouseX, mouseY)
        );
        return true;
    }

    @Override
    public void doOnKey(char key, int code) {
        overlays.iterate(overlay -> !overlay.onKey(key, code));
    }

    @Override
    public void addOverlays(Collection<ScaledOverlay> overlays) {
        for (ScaledOverlay overlay : overlays) {
            addOverlay(overlay);
        }
    }

    @Override
    public synchronized void clear() {
        overlays.clear();
    }

    @Override
    public void onActivate() {
        super.onActivate();
        overlays.iterate(overlay -> {
            overlay.onActivate();
            return true;
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        overlays.iterate(overlay -> {
            overlay.onDestroy();
            return true;
        });
    }

    @Override
    public void onResize() {
        super.onResize();
        overlays.iterate(overlay -> {
            overlay.onResize();
            return true;
        });
    }

    @Override
    public String toString() {
        return "AbstractPanelOverlay{} " + super.toString();
    }
}
