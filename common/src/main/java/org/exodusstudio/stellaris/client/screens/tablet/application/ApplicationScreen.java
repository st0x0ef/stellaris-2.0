package org.exodusstudio.stellaris.client.screens.tablet.application;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.exodusstudio.stellaris.common.network.packets.OpenMenuPacket;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

/**
 * ApplicationScreen
 * This screen serves as a base for all applications on the tablet.
 * It provides common functionality such as rendering the background and handling player inventory.
 */
public class ApplicationScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    public static final Identifier BACKGROUND = IdentifierUtils.guiTexture("tablet/tablet_background");
    public static final Identifier BLANCK_BACKGROUND = IdentifierUtils.guiTexture("tablet/tablet_background_blanck");
    public static final Identifier SIDE_LIGHTS = IdentifierUtils.guiTexture("tablet/side_light");

    public final Player player;
    public final Inventory inventory;

    public ApplicationScreen(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 250, 162);
        this.player = inventory.player;
        this.inventoryLabelY = -this.imageHeight;
        this.titleLabelY = -this.imageHeight;

        this.inventory = inventory;
    }


    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
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
