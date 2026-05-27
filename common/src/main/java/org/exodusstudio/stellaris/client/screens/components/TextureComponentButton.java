package org.exodusstudio.stellaris.client.screens.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.exodusstudio.stellaris.client.screens.tablet.TabletWidgetAnimation;
import org.exodusstudio.stellaris.client.screens.utils.GUIUtils;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * A utility class to create custom textured button.
 */

public class TextureComponentButton extends Button {

    /** Default Textures */
    public static final Identifier VANILLA_BACKGROUND_TEXTURE = Identifier.withDefaultNamespace("widget/button");
    public static final Identifier VANILLA_HOVER_TEXTURE = Identifier.withDefaultNamespace("widget/button_highlighted");
    public static final Identifier VANILLA_DISABLED_TEXTURE = Identifier.withDefaultNamespace("widget/button_disabled");

    public Identifier contentTexture;

    public int buttonWidth;
    public int buttonHeight;
    public int textureWidth;
    public int textureHeight;

    private String tooltipText;
    private final TabletWidgetAnimation animation = new TabletWidgetAnimation();

    public TextureComponentButton(int x, int y, int width, int height, int textureWidth, int textureHeight, Identifier contentTexture, String tooltipText, OnPress onPressIn, CreateNarration onTooltipIn) {
        super(x, y, width, height, Component.empty(), onPressIn, onTooltipIn);
        this.buttonWidth = width;
        this.buttonHeight = height;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.contentTexture = contentTexture;
        this.tooltipText = tooltipText;
    }

    /** Override Methods */
    @Override
    public void setTooltip(@Nullable Tooltip tooltip) {
        Tooltip buttonTooltip = Tooltip.create(Component.literal(this.tooltipText));
        super.setTooltip(buttonTooltip);
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.animation.push(guiGraphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.isHoveredOrFocused(), this.active);

        /** TEXTURE MANAGER */
        Identifier texture = this.getTypeTexture();
        this.renderHoverGlow(guiGraphics);
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, this.getX(), this.getY(), buttonWidth, buttonHeight, ARGB.white(this.alpha));

        // Draw Content Texture (smaller texture inside the middle of the button)
        int contentX = this.getX() + (this.buttonWidth - this.textureWidth) / 2;
        int contentY = this.getY() + (this.buttonHeight - this.textureHeight) / 2;
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.contentTexture, contentX, contentY, this.textureWidth, this.textureHeight, ARGB.white(this.alpha));

        this.animation.pop(guiGraphics);
    }

    private void renderHoverGlow(GuiGraphics graphics) {
        if (!this.active) {
            return;
        }

        float glow = this.animation.glowAmount();
        if (glow <= 0.01F) {
            return;
        }

        int alpha = Math.round(22.0F * glow);
        graphics.fill(this.getX() - 1, this.getY() - 1, this.getX() + this.getWidth() + 1, this.getY() + this.getHeight() + 1, (alpha << 24) | 0x74D6FF);
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        this.textureWidth = width;
        this.textureHeight = height;
    }

    /** Builder Methods */
    public <T extends TextureComponentButton> T tooltip(@Nullable Tooltip tooltip) {
        this.setTooltip(tooltip);
        return cast();
    }

    @SuppressWarnings("unchecked")
    private <T extends TextureComponentButton> T cast() {
        return (T) this;
    }

    public <T extends TextureComponentButton> T size(int texWidth, int texHeight) {
        this.textureWidth = texWidth;
        this.textureHeight = texHeight;
        return cast();
    }

    /** TYPE TEXTURE MANAGER */
    public Identifier getTypeTexture() {
        if (!this.isActive()) {
            return VANILLA_DISABLED_TEXTURE;
        } else if (this.isHovered) {
            return VANILLA_HOVER_TEXTURE;
        } else {
            return VANILLA_BACKGROUND_TEXTURE;
        }
    }

    public void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (this.isHovered) {
            List<ClientTooltipComponent> tooltipComponents = List.of(GUIUtils.getMessageComponent(this.tooltipText, "White"));
            graphics.renderTooltip(Minecraft.getInstance().font, tooltipComponents, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onPress(@NonNull InputWithModifiers input) {
        if (this.onPress == null) {
            return;
        }
        this.animation.press();
        super.onPress(input);
    }
}
