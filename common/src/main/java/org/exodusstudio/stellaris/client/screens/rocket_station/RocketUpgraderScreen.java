package org.exodusstudio.stellaris.client.screens.rocket_station;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
import org.exodusstudio.stellaris.common.menus.UpgradeStationMenu;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;
import org.exodusstudio.stellaris.common.utils.Utils;

public class RocketUpgraderScreen extends AbstractContainerScreen<UpgradeStationMenu> {
    private static final ResourceLocation GUI_LOCATION = ResourceLocationUtils.guiTexture("upgrade_station"); //temporary

    public RocketUpgraderScreen(UpgradeStationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 180;
        this.imageHeight = 188;

        this.titleLabelX = (180 - Minecraft.getInstance().font.width(title.getString())) / 2;
        this.titleLabelY = 2;

    }

    @Override
    protected void init() {
        super.init();
        TexturedButton craftingButton = new TexturedButton(this.leftPos + this.imageWidth, this.topPos + 50 , 40, 20,
                Component.literal("Crafting"),
                button -> {
                    menu.openCraftingMenu();
                });
        this.addRenderableWidget(craftingButton);

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        UpgradeStationMenu.Error error = this.menu.canUpgradeFuel(menu.getInputModule(), menu.getInputRocket());
        if(error != UpgradeStationMenu.Error.NONE) {
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, error.errorMessage, width / 2, topPos + 26, Utils.getMinecraftColor("red"));
        }
    }


    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

    }
}
