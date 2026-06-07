package org.exodusstudio.stellaris.client.screens;

import dev.architectury.fluid.FluidStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.exodusstudio.stellaris.client.screens.components.GaugeChunkWidget;
import org.exodusstudio.stellaris.client.screens.components.GaugeWidget;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;
import org.exodusstudio.stellaris.common.entities.vehicles.RocketEntity;
import org.exodusstudio.stellaris.common.menus.RocketMenu;
import org.exodusstudio.stellaris.common.registries.ModulesRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.List;


public class RocketScreen extends AbstractContainerScreen<RocketMenu> {

    private static final Identifier SMALL_INVENTORY_TEXTURE = IdentifierUtils.guiTexture("rocket_small_inventory");
    private static final Identifier FULL_INVENTORY_TEXTURE = IdentifierUtils.guiTexture("rocket_full_inventory");

    private GaugeChunkWidget fuelGauge;
    private FluidStack fuel;

    public RocketScreen(RocketMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);

        imageWidth = 180;
        imageHeight = 224;

        titleLabelX = (180 - Minecraft.getInstance().font.width(title.getString())) / 2;
        titleLabelY = 2;
    }

    @Override
    protected void init() {
        super.init();

        if (this.getRocket() == null) {
            return;
        }

        this.fuel = getRocket().getFuelType();
        this.fuelGauge = new GaugeChunkWidget(leftPos + 100, topPos + 20, 12, 46, fuel, GUISprites.FLUID_TANK_OVERLAY, 3000, GaugeWidget.Direction4.DOWN_UP);
        addRenderableWidget(fuelGauge);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);

        //We're only updating the fuel amount since the user cannot change the fuel type while the GUI is open
        if(this.fuelGauge != null) fuelGauge.updateAmount(getRocket().getFuelType());
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        if (getRocket() != null) {
            Identifier texture = getRocket().getRocketModules().contains(ModulesRegistry.CARGO.get()) ? FULL_INVENTORY_TEXTURE : SMALL_INVENTORY_TEXTURE;
            graphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.leftPos, this.topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);

        if(this.fuelGauge != null) fuelGauge.renderTooltips(guiGraphics, x, y, this.font, List::of);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, -11050641, false);
    }

    public RocketEntity getRocket() {
        return menu.getRocket();
    }
}
