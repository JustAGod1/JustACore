package ru.justagod.justacore.gui.animation;


import ru.justagod.justacore.gui.overlay.ScaledOverlay;

/**
 * Created by JustAGod on 02.11.17.
 */
public class DeleteAnimation extends AbstractOverlayAnimator<ScaledOverlay> {

    @Override
    public void init(ScaledOverlay overlay) {
        overlay.remove();
    }

    @Override
    public void update(ScaledOverlay overlay) {

    }
}
