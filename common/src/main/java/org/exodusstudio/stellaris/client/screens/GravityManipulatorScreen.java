package org.exodusstudio.stellaris.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.components.GaugeWidget;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;
import org.exodusstudio.stellaris.common.menus.GravityManipulatorMenu;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

import java.util.List;


public class GravityManipulatorScreen extends AbstractContainerScreen<GravityManipulatorMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocationUtils.guiTexture("gravity_manipulator");

    private final GravityManipulatorBlockEntity blockEntity = getMenu().getBlockEntity();
    private GaugeWidget energyGauge;

    private AbstractSliderButton gravitySlider;

    public GravityManipulatorScreen(GravityManipulatorMenu abstractContainerMenu, Inventory inventory, Component component) {
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

        gravitySlider = new AbstractSliderButton(leftPos + 30, topPos + 50, 120, 20, Component.translatable("stellaris.screen.gravityManipulator.gravity"), blockEntity.getNormalizedGravity()) {
            @Override
            protected void updateMessage() {
                this.setMessage(Component.translatable("stellaris.screen.gravityManipulator.gravity", String.format("%.2f", this.value * 20.0)));
            }

            @Override
            protected void applyValue() {
                double gravityValue = this.value * 20.0; // Scale from 0.0-1.0 to 0.0-20.0, TODO: make max gravity configurable
                blockEntity.setGravity(gravityValue, true);
            }
        };
        addRenderableWidget(gravitySlider);
        gravitySlider.setMessage(Component.translatable("stellaris.screen.gravityManipulator.gravity", String.format("%.2f", blockEntity.getGravity())));
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
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        gravitySlider.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
        energyGauge.renderTooltips(guiGraphics, x, y, font, List::of);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, -11050641, false);
    }
}
