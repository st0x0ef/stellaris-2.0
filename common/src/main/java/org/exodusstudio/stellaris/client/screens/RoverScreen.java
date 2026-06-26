package org.exodusstudio.stellaris.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.components.GaugeWidget;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.entities.vehicles.RoverEntity;
import org.exodusstudio.stellaris.common.menus.RoverMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class RoverScreen extends AbstractContainerScreen<RoverMenu> {

    private static final Identifier TEXTURE = IdentifierUtils.guiTexture("rover");

    private RoverEntity rover;
    private GaugeWidget fuelGauge;

    public RoverScreen(RoverMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component, 180, 188);
        this.rover = getMenu().getRover();

        titleLabelX = (180 - Minecraft.getInstance().font.width(title.getString())) / 2;
        titleLabelY = 2;
    }

    @Override
    protected void init() {
        super.init();

        if (rover == null) {
            return;
        }

        fuelGauge = new GaugeWidget(leftPos + 52, topPos + 30, 12, 46, Component.translatable("stellaris.screen.diesel"), rover.getRoverComponent().getFuelType().getFuelTexture(), GUISprites.FLUID_TANK_OVERLAY, rover.getRoverComponent().getTankCapacity(), GaugeWidget.Direction4.DOWN_UP);
        addRenderableWidget(fuelGauge);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        extractBackground(graphics, mouseX, mouseY, partialTicks);
        super.extractContents(graphics, mouseX, mouseY, partialTicks);
        extractTooltip(graphics, mouseX, mouseY);

        if (rover == null) {
            return;
        }

        fuelGauge.updateAmount(rover.getFuel());
        fuelGauge.updateSprite(rover.getRoverComponent().getFuelType().getFuelTexture());

        fuelGauge.renderTooltips(graphics, mouseX, mouseY, font);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 5726575, false);
    }
}
