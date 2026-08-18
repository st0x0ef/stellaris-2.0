package org.exodusstudio.stellaris.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.components.GaugeWidget;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.blocks.ElectricLightBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.ElectricLightBlockEntity;
import org.exodusstudio.stellaris.common.menus.ElectricLightMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class ElectricLightScreen extends AbstractContainerScreen<ElectricLightMenu> {

    private static final Identifier TEXTURE = IdentifierUtils.guiTexture("basic_machine");

    private final ElectricLightBlockEntity blockEntity = getMenu().getBlockEntity();
    private GaugeWidget energyGauge;

    private AbstractSliderButton brightnessSlider;

    public ElectricLightScreen(ElectricLightMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component, 180, 120);

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

        brightnessSlider = new AbstractSliderButton(leftPos + 30, topPos + 50, 120, 20, brightnessMessage(blockEntity.getBrightness()), toSliderValue(blockEntity.getBrightness())) {
            @Override
            protected void updateMessage() {
                this.setMessage(brightnessMessage(toBrightness()));
            }

            @Override
            protected void applyValue() {
                blockEntity.setBrightness(toBrightness(), true);
            }

            @Override
            public boolean keyPressed(KeyEvent event) {
                boolean left = event.key() == 263;
                if (left || event.key() == 262) {
                    this.setValue(toSliderValue(toBrightness() + (left ? -1 : 1))); // adjust by one step
                    return true;
                }

                return false;
            }

            private int toBrightness() {
                return (int) Math.round(this.value * ElectricLightBlock.MAX_BRIGHTNESS);
            }
        };
        addRenderableWidget(brightnessSlider);
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
    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
        if (brightnessSlider.isHovered()) {
            brightnessSlider.mouseDragged(event, mouseX, mouseY);
        }
        return super.mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int x, int y) {
        super.extractTooltip(guiGraphics, x, y);
        energyGauge.renderTooltips(guiGraphics, x, y, font);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -11050641, false);
    }

    private static Component brightnessMessage(int brightness) {
        return Component.translatable("stellaris.screen.electricLight.brightness", brightness, ElectricLightBlock.MAX_BRIGHTNESS);
    }

    private static double toSliderValue(int brightness) {
        return Mth.clamp(brightness, 0, ElectricLightBlock.MAX_BRIGHTNESS) / (double) ElectricLightBlock.MAX_BRIGHTNESS;
    }
}
