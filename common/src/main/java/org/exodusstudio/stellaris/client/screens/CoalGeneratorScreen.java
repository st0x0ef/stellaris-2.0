package org.exodusstudio.stellaris.client.screens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.components.GaugeWidget;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.client.screens.utils.GUIUtils;
import org.exodusstudio.stellaris.common.blocks.entities.machines.CoalGeneratorBlockEntity;
import org.exodusstudio.stellaris.common.menus.CoalGeneratorMenu;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

@Environment(EnvType.CLIENT)
public class CoalGeneratorScreen extends AbstractContainerScreen<CoalGeneratorMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocationUtils.guiTexture("coal_generator");

    private final CoalGeneratorBlockEntity blockEntity = getMenu().getBlockEntity();
    private GaugeWidget energyGauge;

    public CoalGeneratorScreen(CoalGeneratorMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);

        imageWidth = 180;
        imageHeight = 188;

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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);

        if (blockEntity == null) {
            return;
        }

        energyGauge.updateAmount(blockEntity.getEnergy(null).getEnergy());
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);

        if (menu.isLit()) {
            int i = Mth.ceil(menu.getLitProgress() * 11.0F) + 1;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, GUISprites.COAL_GENERATOR_LIT_PROGRESS_SPRITE, 14, 11, 0, 12 - i, leftPos + 99, topPos + 68 - i, 12, i);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
        GUIUtils.renderEnergyGaugeTooltip(guiGraphics, energyGauge, getMenu().getBlockEntity().getEnergyGeneratedPT(), x, y, font);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, -11050641, false);
    }
}
