package org.exodusstudio.stellaris.client.screens.components.sd;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class SDCardDecodeButton extends Button {

    public SDCardDecodeButton(int x, int y, int width, int height, OnPress onPress) {
        super(x, y, width, height, Component.literal("DECODE"), onPress, Button.DEFAULT_NARRATION);
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderDefaultLabel(guiGraphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE));
    }

}
