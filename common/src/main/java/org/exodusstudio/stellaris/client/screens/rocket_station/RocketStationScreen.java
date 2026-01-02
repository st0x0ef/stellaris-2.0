package org.exodusstudio.stellaris.client.screens.rocket_station;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.menus.rocket_station.RocketStationMenu;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

public class RocketStationScreen extends AbstractContainerScreen<RocketStationMenu> {
    private static final ResourceLocation GUI_LOCATION = ResourceLocationUtils.guiTexture("rocket_station"); //temporary

    public RocketStationScreen(RocketStationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 180;
        this.imageHeight = 224;

        this.titleLabelX = (180 - Minecraft.getInstance().font.width(title.getString())) / 2;
        this.titleLabelY = 2;

        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        TexturedButton upgradeButton = new TexturedButton(this.leftPos + this.imageWidth, this.topPos + 50 , 16,16,
                Component.empty(),
                button -> menu.openUpgradeScreen())
                .tex(GUISprites.MODULES_TAB, GUISprites.MODULES_TAB_HOVER)
                .tooltip(Tooltip.create(Component.literal("Rocket Upgrading")))
                .useSprite(true);
        this.addRenderableWidget(upgradeButton);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }


    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

    }
}
