package org.exodusstudio.stellaris.mixin.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CreateWorldScreen.class)
public abstract class ExperimentalSettingsRemover extends Screen{

    protected ExperimentalSettingsRemover(Component title) {
        super(title);
    }



}
