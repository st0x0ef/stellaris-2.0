package org.exodusstudio.stellaris.client.screens;

import dev.architectury.fluid.FluidStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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


public class RocketScreen extends AbstractContainerScreen<RocketMenu> {

    private static final Identifier SMALL_INVENTORY_TEXTURE = IdentifierUtils.guiTexture("rocket_small_inventory");
    private static final Identifier FULL_INVENTORY_TEXTURE = IdentifierUtils.guiTexture("rocket_full_inventory");

    private GaugeChunkWidget fuelGauge;
    private FluidStack fuel;

    public RocketScreen(RocketMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component, 180, 224);

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
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        extractBackground(graphics, mouseX, mouseY, partialTicks);
        super.extractContents(graphics, mouseX, mouseY, partialTicks);
        extractTooltip(graphics, mouseX, mouseY);

        //We're only updating the fuel amount since the user cannot change the fuel type while the GUI is open
        if(this.fuelGauge != null) fuelGauge.updateAmount(getRocket().getFuelType());
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        if (getRocket() != null) {
            Identifier texture = getRocket().getRocketModules().contains(ModulesRegistry.CARGO.get()) ? FULL_INVENTORY_TEXTURE : SMALL_INVENTORY_TEXTURE;
            graphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.leftPos, this.topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int x, int y) {
        super.extractTooltip(guiGraphics, x, y);

        if(this.fuelGauge != null) fuelGauge.renderTooltips(guiGraphics, x, y, this.font);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -11050641, false);
    }

    public RocketEntity getRocket() {
        return menu.getRocket();
    }
}
