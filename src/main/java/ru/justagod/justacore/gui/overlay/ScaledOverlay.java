package ru.justagod.justacore.gui.overlay;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import ru.justagod.justacore.gui.helper.DrawHelper;
import ru.justagod.justacore.gui.helper.DrawHelper.CursorType;
import ru.justagod.justacore.gui.helper.ScissorHelper;
import ru.justagod.justacore.gui.listener.*;
import ru.justagod.justacore.gui.model.Dimensions;
import ru.justagod.justacore.gui.model.Rect;
import ru.justagod.justacore.gui.model.Vector;
import ru.justagod.justacore.gui.parent.OverlayParent;
import ru.justagod.justacore.gui.transform.Transformation;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.glDisable;

/**
 * Created by JustAGod on 16.10.17.
 */
public abstract class ScaledOverlay extends Overlay {

    public static final int BOUND = 100;



    protected List<Transformation> transformations;

    // Listeners
    private List<MouseClickListener> mouseDoubleClickListeners;
    private List<MouseClickListener> mouseClickListeners;
    private List<MouseHoverListener> mouseHoverListeners;
    private List<KeyboardListener> keyboardListeners;
    private List<MouseDragListener> dragListeners;
    private List<MouseScrollingListener> scrollingListeners;
    private List<MouseEnterListener> mouseEnterListeners;
    private List<MouseLeaveListener> mouseLeaveListeners;


    protected double width;
    protected double height;
    protected boolean scalePosition = true;
    protected boolean scaleSize = true;
    protected boolean doScissor = false;
    protected boolean isFocused;
    protected boolean isMouseInside;
    protected ScaleSizeMode scaleSizeMode = ScaleSizeMode.NORMAL;
    protected ScalePosMode scalePosMode = ScalePosMode.NORMAL;
    protected CursorType cursor = CursorType.NORMAL;

    protected OverlayParent parent;

    private long lastClickTime;

    private double scaledHeight;
    private double scaledWidth;
    private double scaledX;
    private double scaledY;
    private Rect mouseRect;
    private Rect renderRect;

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

    @SuppressWarnings("unused")
    public List<Transformation> getTransformations() {
        if (transformations == null) {
            transformations = new ArrayList<>();
        }
        return transformations;
    }

    @SuppressWarnings("unused")
    public List<MouseEnterListener> getMouseEnterListeners() {
        if (mouseEnterListeners == null) {
            mouseEnterListeners = new ArrayList<>();
        }
        return mouseEnterListeners;
    }

    @SuppressWarnings("unused")
    public List<MouseLeaveListener> getMouseLeaveListeners() {
        if (mouseLeaveListeners == null) {
            mouseLeaveListeners = new ArrayList<>();
        }
        return mouseLeaveListeners;
    }

    @SuppressWarnings("unused")
    public List<MouseClickListener> getMouseDoubleClickListeners() {
        if (mouseDoubleClickListeners == null) {
            mouseDoubleClickListeners = new ArrayList<>();
        }
        return mouseDoubleClickListeners;
    }

    @SuppressWarnings("unused")
    public List<MouseClickListener> getMouseClickListeners() {
        if (mouseClickListeners == null) {
            mouseClickListeners = new ArrayList<>();
        }
        return mouseClickListeners;
    }

    @SuppressWarnings("unused")
    public List<MouseHoverListener> getMouseHoverListeners() {
        if (mouseHoverListeners == null) {
            mouseHoverListeners = new ArrayList<>();
        }
        return mouseHoverListeners;
    }

    @SuppressWarnings("unused")
    public List<KeyboardListener> getKeyboardListeners() {
        if (keyboardListeners == null) {
            keyboardListeners = new ArrayList<>();
        }
        return keyboardListeners;
    }

    @SuppressWarnings("unused")
    public List<MouseDragListener> getDragListeners() {
        if (dragListeners == null) {
            dragListeners = new ArrayList<>();
        }
        return dragListeners;
    }

    @SuppressWarnings("unused")
    public List<MouseScrollingListener> getScrollingListeners() {
        if (scrollingListeners == null) {
            scrollingListeners = new ArrayList<>();
        }
        return scrollingListeners;
    }

    public boolean onKey(char key, int code) {
        if (isFocused()) {
            if (keyboardListeners != null) {
                if (keyboardListeners.size() > 0) {
                    for (KeyboardListener listener : keyboardListeners) {
                        listener.onKey(this, key, code);
                    }

                }

            }
            doOnKey(key, code);
            return true;

        }
        return false;
    }

    public void doOnKey(char key, int code) {

    }

    public void onActivate() {
        recalculateStatements();
    }

    public void onDestroy() {

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
        return scaledX;
    }

    protected double calculateScaledX() {
        if (isScalePosition()) {
            switch (scalePosMode) {
                case NORMAL:
                case CENTRALIZE_Y:
                    return getX() * getXFactor() + parent.getScaledX();
                case CENTRALIZE:
                case CENTRALIZE_X:
                    return getX() * getXFactor() + parent.getScaledX() - getScaledWidth() / 2;
            }
        } else {
            return x + parent.getScaledX();
        }
        return -1;
    }


    public double getScaledY() {
        return scaledY;
    }

    protected double calculateScaledY() {
        if (isScalePosition()) {
            switch (scalePosMode) {
                case NORMAL:
                case CENTRALIZE_X:
                    return getY() * getYFactor() + parent.getScaledY();
                case CENTRALIZE:
                case CENTRALIZE_Y:
                    return getY() * getYFactor() + parent.getScaledY() - getScaledHeight() / 2;
            }
        } else {
            return x + parent.getScaledX();
        }
        return -1;
    }


    public double getXFactor() {
        return parent.getParentWidth() / BOUND;
    }

    public double getYFactor() {
        return parent.getParentHeight() / BOUND;
    }

    public double getScaledWidth() {
        return scaledWidth;
    }

    protected double calculateScaledWidth() {
        if (isScaleSize()) {
            switch (scaleSizeMode) {
                case NORMAL:
                    return width * getXFactor();
                case DONT_SCALE_WIDTH:
                    return width;
                case WIDTH_EQUAL_HEIGHT:
                    return calculateScaledHeight();
                default:
                    return width * getXFactor();
            }
        } else {
            return width;
        }
    }

    public Rect getMouseRect() {
        return mouseRect;
    }

    /**
     * Необходимо переопределить
     *
     * @return область в которой считается что мышка внутри
     */
    protected Rect calculateMouseRect() {
        return new Rect(getScaledX(), getScaledY(), getScaledX() + getScaledWidth(), getScaledY() + getScaledHeight());
    }

    public double getScaledHeight() {
        return scaledHeight;
    }

    /**
     * Расчитывает высоту элемента согласно всем настройкам
     *
     * @return абсолютная высота элемента
     * @see #setScaleSizeMode(ScaleSizeMode)
     * @see #setScaleSize(boolean)
     */
    protected double calculateScaledHeight() {
        if (isScaleSize()) {
            switch (scaleSizeMode) {
                case NORMAL:
                    return height * getYFactor();
                case DONT_SCALE_HEIGHT:
                    return height;
                case HEIGHT_EQUAL_WIDTH:
                    return calculateScaledWidth();
                default:
                    return height * getYFactor();
            }
        } else {
            return height;
        }
    }

    @SuppressWarnings("unused")
    public boolean isFocused() {
        return isFocused;
    }

    @SuppressWarnings("unused")
    public ScalePosMode getScalePosMode() {
        return scalePosMode;
    }

    public void setScalePosMode(ScalePosMode scalePosMode) {
        this.scalePosMode = scalePosMode;
        recalculateStatements();
    }

    @SuppressWarnings("unused")
    public ScaleSizeMode getScaleSizeMode() {
        return scaleSizeMode;
    }

    public void setScaleSizeMode(ScaleSizeMode scaleSizeMode) {
        this.scaleSizeMode = scaleSizeMode;
        recalculateStatements();
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
        recalculateStatements();
    }

    public boolean isScaleSize() {
        return scaleSize;
    }

    public void setScaleSize(boolean scaleSize) {
        this.scaleSize = scaleSize;
        recalculateStatements();
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

    protected void runPreTransformations() {
        if (transformations != null) {
            for (Transformation transformation : transformations) {
                transformation.preTransform(this);
            }
        }
    }

    protected void runPostTransformations() {
        if (transformations != null) {
            for (Transformation transformation : transformations) {
                transformation.postTransform(this);
            }
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
            if (scrollingListeners != null) {
                for (MouseScrollingListener listener : scrollingListeners) {
                    listener.onMouseScroll(scrollAmount, mouseX, mouseY, this);
                }
            }
            return doMouseScroll(mouseX, mouseY, scrollAmount) || (scrollingListeners != null && scrollingListeners.size() > 0);
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
        boolean before = isMouseInside;
        if (isMouseInside = isInMouseRect(mouseX, mouseY)) {
            if (mouseHoverListeners != null) {

                for (MouseHoverListener listener : mouseHoverListeners) {
                    listener.onHover(mouseX - getScaledX(), mouseY - getScaledY(), this);
                }
            }
            CursorType cursor = getCursorType(mouseX, mouseY);
            if (cursor != CursorType.NONE) {
                DrawHelper.bindCursor(cursor);
            }

        }
        if (!before && isMouseInside) {
            if (mouseEnterListeners != null) {
                for (MouseEnterListener listener : mouseEnterListeners) {
                    listener.onMouseEnter(mouseX, mouseY, this);
                    doMouseEnter(mouseX, mouseY);
                }
            }
        } else if (before && !isMouseInside) {
            if (mouseLeaveListeners != null) {
                for (MouseLeaveListener listener : mouseLeaveListeners) {
                    listener.onMouseLeft(mouseX, mouseY, this);
                    doMouseLeft(mouseX, mouseY);
                }
            }
        }
        runPreTransformations();
        if (flag) {
            ScissorHelper.push();
            ScissorHelper.relativeScissor(getScaledX() / getScreenScaledWidth(), getScaledY() / getScreenScaledHeight(), getScaledWidth() / getScreenScaledWidth(), getScaledHeight() / getScreenScaledHeight());

        }

        doDraw(getScaledX(), getScaledY(), getScaledWidth(), getScaledHeight(), partialTick, mouseX, mouseY, isInMouseRect(mouseX, mouseY));

        runPostTransformations();

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
            if (dragListeners != null) {
                for (MouseDragListener listener : dragListeners) {
                    listener.onDrag(this, new Vector(lastMouseX, lastMouseY), new Vector(mouseX, mouseY));
                }
            }
            return doMouseDrag(lastMouseX, lastMouseY, mouseX, mouseY) || (dragListeners != null && dragListeners.size() > 0);
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
            if (mouseClickListeners != null) {
                for (MouseClickListener listener : mouseClickListeners) {
                    listener.onClick(mouseX - getScaledX(), mouseY - getScaledY(), this);

                }
            }
            if (System.currentTimeMillis() - lastClickTime <= 200) {
                if (mouseDoubleClickListeners != null) {
                    for (MouseClickListener listener : mouseDoubleClickListeners) {
                        listener.onClick(mouseX - getScaledX(), mouseY - getScaledY(), this);

                    }
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
        boolean before = isMouseInside;
        if (isMouseInside = isPointInBounds(mouseX, mouseY)) {
            if (mouseHoverListeners != null) {
                for (MouseHoverListener listener : mouseHoverListeners) {
                    listener.onHover(mouseX - getScaledX(), mouseY - getScaledY(), this);
                }
            }
            CursorType cursor = getCursorType(mouseX, mouseY);
            if (cursor != CursorType.NONE) {
                DrawHelper.bindCursor(cursor);
            }
        }
        if (!before && isMouseInside) {
            if (mouseEnterListeners != null) {
                for (MouseEnterListener listener : mouseEnterListeners) {
                    listener.onMouseEnter(mouseX, mouseY, this);
                    doMouseEnter(mouseX, mouseY);
                }
            }
        } else if (before && !isMouseInside) {
            if (mouseLeaveListeners != null) {
                for (MouseLeaveListener listener : mouseLeaveListeners) {
                    listener.onMouseLeft(mouseX, mouseY, this);
                    doMouseLeft(mouseX, mouseY);
                }
            }
        }
        runPreTransformations();
        if (flag) {
            ScissorHelper.push();
            ScissorHelper.relativeScissor(getScaledX() / getScreenScaledWidth(), getScaledY() / getScreenScaledHeight(), getScaledWidth() / getScreenScaledWidth(), getScaledHeight() / getScreenScaledHeight());

        }

        doDrawText(getScaledX(), getScaledY(), getScaledWidth(), getScaledHeight(), partialTick, mouseX, mouseY, isInMouseRect(mouseX, mouseY));

        runPostTransformations();

        GL11.glEnable(GL11.GL_CULL_FACE);
        if (flag) {
            ScissorHelper.pop();
        }

    }

    protected void doMouseLeft(@SuppressWarnings("unused")int mouseX, @SuppressWarnings("unused")int mouseY) {
    }

    protected void doMouseEnter(@SuppressWarnings("unused")int mouseX, @SuppressWarnings("unused")int mouseY) {

    }

    protected boolean isPointInBounds(int x, int y) {
        return x >= getScaledX() && y >= getScaledY() && x <= getScaledX() + getScaledWidth() && y <= getScaledY() + getScaledHeight();
    }

    protected abstract void doDraw(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds);

    protected abstract void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds);

    public double getX() {
        return x;
    }

    @Override
    public void setX(double x) {
        super.setX(x);
        recalculateStatements();
    }

    public double getY() {
        return y;
    }

    @Override
    public void setY(double y) {
        super.setY(y);
        recalculateStatements();
    }

    public OverlayParent getParent() {
        return parent;
    }

    public synchronized ScaledOverlay setParent(OverlayParent parent) {
        this.parent = parent;
        return this;
    }

    protected void recalculateStatements() {
        if (parent != null) {
            scaledHeight = calculateScaledHeight();
            scaledWidth = calculateScaledWidth();

            scaledX = calculateScaledX();
            scaledY = calculateScaledY();

            renderRect = calculateRenderRect();
            mouseRect = calculateMouseRect();
        }
    }

    public void onResize() {
        recalculateStatements();
    }

    public Rect getRenderRect() {
        return renderRect;
    }

    protected Rect calculateRenderRect() {
        return new Rect(getScaledX(), getScaledY(), getScaledX() + getScaledWidth(), getScaledY() + getScaledHeight());
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
                "\n, scaleSizeMode=" + scaleSizeMode +
                "\n, parent=" + parent +
                "\n, scaledDimensions=" + getDimensions() +
                "\n} " + super.toString();
    }

    public Vector getScaledPos() {
        return new Vector(getScaledX(), getScaledY());
    }



    protected CursorType getCursorType(@SuppressWarnings("unused") int mouseX, @SuppressWarnings("unused") int mouseY) {
        return cursor;
    }

    public Dimensions getDimensions() {
        return new Dimensions(getScaledWidth(), getScaledHeight());
    }

    // TODO: 04.12.17 Курсоры!
    public CursorType getCursor() {
        return cursor;
    }

    public void setCursor(CursorType cursor) {
        this.cursor = cursor;
    }

    @SuppressWarnings("unused")
    public void toFront() {
        parent.moveToFront(this);
    }

    @SuppressWarnings("unused")
    public void toBackground() {
        parent.moveToBackground(this);
    }


    public enum ScaleSizeMode {
        WIDTH_EQUAL_HEIGHT, HEIGHT_EQUAL_WIDTH, NORMAL, DONT_SCALE_WIDTH, DONT_SCALE_HEIGHT
    }

    public enum ScalePosMode {
        CENTRALIZE_Y,
        CENTRALIZE_X,
        CENTRALIZE,
        NORMAL
    }
}