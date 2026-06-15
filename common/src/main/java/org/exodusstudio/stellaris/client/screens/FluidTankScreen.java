package org.exodusstudio.stellaris.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.components.GaugeChunkWidget;
import org.exodusstudio.stellaris.client.screens.components.GaugeWidget;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.blocks.entities.FluidTankBlockEntity;
import org.exodusstudio.stellaris.common.menus.FluidTankMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.List;

public class FluidTankScreen extends AbstractContainerScreen<FluidTankMenu> {

    private static final Identifier TEXTURE = IdentifierUtils.guiTexture("fluid_tank");

    private final FluidTankBlockEntity blockEntity = getMenu().getBlockEntity();
    private GaugeChunkWidget fluidGauge;

    public FluidTankScreen(FluidTankMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 180, 188);

        titleLabelX = (180 - Minecraft.getInstance().font.width(title.getString())) / 2;
        titleLabelY = 2;
    }

    @Override
    protected void init() {
        super.init();

        if (blockEntity == null) {
            return;
        }


        fluidGauge = new GaugeChunkWidget(leftPos + 84, topPos + 36, 12, 46, blockEntity.getFluidTank().getFluidInTank(0), GUISprites.FLUID_TANK_OVERLAY, blockEntity.getFluidTank().getTankCapacity(0), GaugeWidget.Direction4.DOWN_UP);
        addRenderableWidget(fluidGauge);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.extractContents(guiGraphics, mouseX, mouseY, partialTick);
        extractTooltip(guiGraphics, mouseX, mouseY);

        fluidGauge.updateAmount(this.blockEntity.getFluidTank().getFluidValueInTank());

    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int x, int y) {
        super.extractTooltip(guiGraphics, x, y);
        Component tooltip = blockEntity.getFluidTank().isEmpty() ?
                Component.translatable("stellaris.screen.empty_fluid") :
                blockEntity.getFluidTank().getFluidInTank(0).getName();
        fluidGauge.setMessage(tooltip);
        fluidGauge.renderTooltips(guiGraphics, x, y, font);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -11050641, false);
    }
}
