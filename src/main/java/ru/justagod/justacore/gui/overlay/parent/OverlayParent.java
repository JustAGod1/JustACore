package ru.justagod.justacore.gui.overlay.parent;


import ru.justagod.justacore.gui.overlay.ScaledOverlay;
import ru.justagod.justacore.gui.overlay.set.OverlaySet;

import java.util.Collection;

/**
 * Created by JustAGod on 20.10.17.
 */
public interface OverlayParent {

    void addOverlay(ScaledOverlay overlay);

    void removeOverlay(ScaledOverlay overlay);

    Collection<ScaledOverlay> getOverlays();

    void setOverlays(OverlaySet overlaySet);

    void addOverlays(Collection<ScaledOverlay> overlays);

    void appendOverlays(OverlaySet overlaySet);

    void moveUp(ScaledOverlay overlay);

    void moveDown(ScaledOverlay overlay);

    void moveToFront(ScaledOverlay overlay);

    void moveToBackground(ScaledOverlay overlay);

    double getParentWidth();

    double getParentHeight();

    double getScaledX();

    double getScaledY();

    void clear();
}
