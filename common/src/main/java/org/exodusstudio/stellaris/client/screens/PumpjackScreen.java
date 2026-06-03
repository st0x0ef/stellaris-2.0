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
import org.exodusstudio.stellaris.common.blocks.entities.machines.PumpjackBlockEntity;
import org.exodusstudio.stellaris.common.fluid.SingleFluidStorage;
import org.exodusstudio.stellaris.common.menus.PumpjackMenu;
import org.exodusstudio.stellaris.common.oil.OilUtils;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.List;

public class PumpjackScreen extends AbstractContainerScreen<PumpjackMenu> {

    private static final Identifier TEXTURE = IdentifierUtils.guiTexture("pumpjack");

    private final PumpjackBlockEntity blockEntity = getMenu().getBlockEntity();
    private GaugeWidget resultTankGauge;
    private GaugeWidget energyGauge;

    public PumpjackScreen(PumpjackMenu menu, Inventory playerInventory, Component title) {
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

        SingleFluidStorage resultTank = blockEntity.getResultTank();
        resultTankGauge = new GaugeWidget(leftPos + 114, topPos + 46, 12, 46, Component.translatable("stellaris.screen.oil"), GUISprites.OIL_OVERLAY, GUISprites.FLUID_TANK_OVERLAY, resultTank.getTankCapacity(0), GaugeWidget.Direction4.DOWN_UP);
        addRenderableWidget(resultTankGauge);

        energyGauge = new GaugeWidget(leftPos + 68, topPos + 20, 44, 6, Component.translatable("stellaris.screen.energyContainer"), GUISprites.SIDEWAYS_ENERGY_FULL, null, blockEntity.getEnergy(null).getMaxEnergy(), GaugeWidget.Direction4.LEFT_RIGHT);
        addRenderableWidget(energyGauge);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.extractContents(guiGraphics, mouseX, mouseY, partialTick);
        extractTooltip(guiGraphics, mouseX, mouseY);

        if (blockEntity == null || Minecraft.getInstance().level == null) {
            return;
        }

        guiGraphics.text(this.font, "Oil Level", leftPos + 19, topPos + 40, Utils.getColorHexCode("gray"));
        guiGraphics.centeredText(this.font, String.valueOf(blockEntity.chunkOilLevel(Minecraft.getInstance().level)), leftPos + 40, topPos + 51, OilUtils.getOilLevelColor(blockEntity.chunkOilLevel(Minecraft.getInstance().level)));

        resultTankGauge.updateAmount(blockEntity.getResultTank().getFluidValueInTank());
        energyGauge.updateAmount(blockEntity.getEnergy(null).getEnergy());
    }


    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int x, int y) {
        super.extractTooltip(guiGraphics, x, y);
        resultTankGauge.renderTooltips(guiGraphics, x, y, font, List::of);
        energyGauge.renderTooltips(guiGraphics, x, y, font, List::of);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -11050641, false);
    }
}
