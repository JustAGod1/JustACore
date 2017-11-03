package ru.justagod.justacore.gui.overlay;

/**
 * Created by JustAGod on 15.10.17.
 */
public class ExpirableTextOverlay extends TextOverlay {


    public ExpirableTextOverlay(int x, int y, long lifeTime) {
        super(x, y);
        startDeathThread(lifeTime);
    }

    public ExpirableTextOverlay(int x, int y, String text, long lifeTime) {
        super(x, y, text);
        startDeathThread(lifeTime);
    }

    private void startDeathThread(final long life) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(life);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                parent.removeOverlay(ExpirableTextOverlay.this);
            }
        });
        t.setName("Text overlay timer");
        t.setDaemon(true);
        t.start();
    }
}
