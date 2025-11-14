package org.exodusstudio.stellaris.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.components.GaugeChunkWidget;
import org.exodusstudio.stellaris.client.screens.components.GaugeWidget;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.blocks.entities.machines.ElectrolyzerBlockEntity;
import org.exodusstudio.stellaris.common.fluid.MultipleFluidStorage;
import org.exodusstudio.stellaris.common.fluid.SingleFluidStorage;
import org.exodusstudio.stellaris.common.menus.ElectrolyzerMenu;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

import java.util.List;

public class ElectrolyzerScreen extends AbstractContainerScreen<ElectrolyzerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocationUtils.guiTexture("water_separator");

    private final ElectrolyzerMenu menu;
    private final ElectrolyzerBlockEntity blockEntity;
    private GaugeChunkWidget ingredientTankGauge;
    private GaugeWidget firstIngredientGauge;
    private GaugeWidget secondIngredientGauge;
    private GaugeWidget energyGauge;

    public ElectrolyzerScreen(ElectrolyzerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        imageWidth = 180;
        imageHeight = 224;

        titleLabelX = (180 - Minecraft.getInstance().font.width(title.getString())) / 2;
        titleLabelY = 2;

        this.menu  = menu;
        this.blockEntity = menu.getBlockEntity();
    }

    @Override
    protected void init() {
        super.init();

        if (this.menu.getBlockEntity() == null) {
            return;
        }

        SingleFluidStorage ingredientTank = blockEntity.ingredientTank;
        ingredientTankGauge = new GaugeChunkWidget(leftPos + 53, topPos + 54, 76, 46, Component.translatable("stellaris.screen.water"), GUISprites.WATER_OVERLAY, GUISprites.WATER_SEPARATOR_OVERLAY, ingredientTank.getTankCapacity(0), GaugeChunkWidget.Direction4.DOWN_UP);
        addRenderableWidget(ingredientTankGauge);

        MultipleFluidStorage resultTanks = blockEntity.resultTanks;
        firstIngredientGauge = new GaugeWidget(leftPos + 22, topPos + 54, 12, 46, Component.translatable("stellaris.screen.hydrogen"), GUISprites.HYDROGEN_OVERLAY, GUISprites.FLUID_TANK_OVERLAY, resultTanks.getTankCapacity(0), GaugeWidget.Direction4.UP_DOWN);
        addRenderableWidget(firstIngredientGauge);

        secondIngredientGauge = new GaugeWidget(leftPos + 146, topPos + 54, 12, 46, Component.translatable("stellaris.screen.oxygen"), GUISprites.OXYGEN_OVERLAY, GUISprites.FLUID_TANK_OVERLAY, resultTanks.getTankCapacity(1), GaugeWidget.Direction4.UP_DOWN);
        addRenderableWidget(secondIngredientGauge);

        energyGauge = new GaugeWidget(leftPos + 68, topPos + 20, 44, 6, Component.translatable("stellaris.screen.energyContainer"), GUISprites.SIDEWAYS_ENERGY_FULL, null, blockEntity.getEnergy(null).getMaxEnergy(), GaugeWidget.Direction4.LEFT_RIGHT);
        addRenderableWidget(energyGauge);
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        if (blockEntity == null) {
            return;
        }

        ingredientTankGauge.updateAmount((int) blockEntity.ingredientTank.getFluidValueInTank());
        firstIngredientGauge.updateAmount((int) blockEntity.resultTanks.getFluidValueInTank(0));
        secondIngredientGauge.updateAmount((int) blockEntity.resultTanks.getFluidValueInTank(1));
        energyGauge.updateAmount(blockEntity.getEnergy(null).getEnergy());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
        ingredientTankGauge.renderTooltips(guiGraphics, x, y, this.font, List::of);
        firstIngredientGauge.renderTooltips(guiGraphics, x, y, this.font, List::of);
        secondIngredientGauge.renderTooltips(guiGraphics, x, y, this.font, List::of);
        energyGauge.renderTooltips(guiGraphics, x, y, this.font, List::of);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 5726575, false);
    }

}
