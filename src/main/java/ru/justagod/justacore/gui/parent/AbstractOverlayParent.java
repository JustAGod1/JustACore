package ru.justagod.justacore.gui.parent;


import ru.justagod.justacore.gui.overlay.ScaledOverlay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by JustAGod on 20.10.17.
 */
public abstract class AbstractOverlayParent implements OverlayParent {

    protected List<ScaledOverlay> overlays = new ArrayList<ScaledOverlay>();

    @Override
    public void addOverlay(ScaledOverlay overlay) {
        overlays.add(overlay);
        overlay.setParent(this);
    }

    @Override
    public void removeOverlay(ScaledOverlay overlay) {
        if(overlays.remove(overlay)) {
            overlay.setParent(null);
        }

    }

    @Override
    public Collection<ScaledOverlay> getOverlays() {
        return overlays;
    }

    @Override
    public void moveUp(ScaledOverlay overlay) {
        int position = overlays.indexOf(overlay);

        if (position != -1) {
            overlays.remove(position);
            overlays.add(position - 1, overlay);
        }
    }


    @Override
    public void moveDown(ScaledOverlay overlay) {
        int position = overlays.indexOf(overlay);

        if (position != -1) {
            overlays.remove(position);
            overlays.add(position + 1, overlay);
        }
    }


    @Override
    public void addOverlays(Collection<ScaledOverlay> overlays) {
        for (ScaledOverlay overlay : overlays) {
            addOverlay(overlay);
        }
    }


    @Override
    public synchronized void clear() {
        Iterator<ScaledOverlay> iterator = overlays.iterator();

        while(iterator.hasNext()) {
            ScaledOverlay overlay = iterator.next();
            overlay.setParent(null);
            iterator.remove();
        }
    }

    @Override
    public void moveToFront(ScaledOverlay overlay) {

        if (overlays.contains(overlay)) {
            overlays.remove(overlay);
            overlays.add(0, overlay);
        }
    }

    @Override
    public void moveToBackground(ScaledOverlay overlay) {

        if (overlays.contains(overlay)) {
            overlays.remove(overlay);
            overlays.add(overlays.size(), overlay);
        }
    }
}
