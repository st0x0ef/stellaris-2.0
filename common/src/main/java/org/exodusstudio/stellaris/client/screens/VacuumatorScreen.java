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
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.screens.components.GaugeWidget;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.client.screens.utils.GUIUtils;
import org.exodusstudio.stellaris.common.blocks.entities.machines.VacuumatorBlockEntity;
import org.exodusstudio.stellaris.common.menus.VacuumatorMenu;

@Environment(EnvType.CLIENT)
public class VacuumatorScreen extends AbstractContainerScreen<VacuumatorMenu> {
    public static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Stellaris.MOD_ID, "textures/gui/vacuumator.png");

    private final VacuumatorBlockEntity blockEntity = getMenu().getBlockEntity();
    private GaugeWidget energyGauge;

    public VacuumatorScreen(VacuumatorMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
        this.imageWidth = 180;
        this.imageHeight = 188;

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

        energyGauge = new GaugeWidget(leftPos + 67, topPos + 20, 44, 6, Component.translatable("stellaris.screen.energyContainer"),
                GUISprites.SIDEWAYS_ENERGY_FULL, null, blockEntity.getEnergy(null).getMaxEnergy(), GaugeWidget.Direction4.LEFT_RIGHT);
        addRenderableWidget(energyGauge);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics,mouseX,mouseY,partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        if (blockEntity == null) {
            return;
        }

        energyGauge.updateAmount(blockEntity.getEnergy(null).getEnergy());
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float var2, int var3, int var4) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        if (menu.isLit()) {
            int i = Mth.ceil(menu.getLitProgress() * 11.0F) + 1;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, GUISprites.VACUUMATOR_PROGRESS_SPRITE, 116, 34, 0, 34 - i, leftPos + 99, topPos + 68 - i, 116, i);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
        GUIUtils.renderEnergyGaugeTooltip(guiGraphics, energyGauge, getMenu().getBlockEntity().getEnergy(null).getEnergy(), x, y, font);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, -11050641, false);
    }
}
