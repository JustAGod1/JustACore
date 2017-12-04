package ru.justagod.justacore.gui.model;

import ru.justagod.justacore.gui.overlay.ScaledOverlay;
import ru.justagod.justacore.gui.parent.OverlayParent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by JustAGod on 14.11.17.
 */
public class OverlaysCollection {

    private final Object lock = new Object();
    private final List<ScaledOverlay> overlays;
    private final OverlayParent defaultParent;
    private final Queue<Runnable> postQueue = new LinkedBlockingQueue<>();

    public OverlaysCollection(OverlayParent defaultParent) {
        overlays = new ArrayList<>();
        this.defaultParent = defaultParent;
    }

    public OverlaysCollection(List<ScaledOverlay> overlays, OverlayParent defaultParent) {
        this.overlays = overlays;
        this.defaultParent = defaultParent;
    }


    public void downIterate(Callback<ScaledOverlay, Boolean> callback) {
        synchronized (lock) {
            for (int i = overlays.size() - 1; i >= 0; i--) {
                ScaledOverlay overlay = overlays.get(i);

                if (!callback.call(overlay)) break;
            }
        }
    }

    public void iterate(Callback<ScaledOverlay, Boolean> callback) {
        synchronized (lock) {
            for (ScaledOverlay overlay : overlays) {
                if (!callback.call(overlay)) {
                    break;
                }
            }
        }
    }

    public void update() {
        synchronized (lock) {
            for (ScaledOverlay overlay : overlays) {
                overlay.update();
            }
            while (postQueue.size() > 0) {
                postQueue.poll().run();
            }

        }
    }

    public void remove(ScaledOverlay overlay) {
        if (overlays.contains(overlay)) {
            postQueue.add(() -> {
                    overlay.onDestroy();
                    overlay.setParent(null);
                    overlays.remove(overlay);
            });
        }
    }

    public void add(ScaledOverlay overlay) {
        postQueue.add(() -> {
            overlay.setParent(defaultParent);
            overlay.onActivate();
            overlays.add(overlay);
        });
    }

    public void moveUp(ScaledOverlay overlay) {

        postQueue.add(() -> {
            int index = overlays.indexOf(overlay);
            if (overlays.contains(overlay)) {
                overlays.remove(index);
                overlays.add(Math.min(index - 1, 0), overlay);
            }
        });

    }

    public void moveDown(ScaledOverlay overlay) {

        postQueue.add(() -> {
            int index = overlays.indexOf(overlay);
            if (overlays.contains(overlay)) {
                overlays.remove(index);
                overlays.add(Math.max(index + 1, overlays.size()), overlay);
            }
        });
    }

    public void clear() {
        postQueue.add(() -> {
            Iterator<ScaledOverlay> iterator = overlays.iterator();
            while (iterator.hasNext()) {
                ScaledOverlay overlay = iterator.next();
                overlay.onDestroy();
                overlay.setParent(null);
                iterator.remove();
            }
        });
    }

    public void moveToFront(ScaledOverlay overlay) {
        postQueue.add(() -> {
            if (overlays.contains(overlay)) {
                overlays.remove(overlay);
                overlays.add(0, overlay);
            }
        });
    }

    public void moveToBackground(ScaledOverlay overlay) {
        postQueue.add(() -> {
            if (overlays.contains(overlay)) {
                overlays.remove(overlay);
                overlays.add(overlays.size(), overlay);
            }
        });
    }

    public void addToPost(Runnable runnable) {
        postQueue.add(runnable);
    }

    @Override
    public String toString() {
        return "OverlaysCollection{" +
                "overlays=" + overlays +
                ", postQueue=" + postQueue +
                '}';
    }
}
