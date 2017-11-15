package ru.justagod.justacore.gui.overlay;

import ru.justagod.justacore.gui.model.Color;
import ru.justagod.justacore.gui.model.Dimensions;
import ru.justagod.justacore.gui.parent.OverlayParent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustAGod on 07.11.17.
 */
public class EntriesListOverlay extends ScaledOverlay {
    private final List<Entry> entries = new ArrayList<>();
    private final VerticalScrollingOverlay scrollingOverlay;

    public EntriesListOverlay(double x, double y, Color background, List<Entry> entries) {
        super(x, y);
        this.entries.addAll(entries);
        scrollingOverlay = new VerticalScrollingOverlay(x, y, new Dimensions(0, 0), background);
        rearrange();
    }

    public EntriesListOverlay(double x, double y, double width, double height, Color background, List<Entry> entries) {
        super(x, y, width, height);
        this.entries.addAll(entries);
        scrollingOverlay = new VerticalScrollingOverlay(x, y, width, height, new Dimensions(0, 0), background);
        rearrange();
    }

    private void rearrange() {
        scrollingOverlay.clear();
        double y = 0;
        for (Entry entry : entries) {
            entry.setY(y);
            y += entry.getHeight();
            scrollingOverlay.addOverlay(entry);
        }
        scrollingOverlay.getInnerDimensions().setHeight(y);
    }

    public void clear() {
        entries.clear();
        rearrange();
    }

    public void remove(int pos) {
        entries.remove(pos);
        rearrange();
    }

    public void remove(Entry entry) {
        if (entries.remove(entry)) {
            rearrange();
        }
    }

    public void add(Entry entry, int pos) {
        entries.add(pos, entry);
        rearrange();
    }

    public void add(Entry entry) {
        entries.add(entry);
        rearrange();
    }

    @Override
    public ScaledOverlay setParent(OverlayParent parent) {
        super.setParent(parent);
        scrollingOverlay.setParent(parent);

        return this;
    }

    @Override
    protected void doDraw(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        scrollingOverlay.draw(partialTick, mouseX, mouseY);
    }

    @Override
    protected void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        scrollingOverlay.drawText(partialTick, mouseX, mouseY);
    }

    public abstract class Entry extends ScaledOverlay {

        public Entry(double height) {
            super(0, 0, 100, height);
            setScaleSizeMode(ScaleSizeMode.DONT_SCALE_HEIGHT);
        }
    }
}
