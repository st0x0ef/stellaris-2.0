package org.exodusstudio.stellaris.client.screens.engineering_station;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.common.menus.engineering_station.RocketStationMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class RocketStationScreen extends AbstractContainerScreen<RocketStationMenu> {
    private static final Identifier GUI_LOCATION = IdentifierUtils.guiTexture("rocket_station"); //temporary
    public static final Component TAB_NAME = Component.literal("Rocket Station");

    public RocketStationScreen(RocketStationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, TAB_NAME, 180, 224);

        this.titleLabelX = (180 - Minecraft.getInstance().font.width(TAB_NAME)) / 2;
        this.titleLabelY = 2;
    }

    @Override
    protected void init() {
        super.init();

        EngineUpgraderScreen.addTabsButton(this.leftPos + this.imageWidth, this.topPos + 40, this, menu.engineeringStationPos, "crafting");

    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        extractBackground(graphics, mouseX, mouseY, partialTick);
        super.extractContents(graphics, mouseX, mouseY, partialTick);
        extractTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, TAB_NAME, this.titleLabelX, this.titleLabelY, -11050641, false);
    }
}
