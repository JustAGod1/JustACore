package ru.justagod.justacore.gui.overlay.animation;


import ru.justagod.justacore.gui.overlay.ScaledOverlay;
import ru.justagod.justacore.helper.Vector;

import static ru.justagod.justacore.helper.MathHelper.compareDouble;

/**
 * Created by JustAGod on 02.11.17.
 */
public class MovingToAnimation<T extends ScaledOverlay> extends AbstractOverlayAnimator<T> {

    private int time = 30;
    private double speed;

    private Vector destination;

    private Vector step;

    public MovingToAnimation(int time, double newX, double newY) {
        this.time = time;
        destination = new Vector(newX, newY);
    }



    @Override
    public void init(ScaledOverlay overlay) {
        Vector old = overlay.getPos();

        Vector way = destination.subtract(old);
        double distance = way.length();
        speed = distance / time;


    }

    @Override
    public void update(ScaledOverlay overlay) {
        Vector old = overlay.getPos();

        Vector way = destination.subtract(old);
        step = way.normalize();
        if (compareDouble(overlay.getX(), destination.getX()) && compareDouble(overlay.getY(), destination.getY())) {
            setDead(true);
            return;
        }

        Vector newPos = overlay.getPos().add(step.multiply(speed));
        if (destination.subtract(newPos).length() < step.length()) {
            newPos = destination;
        }
        overlay.setX(newPos.getX());
        overlay.setY(newPos.getY());
    }
}
