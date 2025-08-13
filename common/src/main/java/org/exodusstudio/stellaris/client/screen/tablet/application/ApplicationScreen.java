package org.exodusstudio.stellaris.client.screen.tablet.application;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.menu.MainTabletMenu;
import org.exodusstudio.stellaris.common.network.packets.OpenMenuPacket;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

/**
 * ApplicationScreen
 * This screen serves as a base for all applications on the tablet.
 * It provides common functionality such as rendering the background and handling player inventory.
 */
public  class ApplicationScreen extends AbstractContainerScreen<MainTabletMenu> {

    public static final ResourceLocation BACKGROUND = ResourceLocationUtils.guiTexture("tablet/tablet_background");

    public final Player player;
    public final Inventory inventory;

    public ApplicationScreen(MainTabletMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.player = inventory.player;
        this.imageHeight = 162;
        this.imageWidth = 250;

        this.inventory = inventory;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {

    }

    public int getLeftPos() {
        return this.leftPos;
    }

    public int getTopPos() {
        return this.topPos;
    }

    public Player getPlayer() {
        return player;
    }

    public void openMainTabletScreen() {
        NetworkManager.sendToServer(new OpenMenuPacket("main_tablet"));
    }
}
