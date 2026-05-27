package org.exodusstudio.stellaris.client.screens.tablet.application.sd;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.screens.components.sd.SDCardDecodeButton;
import org.exodusstudio.stellaris.client.screens.components.sd.SDCardInfoWidget;
import org.exodusstudio.stellaris.client.screens.tablet.TabletAnimation;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.common.data.SdCard;
import org.exodusstudio.stellaris.common.data.SdCardData;
import org.exodusstudio.stellaris.common.menus.MainTabletMenu;
import org.exodusstudio.stellaris.common.menus.SDCardReaderApplicationMenu;
import org.exodusstudio.stellaris.common.network.packets.AwardStatPacket;
import org.exodusstudio.stellaris.common.network.packets.OpenMenuPacket;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.StatsRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;


public class SDCardReaderApplicationScreen extends AbstractContainerScreen<@NotNull SDCardReaderApplicationMenu> {

    public static final Identifier TEXTURE = IdentifierUtils.guiTexture("tablet/sd/sd_card_decoder");

    public static SDCardReaderApplicationScreen create(ApplicationRegistry.MenuHolder<MainTabletMenu> menuHolder) {
        NetworkManager.sendToServer(new OpenMenuPacket("sd_card_reader"));
        return null;
    }

    private SDCardInfoWidget cardInfoWidget;
    private Button decodeButton;
    private final TabletAnimation animation = new TabletAnimation();

    public SDCardReaderApplicationScreen(SDCardReaderApplicationMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);

        imageWidth = 310;
        imageHeight = 192;
    }

    @Override
    protected void init() {
        super.init();

        cardInfoWidget = new SDCardInfoWidget(width / 2 + 39, height / 2 - 66, 90, 134, null);

        decodeButton = new SDCardDecodeButton(width / 2 - 76, height / 2 - 48, 100, 21, (btn) -> {
            if (this.getMenu().hasCard()) {
                ItemStack cardItemStack = this.getMenu().getCard();
                String name = cardItemStack.get(DataComponentsRegistry.SD_CARD_NAME.get());
                if (name == null) {
                    Stellaris.LOG.error("SD Card data component (SD_CARD_ID) is null!");
                    return;
                }

                SdCard card = SdCardData.getSdCard(name);

                // If the card is already decoded, do nothing
                if (card == null || cardInfoWidget.getCard() == card) return;

                cardInfoWidget.setCard(card);
                cardInfoWidget.active = true;
                NetworkManager.sendToServer(new AwardStatPacket(StatsRegistry.SD_CARD_READ.get()));
            }
        });
        this.addRenderableWidget(decodeButton);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.children().forEach(l -> l.mouseScrolled(mouseX, mouseY, scrollX, scrollY));
        cardInfoWidget.mouseScrolled(mouseX, mouseY, scrollX, scrollY);

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.animation.finishClosing(this::closeWithoutAnimation)) {
            return;
        }

        float centerX = this.leftPos + this.imageWidth / 2.0F;
        float centerY = this.topPos + this.imageHeight / 2.0F;
        int transformedMouseX = this.animation.transformMouseX(mouseX, centerX, partialTicks);
        int transformedMouseY = this.animation.transformMouseY(mouseY, centerY, partialTicks);

        this.animation.renderBackdrop(graphics, this.width, this.height, partialTicks);
        this.animation.renderTabletShadow(graphics, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, partialTicks);
        this.animation.pushScreen(graphics, centerX, centerY, partialTicks);
        this.renderBg(graphics, partialTicks, transformedMouseX, transformedMouseY);
        super.render(graphics, transformedMouseX, transformedMouseY, partialTicks);

        this.cardInfoWidget.render(graphics, transformedMouseX, transformedMouseY, partialTicks);
        this.animation.renderGlassEffects(graphics, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, partialTicks);
        this.animation.popScreen(graphics);
        renderTooltip(graphics, mouseX, mouseY);

        this.decodeButton.active = this.getMenu().hasCard();
        this.cardInfoWidget.active = this.getMenu().hasCard() && this.cardInfoWidget.active;
        String cardID = this.getMenu().getCard().get(DataComponentsRegistry.SD_CARD_NAME.get());
        if (cardID == null) this.cardInfoWidget.setCard(null);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // The tablet texture is drawn inside render() so it shares the tablet transform.
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

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
