package ru.justagod.justacore.gui.overlay.animation;


import ru.justagod.justacore.gui.overlay.Overlay;
import ru.justagod.justacore.gui.overlay.ScaledOverlay;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustAGod on 02.11.17.
 */
public class QueuedAnimation<T extends Overlay> extends AbstractOverlayAnimator<T> {

    private List<OverlayAnimator<T>> queue;
    private boolean cycled = false;

    private OverlayAnimator<T> current;
    private int pos = -1;

    public QueuedAnimation(List<OverlayAnimator<T>> queue, boolean cycled) {
        this.queue = queue;
        this.cycled = cycled;
    }

    public QueuedAnimation(List<OverlayAnimator<T>> queue) {
        this.queue = queue;
        if (queue.size() == 0) setDead(true);
    }

    @Override
    public void init(T overlay) {
        takeNext(overlay);
    }

    @Override
    public void update(T overlay) {
        if (current != null) {
            current.update(overlay);

            if (current.isDead())
                takeNext(overlay);
        }
    }

    private void takeNext(T overlay) {
        pos = (pos + 1);
        if (pos < queue.size()) {
            current = queue.get(pos);
            current.init(overlay);
        } else if (cycled) {

            pos = 0;
            current = queue.get(pos);
            current.init(overlay);
        } else {
            setDead(true);
        }
    }

    public static class Builder<T extends ScaledOverlay> {
        private boolean cycled;
        private List<OverlayAnimator<T>> animators = new LinkedList<OverlayAnimator<T>>();

        public Builder<T> append(OverlayAnimator<T> animator) {
            animators.add(animator);
            return this;
        }

        public Builder<T> appendDelay(int ticks) {
            animators.add(new DelayAnimation(ticks));
            return this;
        }

        public Builder<T> appendRemove() {
            animators.add((OverlayAnimator<T>) new DeleteAnimation());
            return this;
        }

        public Builder<T> setCycled() {
            cycled = true;
            return this;
        }

        public QueuedAnimation<T> build() {
            return new QueuedAnimation<T>(new ArrayList<OverlayAnimator<T>>(animators), cycled);
        }
    }
}
