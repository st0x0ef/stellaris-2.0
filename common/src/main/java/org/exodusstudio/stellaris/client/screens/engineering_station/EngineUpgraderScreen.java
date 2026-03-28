package org.exodusstudio.stellaris.client.screens.engineering_station;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.menus.engineering_station.EngineUpgradeMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.Utils;

public class EngineUpgraderScreen extends AbstractContainerScreen<EngineUpgradeMenu> {
    private static final Identifier GUI_LOCATION = IdentifierUtils.guiTexture("upgrade_station");
    public static final Component TAB_NAME = Component.literal("Engine Upgrader");

    public EngineUpgraderScreen(EngineUpgradeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, TAB_NAME);
        this.imageWidth = 180;
        this.imageHeight = 188;

        this.titleLabelX = (180 - Minecraft.getInstance().font.width(TAB_NAME)) / 2;
        this.titleLabelY = 2;
    }

    @Override
    protected void init() {
        super.init();
        TexturedButton craftingButton = new TexturedButton(this.leftPos + this.imageWidth, this.topPos + 50 , 16,16,
                Component.empty(),
                button -> menu.openCraftingMenu())
                .tex(GUISprites.ROCKET_CRAFTING_TAB, GUISprites.ROCKET_CRAFTING_TAB_HOVER)
                .tooltip(Tooltip.create(RocketStationScreen.TAB_NAME))
                .useSprite(true);
        this.addRenderableWidget(craftingButton);

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        EngineUpgradeMenu.Error error = this.menu.getErrorMessage(menu.getInputModule(), menu.getInputStack());
        if(error != EngineUpgradeMenu.Error.NONE) {
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, error.errorMessage, width / 2, topPos + 26, Utils.getMinecraftColor("red"));
        }
    }


    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, TAB_NAME, this.titleLabelX, this.titleLabelY, -11050641, false);
    }
}
