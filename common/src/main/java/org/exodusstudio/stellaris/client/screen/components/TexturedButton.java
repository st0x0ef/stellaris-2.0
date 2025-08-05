package org.exodusstudio.stellaris.client.screen.components;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class TexturedButton extends Button {

    /** Default Textures */
    public static final ResourceLocation TEXTURE = ResourceLocationUtils.guiTexture("util/buttons/button");
    public static final ResourceLocation HOVER_TEXTURE = ResourceLocationUtils.guiTexture("util/buttons/button");

    private ResourceLocation buttonTexture;
    private ResourceLocation hoverButtonTexture;

    private int xTexStart;
    private int yTexStart;

    private int yDiffText;

    private int textureWidth;
    private int textureHeight;

    private boolean showText = false;

    public TexturedButton(int x, int y, int widthIn, int heightIn, Button.OnPress onPressIn) {
        this(x, y, widthIn, heightIn, Component.empty(), onPressIn, DEFAULT_NARRATION);
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

    public <T extends TexturedButton> T tex(ResourceLocation buttonTexture, ResourceLocation hoverTexture) {
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

    public <T extends TexturedButton> T showText(boolean showText) {
        this.showText = showText;
        return cast();
    }

    public void setYShift(int y) {
        this.yDiffText = y;
    }

    /** Override Methods */
    @Override
    public void setTooltip(@Nullable Tooltip tooltip) {
        super.setTooltip(tooltip);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();


        int i = this.yTexStart;
        if (this.isHoveredOrFocused()) i += this.yDiffText;

        /** TEXTURE MANAGER */
        ResourceLocation texture = this.getTypeTexture();


        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));


        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.getX(), this.getY(), (float) this.xTexStart, (float) i,
                this.width, this.height, this.textureWidth, this.textureHeight);

        /** FONT RENDERER */
        int color = this.isHovered ? 16777215 : 10526880;

        if(this.showText) {
            this.renderString(graphics, minecraft.font, color | Mth.ceil(this.alpha * 255.0F) << 24);
        }
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        this.textureWidth = width;
        this.textureHeight = height;
    }

    /** TYPE TEXTURE MANAGER */
    private ResourceLocation getTypeTexture() {
        if (this.isHovered) {
            return this.buttonTexture;
        }
        else {
            return this.hoverButtonTexture;
        }
    }




}
