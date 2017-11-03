package ru.justagod.justacore.example.gui;

import ru.justagod.justacore.gui.overlay.ColorOverlay;
import ru.justagod.justacore.gui.overlay.ScaledOverlay;
import ru.justagod.justacore.gui.overlay.animation.MovingToAnimation;
import ru.justagod.justacore.gui.overlay.animation.QueuedAnimation;
import ru.justagod.justacore.gui.overlay.event.MouseDragListener;
import ru.justagod.justacore.gui.screen.PichGui;
import ru.justagod.justacore.helper.MathHelper;
import ru.justagod.justacore.helper.Vector;

/**
 * Created by JustAGod on 03.11.17.
 */
public class SimpleGui extends PichGui {

    public SimpleGui() {
        super();
        setPauseGame(false);
        clear();

        ColorOverlay overlay = new ColorOverlay(50, 50, 4, 4, 1, 0.5, 0.2);
        overlay.setScaleMode(ScaledOverlay.ScaleMode.HEIGHT_EQUAL_WIDTH);
        QueuedAnimation.Builder<ColorOverlay> builder = new QueuedAnimation.Builder<ColorOverlay>();

        addOverlay(overlay);

        for (int i = 0; i < 10; i++) {
            builder.append(new MovingToAnimation<ColorOverlay>(30, MathHelper.randomFloat(0, 96), MathHelper.randomFloat(0, 96))).appendDelay(10);
        }

        overlay.addAnimator(builder.build());

        overlay.dragListeners.add(new MouseDragListener() {
            @Override
            public void onDrag(ScaledOverlay scaledOverlay, Vector from, Vector to) {

                Vector pos = to.subtract(new Vector(scaledOverlay.getWidth() / 2, scaledOverlay.getHeight() / 2));
                scaledOverlay.setPos(pos);
                ColorOverlay o;
                addOverlay(o = new ColorOverlay(pos.getX(), pos.getY(), 4, 4, MathHelper.randomFloat(0, 1), MathHelper.randomFloat(0, 1), MathHelper.randomFloat(0, 1)));
                o.setScaleMode(ScaledOverlay.ScaleMode.HEIGHT_EQUAL_WIDTH);
                o.addAnimator(new QueuedAnimation.Builder<ColorOverlay>().appendDelay(20).appendRemove().build());
                scaledOverlay.toFront();
            }
        });
    }

    @Override
    protected void doDraw() {

    }
}
