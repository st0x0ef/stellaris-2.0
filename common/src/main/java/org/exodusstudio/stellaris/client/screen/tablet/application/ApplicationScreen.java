package org.exodusstudio.stellaris.client.screen.tablet.application;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.screen.tablet.MainTabletScreen;
import org.exodusstudio.stellaris.common.network.packets.OpenMenuPacket;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

public class ApplicationScreen extends Screen {

    public static final ResourceLocation BACKGROUND = ResourceLocationUtils.guiTexture("tablet/tablet_background");

    public final MainTabletScreen mainTablet;

    public ApplicationScreen(MainTabletScreen mainTablet, Component title) {
        super(title);

        this.mainTablet = mainTablet;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.mainTablet.getLeftPos(), this.mainTablet.getTopPos(), 0, 0, 250, 162, 250, 162);
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

    }

    public int getLeftPos() {
        return this.mainTablet.getLeftPos();
    }

    public int getTopPos() {
        return this.mainTablet.getTopPos();
    }

    public Player getPlayer() {
        return this.mainTablet.player;
    }

    public void openMainTabletScreen() {
        NetworkManager.sendToServer(new OpenMenuPacket("main_tablet"));
    }
}
