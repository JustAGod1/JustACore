package ru.justagod.justacore.example.gui;

import ru.justagod.justacore.gui.overlay.*;
import ru.justagod.justacore.gui.overlay.event.MouseDragListener;
import ru.justagod.justacore.gui.overlay.parent.OverlayParent;
import ru.justagod.justacore.gui.screen.PichGui;
import ru.justagod.justacore.helper.Dimensions;
import ru.justagod.justacore.helper.Vector;

import static ru.justagod.justacore.helper.MathHelper.randomFloat;

/**
 * Created by JustAGod on 03.11.17.
 */
public class SimpleGui extends PichGui {

    public SimpleGui() {
        super();
        setPauseGame(false);
        clear();

        scrollingExample();

    }

    private void scrollingExample() {



        final ScrollingOverlay scrollingOverlay = new ScrollingOverlay(2, 50, 96, 48, new Dimensions(1000, 1000));
        //scrollingOverlay.setScaleMode(ScaledOverlay.ScaleMode.HEIGHT_EQUAL_WIDTH);
        addOverlay(scrollingOverlay);

        addOverlay(new TextOverlay(0, 0, "Хлюп"));
        scrollingOverlay.addOverlay(new ColorButtonOverlay(0, 0, 10, 4, "Хлюп", new Runnable() {
            @Override
            public void run() {
                generateNew(scrollingOverlay);
            }
        }, 1, 0.5, 0.5, 1));

        for (int i = 0; i < 1; i++) {
            generateNew(scrollingOverlay);
        }
    }

    private void generateNew(OverlayParent parent) {
        ColorOverlay o = new ColorOverlay(randomFloat(0, 100), randomFloat(0, 100), 10, 10, 1, randomFloat(0, 1), randomFloat(0, 1), randomFloat(0, 1));

        o.dragListeners.add(new MouseDragListener() {
            @Override
            public void onDrag(ScaledOverlay scaledOverlay, Vector from, Vector to) {
                scaledOverlay.setScaledPos(to.subtract(new Vector(5, 5)));
            }
        });
        o.setScaleSize(false);
        parent.addOverlay(o);

    }


}
