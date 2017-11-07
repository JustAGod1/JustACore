package ru.justagod.justacore.gui.animation;


import ru.justagod.justacore.gui.overlay.Overlay;

/**
 * Created by JustAGod on 20.10.17.
 */
public abstract class AbstractOverlayAnimator<T extends Overlay> implements OverlayAnimator<T> {
    private boolean dead;

    @Override
    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    @Override
    public void init(T overlay) {

    }
}
