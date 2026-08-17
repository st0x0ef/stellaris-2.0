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
import org.exodusstudio.stellaris.common.blocks.entities.machines.OxygenDistributorBlockEntity;
import org.exodusstudio.stellaris.common.menus.OxygenDistributorMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.OxygenUtils;
import org.exodusstudio.stellaris.common.utils.Utils;


public class OxygenDistributorScreen extends AbstractContainerScreen<OxygenDistributorMenu> {

    public static final Identifier TEXTURE = IdentifierUtils.guiTexture("oxygen_distributor");

    private final OxygenDistributorBlockEntity blockEntity = getMenu().getBlockEntity();
    private GaugeWidget oxygenGauge;
    private GaugeWidget energyGauge;

    public OxygenDistributorScreen(OxygenDistributorMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component, 180, 188);

        titleLabelX = (180 - Minecraft.getInstance().font.width(title.getString())) / 2;
        titleLabelY = 2;
    }

    @Override
    protected void init() {
        super.init();

        if (blockEntity == null) {
            return;
        }

        oxygenGauge = new GaugeWidget(leftPos + 76, topPos + 42, 12, 46, Component.translatable("fluid.stellaris.oxygen"),
                GUISprites.OXYGEN_OVERLAY, GUISprites.FLUID_TANK_OVERLAY, blockEntity.getOxygenTank().getTankCapacity(0), GaugeWidget.Direction4.DOWN_UP);
        addRenderableWidget(oxygenGauge);

        energyGauge = new GaugeWidget(leftPos + 68, topPos + 20, 44, 6, Component.translatable("stellaris.screen.energyContainer"), GUISprites.SIDEWAYS_ENERGY_FULL, null, blockEntity.getEnergy(null).getMaxEnergy(), GaugeWidget.Direction4.LEFT_RIGHT);
        addRenderableWidget(energyGauge);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        extractBackground(graphics, mouseX, mouseY, partialTicks);
        super.extractContents(graphics, mouseX, mouseY, partialTicks);
        extractTooltip(graphics, mouseX, mouseY);

        if (blockEntity == null) {
            return;
        }

        oxygenGauge.updateAmount(blockEntity.getOxygenTank().getFluidValueInTank());
        energyGauge.updateAmount(blockEntity.getEnergy(null).getEnergy());
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int x, int y) {
        super.extractTooltip(guiGraphics, x, y);
        oxygenGauge.renderTooltips(guiGraphics, x, y, this.font);
        energyGauge.renderTooltips(guiGraphics, x, y, this.font);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -11050641, false);

        if (blockEntity == null || blockEntity.getStatus() == OxygenUtils.OxygenStatus.OK) {
            return;
        }

        guiGraphics.centeredText(this.font, Component.translatable(blockEntity.getStatus().translationKey()),
                imageWidth / 2, 94, Utils.getMinecraftColor("red"));
    }
}
