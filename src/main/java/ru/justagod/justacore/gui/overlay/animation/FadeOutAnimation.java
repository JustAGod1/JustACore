package ru.justagod.justacore.gui.overlay.animation;

import ru.justagod.illnesses.client.gui.overlay.ScaledOverlay;
import ru.justagod.illnesses.client.gui.overlay.transform.ColorTransformation;

/**
 * Created by JustAGod on 02.11.17.
 */
public class FadeOutAnimation extends AbstractOverlayAnimator<ScaledOverlay> {

    private ColorTransformation transformation = new ColorTransformation(1, 1, 1, 1);

    @Override
    public void init(ScaledOverlay overlay) {
        overlay.transformations.add(transformation);
    }

    @Override
    public void update(ScaledOverlay overlay) {
        transformation.setAlpha(transformation.getAlpha() - 0.01);

        if (transformation.getAlpha() <= 0) {
            overlay.remove();
            setDead(true);
        }
    }
}
