package org.exodusstudio.stellaris.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.components.GaugeWidget;
import org.exodusstudio.stellaris.client.screens.components.TexturedButton;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.blocks.entities.machines.BlenderBlockEntity;
import org.exodusstudio.stellaris.common.menus.BlenderMenu;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class BlenderScreen extends AbstractContainerScreen<BlenderMenu> {
    public static final Identifier texture = IdentifierUtils.guiTexture("blender");

    private static final int STATUS_X = 91;
    private static final int STATUS_Y = 65;
    private static final int STATUS_WIDTH = 7;
    private static final int STATUS_HEIGHT = 2;
    private static final int STATUS_COLOR = 0xFFFFFFFF;

    private static final int BLEND_BUTTON_X = 104;
    private static final int BLEND_BUTTON_Y = 83;
    private static final int BLEND_BUTTON_WIDTH = 60;
    private static final int BLEND_BUTTON_HEIGHT = 16;
    private static final int BLEND_BUTTON_TINT = 0xFFFFFFFF;
    private static final int BLEND_BUTTON_TINT_DISABLED = 0xFF888888;

    private final BlenderBlockEntity blockEntity = getMenu().getBlockEntity();
    private GaugeWidget energyGauge;
    private TexturedButton blendButton;

    public BlenderScreen(BlenderMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component, 180, 188);

        this.inventoryLabelY = this.imageHeight - 95;

        titleLabelX = (180 - Minecraft.getInstance().font.width(title.getString())) / 2;
        titleLabelY = 2;
    }

    @Override
    protected void init() {
        super.init();

        blendButton = new TexturedButton(leftPos + BLEND_BUTTON_X, topPos + BLEND_BUTTON_Y,
                BLEND_BUTTON_WIDTH, BLEND_BUTTON_HEIGHT,
                Component.translatable("stellaris.screen.blender.blend"), button -> requestBlend())
                .tex(GUISprites.MACHINE_BUTTON, GUISprites.MACHINE_BUTTON_HOVER)
                .tooltip(Tooltip.create(Component.translatable("stellaris.screen.blender.blend.tooltip")))
                .useSprite(true);
        addRenderableWidget(blendButton);

        if (blockEntity == null) {
            return;
        }

        energyGauge = new GaugeWidget(leftPos + 68, topPos + 20, 44, 6, Component.translatable("stellaris.screen.energyContainer"),
                GUISprites.SIDEWAYS_ENERGY_FULL, null, blockEntity.getEnergy(null).getMaxEnergy(), GaugeWidget.Direction4.LEFT_RIGHT);
        addRenderableWidget(energyGauge);
    }

    private void requestBlend() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, BlenderMenu.BLEND_BUTTON);
        }
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        this.extractBackground(graphics, mouseX, mouseY, partialTicks);
        super.extractContents(graphics, mouseX, mouseY, partialTicks);
        this.extractTooltip(graphics, mouseX, mouseY);

        blendButton.active = !menu.isBlending() && menu.canBlend();
        blendButton.setColor(blendButton.active ? BLEND_BUTTON_TINT : BLEND_BUTTON_TINT_DISABLED);

        if (blockEntity == null) {
            return;
        }

        energyGauge.updateAmount(blockEntity.getEnergy(null).getEnergy());

        if (menu.isBlending()) {
            int filled = Math.max(1, Mth.ceil(menu.getBlendProgress() * STATUS_WIDTH));
            graphics.fill(leftPos + STATUS_X, topPos + STATUS_Y, leftPos + STATUS_X + filled,
                    topPos + STATUS_Y + STATUS_HEIGHT, STATUS_COLOR);
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

        if (energyGauge != null) {
            energyGauge.renderTooltips(guiGraphics, x, y, font);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -11050641, false);
    }
}
