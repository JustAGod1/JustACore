package ru.justagod.justacore.gui.overlay.parent;

/**
 * Created by JustAGod on 20.10.17.
 */
public class PanelOverlay extends AbstractPanelOverlay {

    public PanelOverlay(double x, double y) {
        super(x, y);
    }

    public PanelOverlay(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public PanelOverlay(double x, double y, double width, double height, boolean scalePosition, boolean scaleSize) {
        super(x, y, width, height, scalePosition, scaleSize);
    }


}
