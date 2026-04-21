package org.exodusstudio.stellaris.client.screens.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.Objects;

public class CustomCheckBox extends AbstractButton {

    private Identifier texture = IdentifierUtils.id("widget/checkbox_selected");;
    private Identifier checkTexture = IdentifierUtils.id("widget/checkbox");;

    public boolean selected;
    private final OnValueChange onValueChange;
    private final MultiLineTextWidget textWidget;

    private boolean text = true;

    public CustomCheckBox(int x, int y, int maxWidth, Component message, Font font, boolean selected) {
        this(x, y, maxWidth, message, font, selected, OnValueChange.NOP);
    }


    public CustomCheckBox(int x, int y, int maxWidth, Component message, Font font, boolean selected, OnValueChange onValueChange) {
        super(x, y, 0, 0, message);
        this.width = maxWidth;
        this.textWidget = (new MultiLineTextWidget(message, font)).setMaxWidth(this.width);
        this.height = this.width;
        this.selected = selected;
        this.onValueChange = onValueChange;
    }

    public CustomCheckBox showText(boolean text) {
        this.text = text;
        return this;
    }

    public CustomCheckBox setTexture(Identifier texture, Identifier checkTexture) {
        this.texture = texture;
        this.checkTexture = checkTexture;
        return this;
    }

    private int getAdjustedWidth(int maxWidth, Component message, Font font) {
        return Math.min(getDefaultWidth(message, font), maxWidth);
    }

    private int getAdjustedHeight(Font font) {
        return Math.max(getBoxSize(font), this.textWidget.getHeight());
    }

    static int getDefaultWidth(Component message, Font font) {
        return getBoxSize(font) + 4 + font.width(message);
    }

    public static int getBoxSize(Font font) {
        Objects.requireNonNull(font);
        return 17;
    }

    public CustomCheckBox setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.focused"));
            } else {
                narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.hovered"));
            }
        }
    }

    @Override
    public void onPress(InputWithModifiers input) {
        this.selected = !this.selected;
        this.onValueChange.onValueChange(this, this.selected);
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        Identifier resourceLocation = this.selected ? this.checkTexture : this.texture;

        int i = this.width;
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, resourceLocation, this.getX(), this.getY(), i, i);

        if(this.text) {
            int j = this.getX() + i + 4;
            int k = this.getY() + i / 2 - this.textWidget.getHeight() / 2;
            this.textWidget.setPosition(j, k);
            this.textWidget.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        }

    }


    @Environment(EnvType.CLIENT)
    public interface OnValueChange {
        OnValueChange NOP = (checkbox, bl) -> {
        };

        void onValueChange(CustomCheckBox checkbox, boolean bl);
    }


}
