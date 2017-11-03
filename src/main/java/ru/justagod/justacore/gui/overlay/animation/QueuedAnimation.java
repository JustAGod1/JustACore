package ru.justagod.justacore.gui.overlay.animation;

import ru.justagod.illnesses.client.gui.overlay.Overlay;
import ru.justagod.illnesses.client.gui.overlay.ScaledOverlay;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by JustAGod on 02.11.17.
 */
public class QueuedAnimation<T extends Overlay> extends AbstractOverlayAnimator<T> {

    private Queue<OverlayAnimator<T>> queue;

    private OverlayAnimator<T> current;
    public QueuedAnimation(Queue<OverlayAnimator<T>> queue) {
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

            if (current.isDead()) takeNext(overlay);
        }
    }

    private void takeNext(T overlay) {
        current = queue.poll();
        if (current != null) {
            current.init(overlay);
        } else {
            setDead(true);
        }
    }

    public static class Builder<T extends ScaledOverlay> {
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

        public QueuedAnimation<T> build() {
            return new QueuedAnimation<T>(new ArrayBlockingQueue<OverlayAnimator<T>>(animators.size(), true, animators));
        }
    }
}
