package ru.justagod.justacore.gui.overlay.common.button;

import net.minecraft.util.ResourceLocation;
import ru.justagod.justacore.gui.overlay.ScaledOverlay;

/**
 * Created by JustAGod on 17.10.17.
 */
public class ButtonOverlay extends CustomButtonOverlay {
    public ButtonOverlay(double x, double y, double width, String text, Runnable onClick) {
        super(x, y, width, 20, text, onClick, new ResourceLocation("jac", "textures/gui/gui_button.png"));
        setScaleSizeMode(ScaledOverlay.ScaleSizeMode.DONT_SCALE_HEIGHT);
    }
}
