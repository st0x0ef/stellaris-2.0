package org.exodusstudio.stellaris.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.components.GaugeChunkWidget;
import org.exodusstudio.stellaris.client.screens.components.GaugeWidget;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.blocks.entities.machines.ElectrolyzerBlockEntity;
import org.exodusstudio.stellaris.common.fluid.MultipleFluidStorage;
import org.exodusstudio.stellaris.common.fluid.SingleFluidStorage;
import org.exodusstudio.stellaris.common.menus.ElectrolyzerMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.List;

public class ElectrolyzerScreen extends AbstractContainerScreen<ElectrolyzerMenu> {
    private static final Identifier TEXTURE = IdentifierUtils.guiTexture("electrolyzer");

    private final ElectrolyzerMenu menu;
    private final ElectrolyzerBlockEntity blockEntity;
    private GaugeChunkWidget ingredientTankGauge;
    private GaugeChunkWidget firstIngredientGauge;
    private GaugeChunkWidget secondIngredientGauge;
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
        MultipleFluidStorage resultTanks = blockEntity.resultTanks;

        ingredientTankGauge = new GaugeChunkWidget(leftPos + 53, topPos + 54, 76, 46, ingredientTank.getFluidInTank(0), GUISprites.ELECTROLYZER_OVERLAY_BIG, ingredientTank.getTankCapacity(0), GaugeChunkWidget.Direction4.DOWN_UP);
        addRenderableWidget(ingredientTankGauge);

        firstIngredientGauge = new GaugeChunkWidget(leftPos + 22, topPos + 54, 12, 46, resultTanks.getFluidInTank(0), GUISprites.FLUID_TANK_OVERLAY, resultTanks.getTankCapacity(0), GaugeWidget.Direction4.DOWN_UP);
        addRenderableWidget(firstIngredientGauge);


        secondIngredientGauge = new GaugeChunkWidget(leftPos + 146, topPos + 54, 12, 46, resultTanks.getFluidInTank(1), GUISprites.FLUID_TANK_OVERLAY, resultTanks.getTankCapacity(1), GaugeWidget.Direction4.DOWN_UP);
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

        if(ingredientTankGauge != null) ingredientTankGauge.updateAmount(blockEntity.ingredientTank, 0);
        if(firstIngredientGauge != null)firstIngredientGauge.updateAmount(blockEntity.resultTanks, 0);
        if(secondIngredientGauge != null)secondIngredientGauge.updateAmount(blockEntity.resultTanks, 1);
        if(energyGauge != null)energyGauge.updateAmount(blockEntity.getEnergy(null).getEnergy());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);

        ingredientTankGauge.renderTooltips(guiGraphics, x, y, this.font, List::of);

        Component tooltip = blockEntity.resultTanks.isEmpty() ?
                Component.translatable("stellaris.screen.empty_fluid") :
                blockEntity.resultTanks.getFluidInTank(0).getName();
        firstIngredientGauge.setMessage(tooltip);

        tooltip = blockEntity.resultTanks.isEmpty() ?
                Component.translatable("stellaris.screen.empty_fluid") :
                blockEntity.resultTanks.getFluidInTank(1).getName();
        secondIngredientGauge.setMessage(tooltip);

        firstIngredientGauge.renderTooltips(guiGraphics, x, y, this.font, List::of);
        secondIngredientGauge.renderTooltips(guiGraphics, x, y, this.font, List::of);

        energyGauge.renderTooltips(guiGraphics, x, y, this.font, List::of);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, -11050641, false);
    }

}
