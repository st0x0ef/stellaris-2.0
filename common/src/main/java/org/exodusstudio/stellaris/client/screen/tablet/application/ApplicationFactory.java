package org.exodusstudio.stellaris.client.screen.tablet.application;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.Function;

public class ApplicationFactory {

    private final Component name;
    private final Component description;
    private final ResourceLocation iconLocation;
    private final Function<Player, Screen> screenFactory; // Function that takes Player and returns a Screen

    public ApplicationFactory(Component name, Component description, ResourceLocation iconLocation, Function<Player, Screen> screenFactory) {
        this.name = name;
        this.description = description;
        this.iconLocation = iconLocation;
        this.screenFactory = screenFactory;
    }

    public Component getName() { return name; }
    public Component getDescription() { return description; }
    public ResourceLocation getIconLocation() { return iconLocation; }

    // This method creates the screen when a Player is available
    public Screen createScreen(Player player) {
        return screenFactory.apply(player);
    }
}