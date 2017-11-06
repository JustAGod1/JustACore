package ru.justagod.justacore.helper

/**
 * Created by JustAGod on 05.11.17.
 */
class Scissor {
    private int x
    private int y
    private int width
    private int height

    Scissor(int x, int y, int width, int height) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    /**
     * Находит пересечение сцизоров
     * @param scissor с кем искать пересечение
     * @return null если пересечений нет
     */
    Scissor intersection(Scissor scissor) {
        int mMaxX = x + width
        int mMaxY = y + height

        int hMaxX = scissor.x + scissor.width
        int hMaxY = scissor.y + scissor.height

        if (hMaxX <= mMaxX && scissor.x >= x && hMaxY <= mMaxY && scissor.y <= y) {
            scissor
        } else if (hMaxX >= mMaxX && scissor.x <= x && hMaxY >= mMaxY && scissor.y >= y) {
            this
        } else if (hMaxY >= mMaxY && scissor.y <= mMaxY && scissor.x >= x && scissor.x <= mMaxX) {
            new Scissor(scissor.x, scissor.y, Math.min(hMaxX, mMaxX) - scissor.x, mMaxY - scissor.y)
        } else if (hMaxY >= mMaxY && scissor.y <= mMaxY && scissor.x <= x && scissor.x <= mMaxX) {
            new Scissor(x, scissor.y, Math.min(hMaxX, mMaxX) - x, mMaxY - scissor.y)
        } else if (scissor.y <= y && hMaxY >= y && scissor.x >= x && scissor.x <= mMaxX) {
            new Scissor(scissor.x, y, Math.min(hMaxX, mMaxX) - scissor.x, hMaxY - y)
        } else if (scissor.y <= y && hMaxY >= y && scissor.x <= x && scissor.x <= mMaxX) {
            new Scissor(x, y, Math.min(hMaxX, mMaxX) - x, hMaxY - y)
        } else if (scissor.y >= y && hMaxY <= mMaxY && scissor.x >= x && scissor.x <= mMaxX && hMaxX >= mMaxX) {
            new Scissor(scissor.x, scissor.y, mMaxX - scissor.x, scissor.y)
        } else if (scissor.y <= y && hMaxY >= y && scissor.x >= x && hMaxX >= mMaxX) {
            new Scissor(x, scissor.y, hMaxX - x, hMaxY - y)
        }  else null
    }


    int getX() {
        return x
    }

    int getY() {
        return y
    }

    int getWidth() {
        return width
    }

    int getHeight() {
        return height
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Scissor scissor = (Scissor) o

        if (height != scissor.height) return false
        if (width != scissor.width) return false
        if (x != scissor.x) return false
        if (y != scissor.y) return false

        return true
    }

    int hashCode() {
        int result
        result = x
        result = 31 * result + y
        result = 31 * result + width
        result = 31 * result + height
        return result
    }

    @Override
    String toString() {
        return "Scissor{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}'
    }
}