package ru.justagod.justacore.gui.overlay;

import net.minecraft.client.resources.I18n;

/**
 * Created by JustAGod on 04.11.17.
 */
public abstract class AbstractButtonOverlay extends ScaledOverlay {

    protected String text;
    private Runnable onClick;


    public AbstractButtonOverlay(double x, double y, String text, final Runnable onClick) {
        this(x, y, 20, 2, text, onClick);
        setDoScissor(true);
    }

    public AbstractButtonOverlay(double x, double y, double width, double height, String text, final Runnable onClick) {
        super(x, y, width, height);
        this.text = text;
        this.onClick = onClick;
        setDoScissor(true);

    }

    public AbstractButtonOverlay localize() {
        text = I18n.format(text);
        return this;
    }

    @Override
    protected boolean doClick(int x, int y) {
        onClick.run();
        return true;
    }
}
