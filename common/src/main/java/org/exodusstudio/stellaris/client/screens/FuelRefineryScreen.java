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
import org.exodusstudio.stellaris.common.blocks.entities.machines.FuelRefineryBlockEntity;
import org.exodusstudio.stellaris.common.fluid.SingleFluidStorage;
import org.exodusstudio.stellaris.common.menus.FuelRefineryMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.List;

public class FuelRefineryScreen extends AbstractContainerScreen<FuelRefineryMenu> {

    private static final Identifier TEXTURE = IdentifierUtils.guiTexture("fuel_refinery");

    private final FuelRefineryBlockEntity blockEntity = getMenu().getBlockEntity();
    private GaugeWidget ingredientTankGauge;
    private GaugeWidget fuelTankGauge;
    private GaugeWidget dieselTankGauge;
    private GaugeWidget energyGauge;

    public FuelRefineryScreen(FuelRefineryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 180, 224);

        titleLabelX = (180 - Minecraft.getInstance().font.width(title.getString())) / 2;
        titleLabelY = 2;
    }

    @Override
    protected void init() {
        super.init();

        if (blockEntity == null) {
            return;
        }


        SingleFluidStorage ingredientTank = blockEntity.getIngredientTank();
        ingredientTankGauge = new GaugeWidget(leftPos + 42, topPos + 78, 12, 46, Component.translatable("stellaris.screen.oil"),
                GUISprites.OIL_OVERLAY, GUISprites.FLUID_TANK_OVERLAY, ingredientTank.getTankCapacity(0), GaugeWidget.Direction4.DOWN_UP);
        addRenderableWidget(ingredientTankGauge);

        SingleFluidStorage fuelTank = blockEntity.getOutputFuelTank();
        fuelTankGauge = new GaugeWidget(leftPos + 80, topPos + 78, 12, 46, Component.translatable("stellaris.screen.fuel"),
                GUISprites.FUEL_OVERLAY, GUISprites.FLUID_TANK_OVERLAY, fuelTank.getTankCapacity(0), GaugeWidget.Direction4.DOWN_UP);
        addRenderableWidget(fuelTankGauge);

        SingleFluidStorage dieselTank = blockEntity.getOutputFuelTank();
        dieselTankGauge = new GaugeWidget(leftPos + 128, topPos + 78, 12, 46, Component.translatable("stellaris.screen.diesel"),
                GUISprites.DIESEL_OVERLAY, GUISprites.FLUID_TANK_OVERLAY, dieselTank.getTankCapacity(0), GaugeWidget.Direction4.DOWN_UP);
        addRenderableWidget(dieselTankGauge);

        energyGauge = new GaugeWidget(leftPos + 68, topPos + 20, 44, 6, Component.translatable("stellaris.screen.energyContainer"), GUISprites.SIDEWAYS_ENERGY_FULL, null, blockEntity.getEnergy(null).getMaxEnergy(), GaugeWidget.Direction4.LEFT_RIGHT);
        addRenderableWidget(energyGauge);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.extractContents(guiGraphics, mouseX, mouseY, partialTick);
        extractTooltip(guiGraphics, mouseX, mouseY);

        if (blockEntity == null) {
            return;
        }

        ingredientTankGauge.updateAmount(blockEntity.getIngredientTank().getFluidValueInTank());
        fuelTankGauge.updateAmount(blockEntity.getOutputFuelTank().getFluidValueInTank());
        dieselTankGauge.updateAmount(blockEntity.getOutputDieselTank().getFluidValueInTank());
        energyGauge.updateAmount(blockEntity.getEnergy(null).getEnergy());
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int x, int y) {
        super.extractTooltip(guiGraphics, x, y);
        ingredientTankGauge.renderTooltips(guiGraphics, x, y, font);
        fuelTankGauge.renderTooltips(guiGraphics, x, y, font);
        dieselTankGauge.renderTooltips(guiGraphics, x, y, font);
        energyGauge.renderTooltips(guiGraphics, x, y, font);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -11050641, false);
    }
}
