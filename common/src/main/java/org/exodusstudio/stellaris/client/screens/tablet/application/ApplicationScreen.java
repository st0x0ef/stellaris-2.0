package org.exodusstudio.stellaris.client.screens.tablet.application;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.exodusstudio.stellaris.client.screens.tablet.TabletAnimation;
import org.exodusstudio.stellaris.common.network.packets.OpenMenuPacket;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.lwjgl.glfw.GLFW;

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
    private final TabletAnimation animation = new TabletAnimation();

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
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.animation.finishClosing(this::closeWithoutAnimation)) {
            return;
        }

        float centerX = this.leftPos + this.imageWidth / 2.0F;
        float centerY = this.topPos + this.imageHeight / 2.0F;
        int transformedMouseX = this.animation.transformMouseX(mouseX, centerX, partialTick);
        int transformedMouseY = this.animation.transformMouseY(mouseY, centerY, partialTick);

        this.animation.renderBackdrop(guiGraphics, this.width, this.height, partialTick);
        this.animation.renderTabletShadow(guiGraphics, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, partialTick);
        this.animation.pushScreen(guiGraphics, centerX, centerY, partialTick);
        this.renderBg(guiGraphics, partialTick, transformedMouseX, transformedMouseY);
        super.render(guiGraphics, transformedMouseX, transformedMouseY, partialTick);
        this.animation.renderGlassEffects(guiGraphics, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, partialTick);
        this.animation.popScreen(guiGraphics);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // The tablet texture is drawn inside render() so it shares the tablet transform.
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

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

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.animation.isClosing()) {
            return true;
        }

        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            this.animation.startClosing();
            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    public void onClose() {
        if (this.animation.shouldInterceptClose()) {
            return;
        }

        this.closeWithoutAnimation();
    }

    private void closeWithoutAnimation() {
        super.onClose();
    }
}
