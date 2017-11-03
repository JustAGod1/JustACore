package ru.justagod.justacore.gui.overlay.animation;


import ru.justagod.justacore.gui.overlay.Overlay;

/**
 * Created by JustAGod on 20.10.17.
 */
public interface OverlayAnimator<T extends Overlay> {

    void init(T overlay);

    void update(T overlay);

    boolean isDead();
}
