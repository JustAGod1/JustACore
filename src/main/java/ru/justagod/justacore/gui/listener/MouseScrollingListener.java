package ru.justagod.justacore.gui.listener;

import ru.justagod.justacore.gui.overlay.ScaledOverlay;

/**
 * Created by JustAGod on 04.11.17.
 */
public interface MouseScrollingListener {

    void onMouseScroll(int scrollAmount, double mouseX, double mouseY, ScaledOverlay overlay);
}
