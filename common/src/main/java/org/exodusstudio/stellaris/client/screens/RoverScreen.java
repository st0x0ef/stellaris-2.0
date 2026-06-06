package org.exodusstudio.stellaris.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.components.GaugeWidget;
import org.exodusstudio.stellaris.common.menus.RoverMenu;
import org.exodusstudio.stellaris.common.entities.vehicles.RoverEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;

@Environment(value = EnvType.CLIENT)
public class RoverScreen extends AbstractContainerScreen<RoverMenu> {

    private static final Identifier TEXTURE = IdentifierUtils.guiTexture("rover");

    private RoverEntity rover;
    private GaugeWidget fuelGauge;

    public RoverScreen(RoverMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
        this.rover = getMenu().getRover();

        imageWidth = 180;
        imageHeight = 188;

        titleLabelX = (180 - Minecraft.getInstance().font.width(title.getString())) / 2;
        titleLabelY = 2;
    }

    @Override
    protected void init() {
        super.init();

        if (rover == null) {
            return;
        }

        fuelGauge = new GaugeWidget(leftPos + 52, topPos + 30, 12, 46, Component.translatable("stellaris.screen.diesel"), rover.getRoverComponent().getFuelType().getFuelTexture(), GUISprites.FLUID_TANK_OVERLAY, rover.getRoverComponent().getTankCapacity(), GaugeWidget.Direction4.DOWN_UP);
        addRenderableWidget(fuelGauge);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);

        if (rover == null) {
            return;
        }

        fuelGauge.updateAmount(rover.getFuel());
        fuelGauge.updateSprite(rover.getRoverComponent().getFuelType().getFuelTexture());

        fuelGauge.renderTooltip(graphics, mouseX, mouseY, font);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 5726575, false);
    }
}
