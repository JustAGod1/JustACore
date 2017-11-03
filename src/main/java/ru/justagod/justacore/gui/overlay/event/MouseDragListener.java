package ru.justagod.justacore.gui.overlay.event;

import ru.justagod.justacore.gui.overlay.ScaledOverlay;
import ru.justagod.justacore.helper.Vector;

/**
 * Created by JustAGod on 03.11.17.
 */
public interface MouseDragListener {

    void onDrag(ScaledOverlay scaledOverlay, Vector from, Vector to);
}
