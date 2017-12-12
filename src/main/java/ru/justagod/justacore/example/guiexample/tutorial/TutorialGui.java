package ru.justagod.justacore.example.guiexample.tutorial;

import net.minecraft.util.ResourceLocation;
import ru.justagod.justacore.gui.animation.MovingToAnimation;
import ru.justagod.justacore.gui.helper.DrawHelper;
import ru.justagod.justacore.gui.model.Color;
import ru.justagod.justacore.gui.model.Dimensions;
import ru.justagod.justacore.gui.model.Vector;
import ru.justagod.justacore.gui.overlay.ImageOverlay;
import ru.justagod.justacore.gui.overlay.ScaledOverlay;
import ru.justagod.justacore.gui.overlay.scrollable.VerticalScrollingOverlay;
import ru.justagod.justacore.gui.parent.AbstractPanelOverlay;
import ru.justagod.justacore.gui.screen.PichGui;
import ru.justagod.justacore.gui.transform.Transformation;

import static org.lwjgl.opengl.GL11.glColor4d;

/**
 * Created by JustAGod on 12.11.17.
 */
public class TutorialGui extends PichGui {

    public TutorialGui() {
        ScaledOverlay o = new ImageOverlay(50, 50, 25, 25, new ResourceLocation("modid", "texture"));
        addOverlay(o);

        o.getMouseClickListeners().add((x, y, overlay) -> {
            System.out.println("Нажатие!");
        });
        o.getDragListeners().add((scaledOverlay, from, to) -> {
            Vector way = to.subtract(from);
        });
        o.addAnimator(new MovingToAnimation(60, 80, 15));
        o.getTransformations().add(new Transformation() {
            @Override
            public void preTransform(ScaledOverlay scaledOverlay) {
                DrawHelper.enableAlpha();
                glColor4d(1, 0, 0, 0.4);
            }

            @Override
            public void postTransform(ScaledOverlay scaledOverlay) {
                DrawHelper.disableAlpha();
            }
        });

        AbstractPanelOverlay parent = new VerticalScrollingOverlay(50, 50, new Dimensions(3000, 3000), new Color(0.3, 0.5, 0.4, 0.2));
        addOverlay(parent);
        parent.addOverlay(o);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int lastButton, long time) {
        super.mouseClickMove(mouseX, mouseY, lastButton, time);
    }
}
