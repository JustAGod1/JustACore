package ru.justagod.justacore.example.gui;

import net.minecraft.client.gui.GuiButton;
import ru.justagod.justacore.gui.overlay.*;
import ru.justagod.justacore.gui.overlay.animation.MovingToAnimation;
import ru.justagod.justacore.gui.overlay.animation.QueuedAnimation;
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

    @Override
    protected void actionPerformed(GuiButton p_146284_1_) {
        super.actionPerformed(p_146284_1_);
    }

    private void scrollingExample() {



        final ScrollingOverlay scrollingOverlay = new ScrollingOverlay(2, 50, 96, 48, new Dimensions(500, 500));
        //scrollingOverlay.setScaleMode(ScaledOverlay.ScaleMode.HEIGHT_EQUAL_WIDTH);
        addOverlay(scrollingOverlay);

        TextOverlay o = new TextOverlay(10, 0, "Анимация");
        addOverlay(o);

        o.addAnimator(new QueuedAnimation.Builder().append(new MovingToAnimation(40, 90, 0)).append(new MovingToAnimation(40, 10, 0)).setCycled().build());

        ColorButtonOverlay button = new ColorButtonOverlay(2, 5, 20, 20, "Добавить", () -> generateNew(scrollingOverlay), 1, 0.5, 0.5, 1);
        scrollingOverlay.addOverlay(button);

        button.setScaleMode(ScaledOverlay.ScaleMode.DONT_SCALE_HEIGHT);

        for (int i = 0; i < 100; i++) {
            generateNew(scrollingOverlay);
        }
    }

    private void generateNew(OverlayParent parent) {
        ColorOverlay o = new ColorOverlay(randomFloat(0, 100), randomFloat(0, 100), 10, 10, 1, randomFloat(0, 1), randomFloat(0, 1), randomFloat(0, 1));

        o.dragListeners.add((scaledOverlay, from, to) -> scaledOverlay.setScaledPos(to.subtract(new Vector(5, 5))));
        o.setScaleSize(false);
        o.setDoScissor(true);
        parent.addOverlay(o);

    }


}
