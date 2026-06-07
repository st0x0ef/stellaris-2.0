package org.exodusstudio.stellaris.client.screens.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * A utility class to create custom textured button.
 */

public class TexturedButton extends Button {

    /** Default Textures */
    public static final Identifier TEXTURE = IdentifierUtils.guiTexture("util/buttons/button");
    public static final Identifier HOVER_TEXTURE = IdentifierUtils.guiTexture("util/buttons/button");

    public Identifier buttonTexture;
    public Identifier hoverButtonTexture;

    public int xTexStart;
    public int yTexStart;

    public int yDiffText;

    public int textureWidth;
    public int textureHeight;

    public Padding textPadding = new Padding(0);

    public boolean useSprite = false;
    public Component text = Component.empty();
    public int color;

    public TexturedButton(int x, int y, int widthIn, int heightIn, Button.OnPress onPressIn) {
        this(x, y, widthIn, heightIn, Component.empty(), onPressIn, DEFAULT_NARRATION);
    }

    public TexturedButton(int x, int y, int widthIn, int heightIn, Identifier buttonTexture, Identifier hoverButtonTexture, Button.OnPress onPressIn) {
        this(x, y, widthIn, heightIn, Component.empty(), onPressIn, DEFAULT_NARRATION);
        this.buttonTexture = buttonTexture;
        this.hoverButtonTexture = hoverButtonTexture;
    }

    public TexturedButton(int x, int y, int widthIn, int heightIn, Component title, Button.OnPress onPressIn) {
        this(x, y, widthIn, heightIn, title, onPressIn, DEFAULT_NARRATION);
    }

    public TexturedButton(int x, int y, int widthIn, int heightIn, Component title, Button.OnPress onPressIn,
                          CreateNarration onTooltipIn) {
        super(x, y, widthIn, heightIn, title, onPressIn, onTooltipIn);
        this.textureWidth = widthIn;
        this.textureHeight = heightIn;
        this.yDiffText = 0;
        this.xTexStart = 0;
        this.yTexStart = 0;
        this.buttonTexture = TEXTURE;
        this.hoverButtonTexture = HOVER_TEXTURE;
        this.color = ARGB.white(this.alpha);
        this.text = title;
    }

    /** Override Methods */
    @Override
    public void setTooltip(@Nullable Tooltip tooltip) {
        super.setTooltip(tooltip);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();


        int i = this.yTexStart;
        if (this.isHoveredOrFocused()) i += this.yDiffText;

        /** TEXTURE MANAGER */
        Identifier texture = this.getTypeTexture();

        if(this.useSprite) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, this.getWidth(), this.getHeight(), this.xTexStart, this.yTexStart, this.getX(), this.getY(), this.getWidth() - this.xTexStart, this.getHeight() - this.yTexStart, this.color);
        } else {
            graphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.getX(), this.getY(), (float) this.xTexStart, (float) i,
                    this.width, this.height, this.textureWidth, this.textureHeight, this.color);
        }

        if(!Objects.equals(this.text, Component.empty())) {
            renderScrollingStringOverContents(graphics.textRendererForWidget(this,
                    GuiGraphicsExtractor.HoveredTextEffects.NONE), this.text, this.getX() , this.getY() + (getHeight() - minecraft.font.lineHeight) / 2);

            //graphics.text(minecraft.font, this.text, this.getX() + (this.getWidth() - minecraft.font.width(text)) / 2, this.getY() + (getHeight() - minecraft.font.lineHeight) / 2, Utils.getMinecraftColor("white"));
        }
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        this.textureWidth = width;
        this.textureHeight = height;
    }



    /** Builder Methods */
    public <T extends TexturedButton> T tooltip(@Nullable Tooltip tooltip) {
        this.setTooltip(tooltip);
        return cast();
    }

    @SuppressWarnings("unchecked")
    private <T extends TexturedButton> T cast() {
        return (T) this;
    }

    public <T extends TexturedButton> T useSprite(boolean useSprite) {
        this.useSprite = useSprite;
        return cast();
    }

    public <T extends TexturedButton> T tex(Identifier buttonTexture, Identifier hoverTexture) {
        this.buttonTexture = buttonTexture;
        this.hoverButtonTexture = hoverTexture;
        return cast();
    }

    public <T extends TexturedButton> T size(int texWidth, int texHeight) {
        this.textureWidth = texWidth;
        this.textureHeight = texHeight;
        return cast();
    }

    public <T extends TexturedButton> T setUVs(int xTexStart, int yTexStart) {
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        return cast();
    }

    public <T extends TexturedButton> T setText(Component text) {
        this.text = text;
        return cast();
    }

    public <T extends TexturedButton> T setColor(int color) {
        this.color = color;
        return cast();
    }

    public <T extends TexturedButton> T setTextPadding(Padding textPadding) {
        this.textPadding = textPadding;
        return cast();
    }

    public void setYShift(int y) {
        this.yDiffText = y;
    }

    /** TYPE TEXTURE MANAGER */
    public Identifier getTypeTexture() {
        if (this.isHovered) {
            Stellaris.LOG.error("Hover Texture: " + this.hoverButtonTexture);
            Stellaris.LOG.error("Texture: " + this.buttonTexture);

            return this.hoverButtonTexture;
        }
        else {
            return this.buttonTexture;
        }
    }

    protected void renderScrollingStringOverContents(ActiveTextCollector activeTextCollector, Component text, int x, int y) {
        int endX = this.getX() + this.getWidth() - this.textPadding.right;
        int endY = y + Minecraft.getInstance().font.lineHeight - this.textPadding.bottom;

        activeTextCollector.acceptScrollingWithDefaultCenter(text, x + this.textPadding.left, endX, y + this.textPadding.top, endY);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onPress(@NonNull InputWithModifiers input) {
        if (this.onPress == null) {
            return;
        }
        super.onPress(input);
    }
}
