package ru.justagod.justacore.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import ru.justagod.justacore.gui.helper.DrawHelper;
import ru.justagod.justacore.gui.model.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustAGod on 22.10.17.
 */
public class TextInputOverlay extends ScaledOverlay  {

    private boolean isEnabled = true;
    private List<Character> text = new ArrayList<Character>();
    private int selectionPos = 0;
    private int cursorPosition = 0;
    private int maxLength = -1;

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public TextInputOverlay(double x, double y) {
        super(x, y);
        isEnabled = true;
        setCursor(DrawHelper.CursorType.TEXT);

    }

    public TextInputOverlay(double x, double y, double width, double height) {
        super(x, y, width, height);
        isEnabled = true;
        setCursor(DrawHelper.CursorType.TEXT);
    }

    @Override
    public void doOnKey(char key, int code) {
        switch (key) {
            case 1:
                setCursorPositionEnd();
                setSelectionPos(0);

            case 22:
                if (isEnabled) {
                    writeText(GuiScreen.getClipboardString());
                }


            default:
                switch (code) {
                    case 14:
                        if (GuiScreen.isCtrlKeyDown()) {
                            if (isEnabled) {
                                deleteWords(-1);
                            }
                        } else if (isEnabled) {
                            deleteFromCursor(-1);
                        }

                    case 199:
                        if (GuiScreen.isShiftKeyDown()) {
                            setSelectionPos(0);
                        } else {
                            setCursorPositionZero();
                        }

                    case 207:
                        if (GuiScreen.isShiftKeyDown()) {
                            setSelectionPos(text.size());
                        } else {
                            setCursorPositionEnd();
                        }

                    case 203:
                        moveCursor(-1);
                    case 205:
                        moveCursor(1);
                    case 211:
                        if (GuiScreen.isCtrlKeyDown()) {
                            if (isEnabled) {
                                deleteWords(1);
                            }
                        } else if (isEnabled) {
                            deleteFromCursor(1);
                        }

                    default:
                        if (ChatAllowedCharacters.isAllowedCharacter(key)) {
                            if (isEnabled) {
                                writeText(Character.toString(key));
                            }

                        } else {
                        }
                }
        }

    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getText() {
        StringBuilder builder = new StringBuilder();

        for (Character character : text) {
            builder.append(character);
        }

        return builder.toString();
    }

    public void setText(String text) {
        this.text.clear();
        for (char c : text.toCharArray()) {
            this.text.add(c);
        }
    }

    public int getSelectionPos() {
        return selectionPos;
    }

    public void setSelectionPos(int selectionPos) {
        this.selectionPos = selectionPos;
    }



    private void moveCursor(int move) {
        cursorPosition += move;
        cursorPosition = MathHelper.clamp_int(cursorPosition, 0, text.size());
    }

    private void setCursorPositionZero() {
        cursorPosition = 0;
    }

    private void setCursorPositionEnd() {
        cursorPosition = text.size();
    }

    private void deleteWords(int i) {

    }

    private synchronized void deleteFromCursor(int i) {
        int tmp = cursorPosition;
        if (i > 0) {
            for (int j = tmp; j < tmp + i; j++) {
                if (j < text.size() && j >= 0) {
                    text.remove(j);
                }
            }
        } else {
            for (int j = tmp - 1; j >= tmp + i; j--) {
                if (j < text.size() && j >= 0) {
                    text.remove(j);
                    cursorPosition--;
                }
            }
        }
    }

    private synchronized void writeText(String s) {
        if (text.size() < maxLength || maxLength == -1) {
            for (int i = cursorPosition; i < s.length() + cursorPosition; i++) {
                text.add(i, s.charAt(i - cursorPosition));
            }
            cursorPosition += s.length();
        }
    }





    @Override
    protected void doDraw(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {

    }

    @Override
    protected synchronized void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
        GL11.glColor3f(0 , 0, 0);
        pushAndTranslate(xPos, yPos);
        {
            GL11.glBegin(GL11.GL_QUADS);
            {
                GL11.glVertex2d(0, 0);
                GL11.glVertex2d(width, 0);
                GL11.glVertex2d(width, 10);
                GL11.glVertex2d(0, 10);
            }
            GL11.glEnd();
            int x = 2;
            for (int i = 0; i < text.size(); i++) {
                if (i == cursorPosition && isFocused) {
                    drawString("|", x, 1, false);
                }
                Character c = text.get(i);
                drawString("" + c, x, 1, true);
                x += Minecraft.getMinecraft().fontRenderer.getCharWidth(c);

            }
            if (cursorPosition >= text.size() && isFocused) {
                drawString("|", x, 1, false);

            }
        }
        pop();
    }

    @Override
    public Rect getRenderRect() {
        return new Rect(getScaledX(), getScaledY(), getScaledX() + getScaledWidth(), getScaledY() + 10);
    }

    @Override
    public Rect getMouseRect() {
        return getRenderRect();
    }
}
