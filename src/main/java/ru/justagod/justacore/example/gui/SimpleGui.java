package ru.justagod.justacore.example.gui;

import net.minecraft.client.gui.GuiButton;
import ru.justagod.justacore.gui.animation.MovingToAnimation;
import ru.justagod.justacore.gui.animation.QueuedAnimation;
import ru.justagod.justacore.gui.model.Color;
import ru.justagod.justacore.gui.model.Dimensions;
import ru.justagod.justacore.gui.model.Vector;
import ru.justagod.justacore.gui.overlay.*;
import ru.justagod.justacore.gui.parent.OverlayParent;
import ru.justagod.justacore.gui.screen.PichGui;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static ru.justagod.justacore.gui.helper.MathHelper.randomFloat;

/**
 * Created by JustAGod on 03.11.17.
 */
public class SimpleGui extends PichGui {

    public SimpleGui() {
        super();
        setPauseGame(false);
        clear();

        addOverlay(new ButtonOverlay(40, 40, 20, "Тык", () -> {
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
        //scrollingOverlay.setScaleSizeMode(ScaledOverlay.ScaleSizeMode.HEIGHT_EQUAL_WIDTH);
        addOverlay(scrollingOverlay);

        TextOverlay o = new TextOverlay(10, 0, "Анимация");
        addOverlay(o);


        o.addAnimator(new QueuedAnimation.Builder().append(new MovingToAnimation(40, 90, 0)).append(new MovingToAnimation(40, 10, 0)).setCycled().build());

        try {
            scrollingOverlay.addOverlay(new BufferedImageOverlay(80, 30, 40, 40, ImageIO.read(new File("/Users/Yuri/Desktop/lampa_ruka_svet_noch_117808_3840x2160.jpg"))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        scrollingOverlay.addOverlay(new TextInputOverlay(50, 60, 10, 2));

        ColorButtonOverlay button = new ColorButtonOverlay(2, 2, 10, 20, "Добавить", () -> {
            generateNew(scrollingOverlay);

        }, 1, 0.5, 0.5, 1);
        addOverlay(button);
        button.setScaleSizeMode(ScaledOverlay.ScaleSizeMode.DONT_SCALE_HEIGHT);


        for (int i = 0; i < 500; i++) {
            generateNew(scrollingOverlay);
        }
    }

    private void generateNew(OverlayParent parent) {
        ColorOverlay o = new ColorOverlay(randomFloat(0, 100), randomFloat(0, 100), 10, 10, 1, randomFloat(0, 1), randomFloat(0, 1), randomFloat(0, 1));

        o.getDragListeners().add((scaledOverlay, from, to) -> {
            Vector pos = scaledOverlay.getScaledPos().add(to.subtract(from));
            ColorOverlay newO = new ColorOverlay(pos.getX(), pos.getY(), 10, 10, 1, randomFloat(0, 1), randomFloat(0, 1), randomFloat(0, 1));

            newO.addAnimator(new QueuedAnimation.Builder().appendDelay(40).appendRemove().build());

            parent.addOverlay(newO);
        });
        o.setScaleSize(false);
        parent.addOverlay(o);

    }


}
