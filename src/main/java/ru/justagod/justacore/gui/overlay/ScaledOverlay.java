package ru.justagod.justacore.gui.overlay;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import ru.justagod.justacore.gui.helper.DrawHelper;
import ru.justagod.justacore.gui.helper.DrawHelper.CursorType;
import ru.justagod.justacore.gui.helper.ScissorHelper;
import ru.justagod.justacore.gui.listener.*;
import ru.justagod.justacore.gui.model.Rect;
import ru.justagod.justacore.gui.model.Vector;
import ru.justagod.justacore.gui.parent.OverlayParent;
import ru.justagod.justacore.gui.transform.Transformation;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.glDisable;
import static ru.justagod.justacore.gui.overlay.ScaledOverlay.ScaleMode.NORMAL;

/**
 * Created by JustAGod on 16.10.17.
 */
public abstract class ScaledOverlay extends Overlay {

    public static final int BOUND = 100;


    // Listeners
    public final List<Transformation> transformations = new ArrayList<>();
    public final List<MouseClickListener> mouseDoubleClickListeners = new ArrayList<>();
    public final List<MouseClickListener> mouseClickListeners = new ArrayList<>();
    public final List<MouseHoverListener> mouseHoverListeners = new ArrayList<>();
    public final List<KeyboardListener> keyboardListeners = new ArrayList<>();
    public final List<MouseDragListener> dragListeners = new ArrayList<>();
    public final List<MouseScrollingListener> scrollingListeners = new ArrayList<>();


    protected double width;
    protected double height;
    protected boolean scalePosition = true;
    protected boolean scaleSize = true;
    protected boolean doScissor = false;
    protected boolean isFocused;
    protected boolean isMouseInside;
    protected ScaleMode scaleMode = NORMAL;
    protected CursorType cursor = CursorType.NORMAL;

    protected OverlayParent parent;

    private long lastClickTime;

    public ScaledOverlay(double x, double y) {
        super(x, y);
    }

    public ScaledOverlay(double x, double y, double width, double height) {
        super(x, y);
        this.width = width;
        this.height = height;
    }

    public ScaledOverlay(double x, double y, double width, double height, boolean scalePosition, boolean scaleSize) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.scalePosition = scalePosition;
        this.scaleSize = scaleSize;
    }

    public boolean onKey(char key, int code) {
        if (isFocused) {
            if (keyboardListeners.size() > 0) {
                for (KeyboardListener listener : keyboardListeners) {
                    listener.onKey(this, key, code);
                }

                return true;
            }
            doOnKey(key, code);
        }
        return false;
    }

    public void doOnKey(char key, int code) {

    }

    public boolean isDoScissor() {
        return doScissor;
    }


    /**
     * Использовать очень аккуратно! Сильно замедляет отрисовку.
     *
     * @param doScissor использовать ли glScissor
     */
    public void setDoScissor(boolean doScissor) {
        this.doScissor = doScissor;
    }

    public double getScaledX() {
        return (isScalePosition() ? getX() * getXFactor() : x) + parent.getScaledX();
    }

    public void setScaledX(double scaledX) {
        x = isScalePosition() ? (scaledX - parent.getScaledX()) / getXFactor() : scaledX - parent.getScaledX();
    }

    protected double getScaledY() {
        return (isScalePosition() ? getY() * getYFactor() : y) + parent.getScaledY();
    }

    public void setScaledY(double scaledY) {
        y = isScalePosition() ? (scaledY - parent.getScaledY()) / getYFactor() : scaledY - parent.getScaledY();
    }

    public double getXFactor() {
        return parent.getParentWidth() / BOUND;
    }

    public double getYFactor() {
        return parent.getParentHeight() / BOUND;
    }

    public double getScaledWidth() {
        if (isScaleSize()) {
            switch (scaleMode) {
                case NORMAL:
                    return width * getXFactor();
                case DONT_SCALE_WIDTH:
                    return width;
                case WIDTH_EQUAL_HEIGHT:
                    return getScaledHeight();
                default:
                    return width * getXFactor();
            }
        } else {
            return width;
        }
    }

    /**
     * Необходимо переопределить
     *
     * @return область в которой считается что мышка внутри
     */
    public Rect getMouseRect() {


        return new Rect(getScaledX(), getScaledY(), getScaledX() + getScaledWidth(), getScaledY() + getScaledHeight());
    }


    /**
     * Расчитывает высоту элемента согласно всем настройкам
     *
     * @return абсолютная высота элемента
     * @see #setScaleMode(ScaleMode)
     * @see #setScaleSize(boolean)
     */
    public double getScaledHeight() {
        if (isScaleSize()) {
            switch (scaleMode) {
                case NORMAL:
                    return height * getYFactor();
                case DONT_SCALE_HEIGHT:
                    return height;
                case HEIGHT_EQUAL_WIDTH:
                    return getScaledWidth();
                default:
                    return height * getYFactor();
            }
        } else {
            return height;
        }
    }

    public boolean isFocused() {
        return isFocused;
    }

    public void setFocused(boolean focused) {
        isFocused = focused;
    }

    public ScaleMode getScaleMode() {
        return scaleMode;
    }

    public void setScaleMode(ScaleMode scaleMode) {
        this.scaleMode = scaleMode;
    }

    public boolean isScalePosition() {
        return scalePosition;
    }


    /**
     * Использовать относительную или абсолютную позицию
     *
     * @param scalePosition true/false
     */
    public void setScalePosition(boolean scalePosition) {
        this.scalePosition = scalePosition;
    }

    public boolean isScaleSize() {
        return scaleSize;
    }

    public void setScaleSize(boolean scaleSize) {
        this.scaleSize = scaleSize;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    protected void runTransformations() {
        for (Transformation transformation : transformations) {
            transformation.preTransform(this);
        }
    }

    /**
     * @param mouseX       абсолютная позиция курсора по X
     * @param mouseY       абсолютная позиция курсора по Y
     * @param scrollAmount равно Mouse.getDWheel()
     * @return остановить ли вызов этого метода у других элементов родителя
     */
    public boolean onMouseScroll(double mouseX, double mouseY, int scrollAmount) {

        if (getMouseRect().isVectorInBounds(new Vector(mouseX, mouseY))) {
            for (MouseScrollingListener listener : scrollingListeners) {
                listener.onMouseScroll(scrollAmount, mouseX, mouseY, this);
            }
            return doMouseScroll(mouseX, mouseY, scrollAmount) || scrollingListeners.size() > 0;
        }
        return false;
    }

    /**
     * @param mouseX       абсолютная позиция курсора по X
     * @param mouseY       абсолютная позиция курсора по Y
     * @param scrollAmount равно Mouse.getDWheel()
     * @return остановить ли вызов этого метода у других элементов родителя
     * @see OverlayParent
     * @see #getMouseRect()
     * @see #parent
     */
    protected boolean doMouseScroll(double mouseX, double mouseY, int scrollAmount) {
        return false;
    }

    /**
     * @param partialTick время которое прошло от предыдущего тика деленое на 20
     * @param mouseX      абсолютная позиция курсора по X
     * @param mouseY      абсолютная позиция курсора по Y
     */
    @Override
    public synchronized void draw(float partialTick, int mouseX, int mouseY) {
        if (!ScissorHelper.isInScissorRect(getRenderRect())) {
            return;
        }
        GL11.glColor3d(1, 1, 1);
        glDisable(GL11.GL_CULL_FACE);

        final boolean flag = isDoScissor();
        if (isMouseInside = isInMouseRect(mouseX, mouseY)) {
            for (MouseHoverListener listener : mouseHoverListeners) {
                listener.onHover(mouseX - getScaledX(), mouseY - getScaledY(), this);
            }
            CursorType cursor = getCursorType(mouseX, mouseY);
            if (cursor != CursorType.NONE) {
                DrawHelper.bindCursor(cursor);
            }

        }
        runTransformations();
        if (flag) {
            ScissorHelper.push();
            ScissorHelper.relativeScissor(getScaledX() / getScreenScaledWidth(), getScaledY() / getScreenScaledHeight(), getScaledWidth() / getScreenScaledWidth(), getScaledHeight() / getScreenScaledHeight());

        }

        doDraw(getScaledX(), getScaledY(), getScaledWidth(), getScaledHeight(), partialTick, mouseX, mouseY, isInMouseRect(mouseX, mouseY));

        GL11.glEnable(GL11.GL_CULL_FACE);
        if (flag) {
            ScissorHelper.pop();
        }
    }

    /**
     * @param lastMouseX предыдущая позиция курсора по X
     * @param lastMouseY предыдущая позиция курсора по Y
     * @param mouseX     текущая позиция курсора по X
     * @param mouseY     текущая позиция курсора по Y
     * @return остановить ли вызов этого метода у других элементов родителя
     * @see #getMouseRect()
     */
    public boolean onMouseDrag(int lastMouseX, int lastMouseY, int mouseX, int mouseY) {
        if (isInMouseRect(lastMouseX, lastMouseY)) {
            for (MouseDragListener listener : dragListeners) {
                listener.onDrag(this, new Vector(lastMouseX, lastMouseY), new Vector(mouseX, mouseY));
            }
            return doMouseDrag(lastMouseX, lastMouseY, mouseX, mouseY) || dragListeners.size() > 0;
        }
        return false;
    }

    /**
     * Вызывается если курсор внутри {@link #getMouseRect()}
     *
     * @param lastMouseX предыдущая позиция курсора по X
     * @param lastMouseY предыдущая позиция курсора по Y
     * @param mouseX     текущая позиция курсора по X
     * @param mouseY     текущая позиция курсора по Y
     * @return остановить ли вызов этого метода у других элементов родителя
     */
    protected boolean doMouseDrag(int lastMouseX, int lastMouseY, int mouseX, int mouseY) {
        return false;
    }

    protected boolean isInMouseRect(double x, double y) {
        return getMouseRect().isVectorInBounds(new Vector(x, y));
    }

    public int getScreenScaledWidth() {
        return getResolution().getScaledWidth();
    }

    public int getScreenScaledHeight() {
        return getResolution().getScaledHeight();
    }

    public ScaledResolution getResolution() {
        return new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    }

    /**
     * Вызывается родителем при нажатии ПКМ, ЛКМ или колесика
     *
     * @param mouseX абсолютна позиция курсора по X
     * @param mouseY абсолютная позиция курсора по Y
     * @return остановить ли вызов этого метода у других элементов родителя
     */
    public boolean onClick(int mouseX, int mouseY) {
        if (isPointInBounds(mouseX, mouseY)) {
            for (MouseClickListener listener : mouseClickListeners) {
                listener.onClick(mouseX - getScaledX(), mouseY - getScaledY(), this);

            }
            if (System.currentTimeMillis() - lastClickTime <= 200) {
                for (MouseClickListener listener : mouseDoubleClickListeners) {
                    listener.onClick(mouseX - getScaledX(), mouseY - getScaledY(), this);

                }
                lastClickTime = 0;
                doDoubleClick(mouseX, mouseY);
            } else {
                doClick(mouseX, mouseY);
                lastClickTime = System.currentTimeMillis();
            }
            isFocused = true;
            return true;
        }
        isFocused = false;
        return false;
    }


    /**
     * Вызывается при нажатии если в облости нажатия
     *
     * @param mouseX абсолютна позиция курсора по X
     * @param mouseY абсолютная позиция курсора по Y
     * @return остановить ли вызов этого метода у других элементов родителя
     * @see #getMouseRect()
     */
    protected boolean doClick(int mouseX, int mouseY) {
        return false;
    }

    protected boolean doDoubleClick(int mouseX, int mouseY) {
        return false;
    }

    @Override
    public synchronized void drawText(float partialTick, int mouseX, int mouseY) {
        if (!ScissorHelper.isInScissorRect(getRenderRect())) {
            return;
        }
        GL11.glColor4d(1, 1, 1, 1);
        glDisable(GL11.GL_CULL_FACE);

        final boolean flag = isDoScissor();
        if (isMouseInside = isPointInBounds(mouseX, mouseY)) {
            for (MouseHoverListener listener : mouseHoverListeners) {
                listener.onHover(mouseX - getScaledX(), mouseY - getScaledY(), this);
            }
            CursorType cursor = getCursorType(mouseX, mouseY);
            if (cursor != CursorType.NONE) {
                DrawHelper.bindCursor(cursor);
            }
        }
        runTransformations();
        if (flag) {
            ScissorHelper.push();
            ScissorHelper.relativeScissor(getScaledX() / getScreenScaledWidth(), getScaledY() / getScreenScaledHeight(), getScaledWidth() / getScreenScaledWidth(), getScaledHeight() / getScreenScaledHeight());

        }

        doDrawText(getScaledX(), getScaledY(), getScaledWidth(), getScaledHeight(), partialTick, mouseX, mouseY, isInMouseRect(mouseX, mouseY));

        GL11.glEnable(GL11.GL_CULL_FACE);
        if (flag) {
            ScissorHelper.pop();
        }

    }

    protected boolean isPointInBounds(int x, int y) {
        return x >= getScaledX() && y >= getScaledY() && x <= getScaledX() + getScaledWidth() && y <= getScaledY() + getScaledHeight();
    }

    protected abstract void doDraw(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds);

    protected abstract void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds);

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public OverlayParent getParent() {
        return parent;
    }

    public synchronized void setParent(OverlayParent parent) {
        this.parent = parent;
    }

    public void remove() {
        getParent().removeOverlay(this);
    }

    @Override
    public String toString() {
        return "\nScaledOverlay{" +
                "\ntransformations=" + transformations +
                "\n, mouseClickListeners=" + mouseClickListeners +
                "\n, mouseHoverListeners=" + mouseHoverListeners +
                "\n, keyboardListeners=" + keyboardListeners +
                "\n, dragListeners=" + dragListeners +
                "\n, width=" + width +
                "\n, height=" + height +
                "\n, scalePosition=" + scalePosition +
                "\n, scaleSize=" + scaleSize +
                "\n, doScissor=" + doScissor +
                "\n, isFocused=" + isFocused +
                "\n, scaleMode=" + scaleMode +
                "\n, parent=" + parent +
                "\n, scaledDimensions=" + getDimensions() +
                "\n} " + super.toString();
    }

    public Vector getScaledPos() {
        return new Vector(getScaledX(), getScaledY());
    }

    public void setScaledPos(Vector scaledPos) {
        setScaledX(scaledPos.getX());
        setScaledY(scaledPos.getY());
    }

    protected CursorType getCursorType(int mouseX, int mouseY) {
        return cursor;
    }

    public Vector getDimensions() {
        return new Vector(getScaledWidth(), getScaledHeight());
    }

    protected Rect getRenderRect() {
        return new Rect(getScaledX(), getScaledY(), getScaledX() + getScaledWidth(), getScaledY() + getScaledHeight());
    }

    public CursorType getCursor() {
        return cursor;
    }

    public void setCursor(CursorType cursor) {
        this.cursor = cursor;
    }

    public void toFront() {
        parent.moveToFront(this);
    }

    public void toBackground() {
        parent.moveToBackground(this);
    }

    public enum ScaleMode {
        WIDTH_EQUAL_HEIGHT, HEIGHT_EQUAL_WIDTH, NORMAL, DONT_SCALE_WIDTH, DONT_SCALE_HEIGHT
    }

    public boolean isMouseInside() {
        return isMouseInside;
    }
}
