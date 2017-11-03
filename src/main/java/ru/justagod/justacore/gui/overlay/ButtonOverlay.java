package ru.justagod.justacore.gui.overlay;

import net.minecraft.util.ResourceLocation;

/**
 * Created by JustAGod on 17.10.17.
 */
public class ButtonOverlay extends CustomButtonOverlay {
    public ButtonOverlay(double x, double y, double width, double height, String text, Runnable onClick) {
        super(x, y, width, height, new ResourceLocation("pich", "textures/gui/view_button.png"), text, onClick);
    }
}
