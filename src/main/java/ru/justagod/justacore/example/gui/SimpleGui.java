package ru.justagod.justacore.example.gui;

import net.minecraft.client.gui.GuiButton;
import ru.justagod.justacore.gui.model.Color;
import ru.justagod.justacore.gui.overlay.*;
import ru.justagod.justacore.gui.animation.MovingToAnimation;
import ru.justagod.justacore.gui.animation.QueuedAnimation;
import ru.justagod.justacore.gui.parent.AbstractPanelOverlay;
import ru.justagod.justacore.gui.parent.OverlayParent;
import ru.justagod.justacore.gui.screen.PichGui;
import ru.justagod.justacore.gui.model.Dimensions;
import ru.justagod.justacore.gui.model.Vector;

import static ru.justagod.justacore.gui.helper.MathHelper.randomFloat;

/**
 * Created by JustAGod on 03.11.17.
 */
public class SimpleGui extends PichGui {

    public SimpleGui() {
        super();
        setPauseGame(false);
        clear();

        addOverlay(new ButtonOverlay(40, 40, 20, "Тык", ()->{
            clear();
            scrollingExample();
        }));

    }

    @Override
    protected void actionPerformed(GuiButton p_146284_1_) {
        super.actionPerformed(p_146284_1_);
    }

    private void scrollingExample() {



        final HorizontalScrollingOverlay scrollingOverlay = new HorizontalScrollingOverlay(2, 50, 96, 48, new Dimensions(2000, 2000), new Color(0.2, 0.2, 0.2, 0.2));
        //scrollingOverlay.setScaleMode(ScaledOverlay.ScaleMode.HEIGHT_EQUAL_WIDTH);
        addOverlay(scrollingOverlay);

        TextOverlay o = new TextOverlay(10, 0, "Анимация");
        addOverlay(o);

        o.addAnimator(new QueuedAnimation.Builder().append(new MovingToAnimation(40, 90, 0)).append(new MovingToAnimation(40, 10, 0)).setCycled().build());


        scrollingOverlay.addOverlay(new TextInputOverlay(50, 60, 10, 2));
        AbstractPanelOverlay parent = new VerticalScrollingOverlay(2, 2, 96, 48, new Dimensions(2000, 2000), new Color(0.2, 0.2, 0.2, 1));
        scrollingOverlay.addOverlay(parent);

        ColorButtonOverlay button = new ColorButtonOverlay(2, 30, 20, 20, "Добавить", () -> {
            generateNew(scrollingOverlay);
            parent.toFront();

        }, 1, 0.5, 0.5, 1);
        scrollingOverlay.addOverlay(button);
        button.setScaleMode(ScaledOverlay.ScaleMode.DONT_SCALE_HEIGHT);


        for (int i = 0; i < 500; i++) {
            generateNew(parent);
        }
    }

    private void generateNew(OverlayParent parent) {
        ColorOverlay o = new ColorOverlay(randomFloat(0, 100), randomFloat(0, 100), 10, 10, 1, randomFloat(0, 1), randomFloat(0, 1), randomFloat(0, 1));

        o.dragListeners.add((scaledOverlay, from, to) -> scaledOverlay.setScaledPos(to.subtract(new Vector(5, 5))));
        o.setScaleSize(false);
        parent.addOverlay(o);

    }


}
