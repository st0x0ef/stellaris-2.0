package org.exodusstudio.stellaris.client.screens.components.sd;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class SDCardDecodeButton extends Button {

    public SDCardDecodeButton(int x, int y, int width, int height, OnPress onPress) {
        super(x, y, width, height, Component.literal("DECODE"), onPress, Button.DEFAULT_NARRATION);
    }

    @Override
    public void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();

        int color = this.active ? -1 : -6250336;
        this.renderString(ctx, mc.font, color);
    }

}
