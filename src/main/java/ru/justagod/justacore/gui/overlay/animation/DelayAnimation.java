package ru.justagod.justacore.gui.overlay.animation;


import ru.justagod.justacore.gui.overlay.Overlay;

/**
 * Created by JustAGod on 02.11.17.
 */
public class DelayAnimation extends AbstractOverlayAnimator {

    private int time;
    private int ticks = 0;

    public DelayAnimation(int time) {
        this.time = time;
    }

    @Override
    public void update(Overlay overlay) {
        ticks++;

        if (ticks >= time) setDead(true);
    }
}
