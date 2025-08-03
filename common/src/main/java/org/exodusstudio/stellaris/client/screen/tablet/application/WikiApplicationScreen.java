package org.exodusstudio.stellaris.client.screen.tablet.application;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class WikiApplicationScreen extends Screen{

    public Player player;

    public WikiApplicationScreen(Player player) {
        super(Component.empty());

        this.player = player;
    }

}
