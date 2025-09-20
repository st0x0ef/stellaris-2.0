package org.exodusstudio.stellaris.client.screens.tablet.application.sd;

import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.components.sd.SDCardInfoWidget;
import org.exodusstudio.stellaris.client.screens.tablet.application.ApplicationRegistry;
import org.exodusstudio.stellaris.common.menu.MainTabletMenu;
import org.exodusstudio.stellaris.common.menus.SDCardReaderApplicationMenu;
import org.exodusstudio.stellaris.common.network.packets.OpenMenuPacket;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.SDCardsRegistry;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

@Environment(EnvType.CLIENT)
public class SDCardReaderApplicationScreen extends AbstractContainerScreen<SDCardReaderApplicationMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocationUtils.guiTexture("coal_generator");

    public static SDCardReaderApplicationScreen create(ApplicationRegistry.MenuHolder<MainTabletMenu> menuHolder) {
        NetworkManager.sendToServer(new OpenMenuPacket("sd_card_reader"));
        return null;
    }

    private SDCardInfoWidget cardInfoWidget = new SDCardInfoWidget(40, 40, 200, 200, null);;
    private Button decodeButton;

    public SDCardReaderApplicationScreen(SDCardReaderApplicationMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);

        imageWidth = 180;
        imageHeight = 188;
    }

    @Override
    protected void init() {
        super.init();

        decodeButton = Button.builder(Component.literal("DECODE"), (press) -> {
            if (this.getMenu().hasCard()) {
                var cardItemStack = this.getMenu().getCard();
                var card = SDCardsRegistry.get(cardItemStack.get(DataComponentsRegistry.SD_CARD_ID.get()));

                cardInfoWidget.setCard(card);
                cardInfoWidget.active = true;
            }
        }).build();
        this.addRenderableWidget(decodeButton);
        //this.addRenderableWidget(cardInfoWidget);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.children().forEach(l -> l.mouseScrolled(mouseX, mouseY, scrollX, scrollY));
        cardInfoWidget.mouseScrolled(mouseX, mouseY, scrollX, scrollY);

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);

        this.cardInfoWidget.render(graphics, mouseX, mouseY, partialTicks);

        this.decodeButton.active = this.getMenu().hasCard();
        this.cardInfoWidget.active = this.getMenu().hasCard() && this.cardInfoWidget.active;
        var cardID = this.getMenu().getCard().get(DataComponentsRegistry.SD_CARD_ID.get());
        if (cardID == null) this.cardInfoWidget.setCard(null);
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
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, -11050641, false);
    }

}
