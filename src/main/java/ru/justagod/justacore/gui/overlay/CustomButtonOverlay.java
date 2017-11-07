package ru.justagod.justacore.gui.overlay;


import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;

/**
 * Created by JustAGod on 17.10.17.
 */
public class CustomButtonOverlay extends AbstractButtonOverlay {

    private ResourceLocation texture;


    public CustomButtonOverlay(double x, double y, String text, Runnable onClick, ResourceLocation sound, ResourceLocation texture) {
        super(x, y, text, onClick, sound);
        this.texture = texture;
    }

    public CustomButtonOverlay(double x, double y, double width, double height, String text, Runnable onClick, ResourceLocation sound, ResourceLocation texture) {
        super(x, y, width, height, text, onClick, sound);
        this.texture = texture;
    }

    public CustomButtonOverlay(double x, double y, double width, double height, boolean scalePosition, boolean scaleSize, String text, Runnable onClick, ResourceLocation sound, ResourceLocation texture) {
        super(x, y, width, height, scalePosition, scaleSize, text, onClick, sound);
        this.texture = texture;
    }

    public CustomButtonOverlay(double x, double y, String text, Runnable onClick, ResourceLocation texture) {
        super(x, y, text, onClick);
        this.texture = texture;
    }

    public CustomButtonOverlay(double x, double y, double width, double height, String text, Runnable onClick, ResourceLocation texture) {
        super(x, y, width, height, text, onClick);
        this.texture = texture;
    }

    public CustomButtonOverlay localize() {
        text = I18n.format(text);
        return this;
    }




    public String getText() {
        return text;
    }

    @Override
    protected void doDraw(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        bindTexture(texture);

        if (mouseInBounds) GL11.glColor3f(0.6f, 0.6f, 1f);
        pushAndTranslate(xPos + (width / 2), yPos + (height / 2));
        {
            t.startDrawingQuads();
            t.addVertexWithUV(-(width / 2), -(height / 2), 0, 0, 0);
            t.addVertexWithUV(-(width / 2), (height / 2), 0, 0, 1);
            t.addVertexWithUV((width / 2), (height / 2), 0, 1, 1);
            t.addVertexWithUV((width / 2), -(height / 2), 0, 1, 0);
            t.draw();
        }
        pop();
    }

    @Override
    protected void doDrawText(double xPos, double yPos, double width, double height, float partialTick, int mouseX, int mouseY, boolean mouseInBounds) {
        if (!StringUtils.isNullOrEmpty(getText())) {
            drawCentredString(getText(), (int)(xPos + (width / 2)), (int) (yPos + (height * 0.2)), false);
        }
    }

    public void setText(String text) {
        this.text = text;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }


}
