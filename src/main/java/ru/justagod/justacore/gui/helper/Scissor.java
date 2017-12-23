package ru.justagod.justacore.gui.helper;

import ru.justagod.justacore.gui.model.Rect;

/**
 * Created by JustAGod on 05.11.17.
 */
class Scissor {
    private int x;
    private int y;
    private int width;
    private int height;

    Scissor(Rect rect) {
        int x1 = ScreenHelper.transformToScreenX((int) rect.getX1());
        int y1 = ScreenHelper.transformToScreenY((int) rect.getY1());
        int x2 = ScreenHelper.transformToScreenX((int) rect.getX2());
        int y2 = ScreenHelper.transformToScreenY((int) rect.getY2());

        x = x1;
        width = x2 - x1;

        y = y2;
        height = y1 - y2;
    }

    Scissor(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Находит пересечение сцизоров
     * @param scissor с кем искать пересечение
     * @return null если пересечений нет
     */
    Scissor intersection(Scissor scissor) {
        int mMaxX = x + width;
        int mMaxY = y + height;

        int hMaxX = scissor.x + scissor.width;
        int hMaxY = scissor.y + scissor.height;

        if (hMaxX <= mMaxX && scissor.x >= x && hMaxY <= mMaxY && scissor.y >= y) {
            return scissor;
        } else if (hMaxX >= mMaxX && scissor.x <= x && hMaxY >= mMaxY && scissor.y >= y) {
            return this;
        } else if (hMaxY >= mMaxY && scissor.y <= y && scissor.x >= x && scissor.x <= mMaxX) {
            return new Scissor(scissor.x, y, mMaxX - scissor.x, mMaxY - y);
        } else if (hMaxY <= mMaxY && scissor.y >= y && scissor.x <= x && hMaxX >= mMaxX) {
            return new Scissor(x, scissor.y, mMaxX - x, hMaxY - scissor.y);
        } else if (hMaxY >= mMaxY && scissor.y <= y && scissor.x <= x && scissor.x <= mMaxX) {
            return new Scissor(x, y, hMaxX - x, mMaxY - scissor.y);
        } else if (hMaxY >= mMaxY && scissor.y <= mMaxY && scissor.x >= x && scissor.x <= mMaxX) {
            return new Scissor(scissor.x, scissor.y, Math.min(hMaxX, mMaxX) - scissor.x, mMaxY - scissor.y);
        } else if (hMaxY >= mMaxY && scissor.y <= mMaxY && scissor.x <= x && scissor.x <= mMaxX) {
            return new Scissor(x, scissor.y, Math.min(hMaxX, mMaxX) - x, mMaxY - scissor.y);
        } else if (scissor.y <= y && hMaxY >= y && scissor.x >= x && scissor.x <= mMaxX) {
            return new Scissor(scissor.x, y, Math.min(hMaxX, mMaxX) - scissor.x, hMaxY - y);
        } else if (scissor.y <= y && hMaxY >= y && scissor.x >= x && scissor.x <= mMaxX) {
            return new Scissor(x, y, Math.min(hMaxX, mMaxX) - x, hMaxY - y);
        } else if (scissor.y >= y && hMaxY <= mMaxY && scissor.x >= x && scissor.x <= mMaxX && hMaxX >= mMaxX) {
            return new Scissor(scissor.x, scissor.y, mMaxX - scissor.x, scissor.height);
        } else if (scissor.y >= y && hMaxY <= mMaxY && scissor.x <= x && hMaxX >= x) {
            return new Scissor(x, scissor.y, hMaxX - x, scissor.height);
        }else if (scissor.y <= y && hMaxY >= y && scissor.x >= x && hMaxX >= mMaxX) {
            return new Scissor(x, scissor.y, hMaxX - x, hMaxY - y);
        } /*else if (scissor.y >= y && mMaxY >= scissor.y && scissor.x >= x && hMaxX >= mMaxX) {
            return new Scissor(scissor.x, scissor.y, mMaxX - scissor.x, hMaxY - y);
        } */else return null;
    }


    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Scissor scissor = (Scissor) o;

        if (getX() != scissor.getX()) return false;
        if (getY() != scissor.getY()) return false;
        if (getWidth() != scissor.getWidth()) return false;
        return getHeight() == scissor.getHeight();
    }

    @Override
    public int hashCode() {
        int result = getX();
        result = 31 * result + getY();
        result = 31 * result + getWidth();
        result = 31 * result + getHeight();
        return result;
    }

    @Override
    public String toString() {
        return "Scissor{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}