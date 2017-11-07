package ru.justagod.justacore.gui.animation;


import ru.justagod.justacore.gui.overlay.ScaledOverlay;

/**
 * Created by JustAGod on 21.10.17.
 */
public class ExpirationAnimation extends AbstractOverlayAnimator<ScaledOverlay> {

    private static final double step = 1;

    @Override
    public void update(ScaledOverlay overlay) {
        overlay.setX(overlay.getX() - 0.6);
        if (overlay.getScaledX() + overlay.getScaledWidth() <= 5) {
            setDead(true);
            overlay.remove();
        }
    }
}
