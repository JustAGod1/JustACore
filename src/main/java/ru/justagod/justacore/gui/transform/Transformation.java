package ru.justagod.justacore.gui.transform;

import ru.justagod.justacore.gui.overlay.ScaledOverlay;

/**
 * Created by JustAGod on 02.11.17.
 */
public interface Transformation {

    void preTransform(ScaledOverlay scaledOverlay);

    void postTransform(ScaledOverlay scaledOverlay);
}
