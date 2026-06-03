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
import org.exodusstudio.stellaris.client.screens.utils.GUIUtils;
import org.exodusstudio.stellaris.common.blocks.entities.machines.CoalGeneratorBlockEntity;
import org.exodusstudio.stellaris.common.menus.CoalGeneratorMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;


public class CoalGeneratorScreen extends AbstractContainerScreen<CoalGeneratorMenu> {

    private static final Identifier TEXTURE = IdentifierUtils.guiTexture("coal_generator");

    private final CoalGeneratorBlockEntity blockEntity = getMenu().getBlockEntity();
    private GaugeWidget energyGauge;

    public CoalGeneratorScreen(CoalGeneratorMenu abstractContainerMenu, Inventory inventory, Component component) {
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

        energyGauge.updateAmount(blockEntity.getEnergy(null).getEnergy());
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);

        if (menu.isLit()) {
            int i = Mth.ceil(menu.getLitProgress() * 11.0F) + 1;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, GUISprites.COAL_GENERATOR_LIT_PROGRESS_SPRITE, 14, 11, 0, 12 - i, leftPos + 99, topPos + 68 - i, 14, i);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y) {
        super.extractTooltip(guiGraphicsExtractor, x, y);
        GUIUtils.renderEnergyGeneratorGaugeTooltip(guiGraphicsExtractor, energyGauge, getMenu().getBlockEntity().getEnergyGeneratedPT(), x, y, font);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY) {
        guiGraphicsExtractor.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -11050641, false);
    }
}
