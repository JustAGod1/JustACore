package ru.justagod.justacore.gui.overlay.animation;

import ru.justagod.illnesses.client.gui.overlay.Overlay;
import ru.justagod.illnesses.helper.Vector;

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
