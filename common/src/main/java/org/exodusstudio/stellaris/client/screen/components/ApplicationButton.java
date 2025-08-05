package org.exodusstudio.stellaris.client.screen.components;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.client.screen.tablet.application.ApplicationRegistry;


public class ApplicationButton extends TexturedButton{

    private final ApplicationRegistry.ApplicationFactory applicationFactory;

    public ApplicationButton(int x, int y, int widthIn, int heightIn, OnPress onPressIn, ApplicationRegistry.ApplicationFactory applicationFactory) {
        super(x, y, widthIn, heightIn, onPressIn);
        this.applicationFactory = applicationFactory;
    }

    public ApplicationRegistry.ApplicationFactory getApplicationFactory() {
        return applicationFactory;
    }

    public void openApplication(Player player) {
        Minecraft.getInstance().setScreen(applicationFactory.createScreen(player));
    }

    public static ApplicationButton createButton(int x, int y, int widthIn, int heightIn, Player player, ApplicationRegistry.ApplicationFactory factory) {
        return new ApplicationButton(x, y, widthIn, heightIn, button -> {
            if(button instanceof ApplicationButton appButton) appButton.openApplication(player);
            }, factory)
                .tex(factory.getIconLocation(), factory.getIconLocation());
    }
}
