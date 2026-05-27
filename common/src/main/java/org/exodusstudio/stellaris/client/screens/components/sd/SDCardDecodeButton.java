package org.exodusstudio.stellaris.client.screens.components.sd;

import net.minecraft.network.chat.Component;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;

public class SDCardDecodeButton extends TexturedButton {

    public SDCardDecodeButton(int x, int y, int width, int height, OnPress onPress) {
        super(x, y, width, height, Component.literal("DECODE"), onPress);
    }

}
