package org.exodusstudio.stellaris.client.screen.tablet.application;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.client.screen.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

public class ApplicationScreen extends Screen {

    public static final ResourceLocation BACKGROUND = ResourceLocationUtils.guiTexture("tablet/tablet_background");

    public final MainTabletScreen mainTablet;

    protected ApplicationScreen(MainTabletScreen mainTablet, Component title) {
        super(title);

        this.mainTablet = mainTablet;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(BACKGROUND, this.mainTablet.getLeftPos(), this.mainTablet.getTopPos(), 0, 0, 250, 162, 250, 162);
    }

    public Player getPlayer() {
        return this.mainTablet.player;
    }
}
