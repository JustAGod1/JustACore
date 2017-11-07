package ru.justagod.justacore.gui.overlay;

import net.minecraft.util.ResourceLocation;

/**
 * Created by JustAGod on 17.10.17.
 */
public class ButtonOverlay extends CustomButtonOverlay {
    public ButtonOverlay(double x, double y, double width, String text, Runnable onClick) {
        super(x, y, width, 20, text, onClick, new ResourceLocation("jac", "textures/gui/gui_button.png"));
        setScaleMode(ScaleMode.DONT_SCALE_HEIGHT);
    }
}
