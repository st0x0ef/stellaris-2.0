package org.exodusstudio.stellaris.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.components.GaugeWidget;
import org.exodusstudio.stellaris.client.screens.components.TextureComponentButton;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;
import org.exodusstudio.stellaris.common.menus.GravityManipulatorMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;


public class GravityManipulatorScreen extends AbstractContainerScreen<GravityManipulatorMenu> {

    private static final Identifier TEXTURE = IdentifierUtils.guiTexture("basic_machine");
    private static final Button.CreateNarration DEFAULT_NARRATION = supplier -> supplier.get().append("Gravity Manipulator Button");

    private final GravityManipulatorBlockEntity blockEntity = getMenu().getBlockEntity();
    private GaugeWidget energyGauge;

    private AbstractSliderButton gravitySlider;

    TextureComponentButton moonButton;
    TextureComponentButton marsButton;
    TextureComponentButton earthButton;

    private double minGravityValue;
    private double maxGravityValue;
    private double gravityRange;

    public GravityManipulatorScreen(GravityManipulatorMenu abstractContainerMenu, Inventory inventory, Component component) {
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

        minGravityValue = GravityManipulatorBlockEntity.getMinGravity();
        maxGravityValue = GravityManipulatorBlockEntity.getMaxGravity();
        gravityRange = Math.max(maxGravityValue - minGravityValue, 0.01);

        energyGauge = new GaugeWidget(leftPos + 68, topPos + 20, 44, 6, Component.translatable("stellaris.screen.energyContainer"), GUISprites.SIDEWAYS_ENERGY_FULL, null, blockEntity.getEnergy(null).getMaxEnergy(), GaugeWidget.Direction4.LEFT_RIGHT);
        addRenderableWidget(energyGauge);

        gravitySlider = new AbstractSliderButton(leftPos + 30, topPos + 45, 120, 20, Component.translatable("stellaris.screen.gravityManipulator.gravity"), blockEntity.getNormalizedGravity()) {
            @Override
            protected void updateMessage() {
                this.setMessage(Component.translatable("stellaris.screen.gravityManipulator.gravity", String.format("%.2f", toGravity(this.value))));
            }

            @Override
            protected void applyValue() {
                blockEntity.setGravity(toGravity(this.value), true); // Scale from 0.0-1.0 to minGravityValue-maxGravityValue
            }

            @Override
            public boolean keyPressed(KeyEvent event) {
                boolean bl = event.key() == 263;
                if (bl || event.key() == 262) {
                    float f = bl ? -1.0F : 1.0F;
                    this.setValue(this.value + 0.1 / gravityRange * f); // adjust by 0.1 gravity
                    return true;
                }

                return false;
            }
        };
        addRenderableWidget(gravitySlider);
        gravitySlider.setMessage(Component.translatable("stellaris.screen.gravityManipulator.gravity", String.format("%.2f", blockEntity.getGravity())));

        initPlanetButtons();
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
        if (gravitySlider.isHovered()) {
            gravitySlider.mouseDragged(event, mouseX, mouseY);
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

        moonButton.extractTooltip(guiGraphics, x, y);
        marsButton.extractTooltip(guiGraphics, x, y);
        earthButton.extractTooltip(guiGraphics, x, y);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -11050641, false);
    }

    private double toGravity(double sliderValue) {
        return minGravityValue + sliderValue * gravityRange;
    }

    private double toSliderValue(double gravity) {
        return (gravity - minGravityValue) / gravityRange;
    }

    private void  initPlanetButtons() {
        moonButton = new TextureComponentButton(leftPos + 50, topPos + 75, 20, 20, 14, 14,
                GUISprites.MOON,
                Component.translatable("stellaris.screen.gravityManipulator.planet.moon").getString(),
                button -> gravitySlider.setValue(toSliderValue(1.62)),
                DEFAULT_NARRATION);

        marsButton = new TextureComponentButton(leftPos + 80, topPos + 75, 20, 20, 14, 14,
                GUISprites.MARS,
                Component.translatable("stellaris.screen.gravityManipulator.planet.mars").getString(),
                button -> gravitySlider.setValue(toSliderValue(3.73)),
                DEFAULT_NARRATION);

        earthButton = new TextureComponentButton(leftPos + 110, topPos + 75, 20, 20, 14, 14,
                GUISprites.EARTH,
                Component.translatable("stellaris.screen.gravityManipulator.planet.earth").getString(),
                button -> gravitySlider.setValue(toSliderValue(9.81)),
                DEFAULT_NARRATION);

        addRenderableWidget(earthButton);
        addRenderableWidget(marsButton);
        addRenderableWidget(moonButton);
    }
}
