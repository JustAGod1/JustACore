package ru.justagod.justacore.gui.animation;


import ru.justagod.justacore.gui.overlay.ScaledOverlay;

/**
 * Created by JustAGod on 21.10.17.
 */
public class DelayedAnimation extends AbstractOverlayAnimator<ScaledOverlay> {

    private final int delay;
    private OverlayAnimator<? extends ScaledOverlay> animator;
    private int ticks = 0;

    public DelayedAnimation(int delay, OverlayAnimator<? extends ScaledOverlay> animator) {
        this.delay = delay;
        this.animator = animator;
    }

    @Override
    public void update(ScaledOverlay overlay) {
        ticks++;
        if (ticks >= delay) {
            overlay.addAnimator(animator);
            setDead(true);
        }
    }
}
