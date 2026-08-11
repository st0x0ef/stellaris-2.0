package org.exodusstudio.stellaris.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.components.GaugeWidget;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.blocks.entities.machines.VacuumatorBlockEntity;
import org.exodusstudio.stellaris.common.menus.VacuumatorMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;


public class VacuumatorScreen extends AbstractContainerScreen<VacuumatorMenu> {
    public static final Identifier texture = IdentifierUtils.guiTexture("vacuumator");

    private final VacuumatorBlockEntity blockEntity = getMenu().getBlockEntity();
    private GaugeWidget energyGauge;
    private GaugeWidget waterGauge;

    public VacuumatorScreen(VacuumatorMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component, 180, 188);

        this.inventoryLabelY = this.imageHeight - 95;

        titleLabelX = (180 - Minecraft.getInstance().font.width(title.getString())) / 2;
        titleLabelY = 2;
    }

    @Override
    protected void init() {
        super.init();

        if (blockEntity == null) {
            return;
        }

        energyGauge = new GaugeWidget(leftPos + 68, topPos + 20, 44, 6, Component.translatable("stellaris.screen.energyContainer"),
                GUISprites.SIDEWAYS_ENERGY_FULL, null, blockEntity.getEnergy(null).getMaxEnergy(), GaugeWidget.Direction4.LEFT_RIGHT);
        addRenderableWidget(energyGauge);

        waterGauge = new GaugeWidget(leftPos + 124, topPos + 44, 12, 46, Component.translatable("stellaris.screen.water"),
                GUISprites.WATER_OVERLAY, GUISprites.FLUID_TANK_OVERLAY, blockEntity.getWaterTank().getTankCapacity(0), GaugeWidget.Direction4.DOWN_UP);
        addRenderableWidget(waterGauge);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        this.extractBackground(graphics,mouseX,mouseY,partialTicks);
        super.extractContents(graphics, mouseX, mouseY, partialTicks);
        this.extractTooltip(graphics, mouseX, mouseY);

        if (blockEntity == null) {
            return;
        }

        energyGauge.updateAmount(blockEntity.getEnergy(null).getEnergy());
        waterGauge.updateAmount(blockEntity.getWaterTank().getFluidValueInTank());

        if (menu.isLit()) {
            int i = 43 - Mth.ceil(menu.getLitProgress() * 43);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, GUISprites.VACUUMATOR_PROGRESS_SPRITE, 10, 43, 0, 0, leftPos + 18, topPos + 43, 10, i);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int x, int y) {
        super.extractTooltip(guiGraphics, x, y);
        energyGauge.renderTooltips(guiGraphics, x, y, font);
        waterGauge.renderTooltips(guiGraphics, x, y, font);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -11050641, false);
    }
}
