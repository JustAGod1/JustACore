package ru.justagod.justacore.gui.overlay.event;

import ru.justagod.justacore.gui.overlay.ScaledOverlay;

/**
 * Created by JustAGod on 04.11.17.
 */
public interface MouseScrollingListener {

    void onMouseScroll(int scrollAmount, int mouseX, int mouseY, ScaledOverlay overlay);
}
