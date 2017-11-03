package ru.justagod.justacore.gui.overlay.event;


import ru.justagod.justacore.gui.overlay.ScaledOverlay;

/**
 * Created by JustAGod on 03.11.17.
 */
public interface KeyboardListener {

    void onKey(ScaledOverlay overlay, char c, int keyCode);
}
