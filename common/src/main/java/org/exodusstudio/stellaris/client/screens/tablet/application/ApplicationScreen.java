package org.exodusstudio.stellaris.client.screens.tablet.application;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.exodusstudio.stellaris.common.network.packets.OpenMenuPacket;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

/**
 * ApplicationScreen
 * This screen serves as a base for all applications on the tablet.
 * It provides common functionality such as rendering the background and handling player inventory.
 */
public class ApplicationScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    public static final ResourceLocation BACKGROUND = ResourceLocationUtils.guiTexture("tablet/tablet_background");

    public final Player player;
    public final Inventory inventory;

    public ApplicationScreen(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.player = inventory.player;
        this.imageHeight = 162;
        this.imageWidth = 250;
        this.inventoryLabelY = -this.imageHeight;
        this.titleLabelY = -this.imageHeight;

        this.inventory = inventory;
    }



    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

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

    public Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }

    public void openMainTabletScreen() {
        NetworkManager.sendToServer(new OpenMenuPacket("main_tablet"));
    }
}
