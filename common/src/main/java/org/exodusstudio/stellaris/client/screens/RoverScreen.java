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

    private static final Identifier SMALL_INVENTORY_TEXTURE = IdentifierUtils.guiTexture("vehicle_small_inventory");
    private static final Identifier FULL_INVENTORY_TEXTURE = IdentifierUtils.guiTexture("vehicle_full_inventory");

    private final RoverEntity rover;
    private GaugeWidget fuelGauge;

    public RoverScreen(RoverMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component, 180, 224);
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

        fuelGauge = new GaugeWidget(leftPos + 100, topPos + 20, 12, 46,
                Component.translatable("stellaris.screen.diesel"),
                rover.getFuelType().getFuelTexture(), GUISprites.FLUID_TANK_OVERLAY,
                rover.getTankCapacity(), GaugeWidget.Direction4.DOWN_UP);
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
        fuelGauge.updateSprite(rover.getFuelType().getFuelTexture());

        fuelGauge.renderTooltips(graphics, mouseX, mouseY, font);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        if (rover != null) {
            Identifier texture = rover.hasCargoModule() ? FULL_INVENTORY_TEXTURE : SMALL_INVENTORY_TEXTURE;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -11050641, false);
    }
}
