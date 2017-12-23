package ru.justagod.justacore.gui.animation;

import ru.justagod.justacore.gui.overlay.Overlay;

/**
 * Created by JustAGod on 07.12.17.
 */
public class CustomActionAnimation extends AbstractOverlayAnimator{

    private Runnable action;

    public CustomActionAnimation(Runnable action) {
        this.action = action;
    }

    @Override
    public void init(Overlay overlay) {
        if (action != null) {
            action.run();
        }
        setDead(true);
    }

    @Override
    public void update(Overlay overlay) {

    }
}
