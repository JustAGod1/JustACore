package ru.justagod.justacore.example.gui;

import ru.justagod.justacore.gui.overlay.ColorOverlay;
import ru.justagod.justacore.gui.overlay.ScaledOverlay;
import ru.justagod.justacore.gui.overlay.ScrollingOverlay;
import ru.justagod.justacore.gui.screen.PichGui;
import ru.justagod.justacore.helper.Dimensions;

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



        ScrollingOverlay scrollingOverlay = new ScrollingOverlay(25, 25, 50, 50, new Dimensions(300, 300));
        addOverlay(scrollingOverlay);
        for (int i = 0; i < 43; i++) {
            ColorOverlay o = new ColorOverlay(randomFloat(0, 100), randomFloat(0, 100), 4, 4, randomFloat(0, 1), randomFloat(0, 1), randomFloat(0, 1));
            o.setScaleMode(ScaledOverlay.ScaleMode.WIDTH_EQUAL_HEIGHT);
            scrollingOverlay.addOverlay(o);
        }
    }

    @Override
    protected void doDraw() {

    }
}
