package ru.justagod.justacore.gui.animation;


import ru.justagod.justacore.gui.overlay.Overlay;
import ru.justagod.justacore.gui.model.Vector;

/**
 * Created by JustAGod on 03.11.17.
 */
public class MovingAnimation extends AbstractOverlayAnimator {
    private Vector speed;

    public MovingAnimation(Vector speed) {
        this.speed = speed;
    }

    @Override
    public void update(Overlay overlay) {
        Vector newPos = overlay.getPos().add(speed);

        overlay.setPos(newPos);
    }
}
