package org.exodusstudio.stellaris.client.screens.components;

import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import dev.architectury.hooks.fluid.FluidStackHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.exodusstudio.stellaris.client.screens.utils.GUIUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class FluidGaugeWidget extends AbstractWidget {

    private static final TextureAtlasSprite WATER_STILL_SPRITE = FluidStackHooks.getStillTexture(Fluids.WATER);

    private final int tank;
    protected final Supplier<? extends UniversalFluidStorage> fluidStorage;
    private Fluid currFluid = null;
    private int fluidColor = 1;

    protected TextureAtlasSprite sprite;
    protected @Nullable ResourceLocation overlaySprite;
    protected final GaugeWidget.Direction4 DIRECTION;

    protected int imageWidth;
    protected int imageHeight;


    public FluidGaugeWidget(int x, int y, int width, int height, Component message, Supplier<? extends UniversalFluidStorage> fluidStorage, int tank, GaugeWidget.Direction4 direction) {
        super(x, y, width, height, message);
        this.fluidStorage = fluidStorage;
        this.tank = tank;
        this.overlaySprite = null;

        this.DIRECTION = direction;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        UniversalFluidStorage fluids = fluidStorage.get();
        Fluid fluid = fluids.getFluidInTank(tank).getFluid();
        if (fluid != currFluid || currFluid == null) {
            currFluid = fluid;
            updateLiquid(currFluid);
        }

        long amount = fluids.getFluidInTank(tank).getAmount();
        long capacity = fluids.getTankCapacity(tank);

        //RenderSystem.setShaderColor((float)(fluidColor >> 16 & 255) / 255.0F, (float)(fluidColor >> 8 & 255) / 255.0F, (float)(fluidColor & 255) / 255.0F, (float)(fluidColor >> 24 & 255) / 255.0F);
        switch (DIRECTION) {
            case DOWN_UP -> {
                int progress = Mth.ceil(getProgress(amount, capacity) * (getHeight() - 1));
                for (int j = 0; j < width / imageWidth; j++) {

                    // Dessine un bloc complet du sprite de largeur imageWidth et hauteur progress, aligné en bas
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX() + imageWidth * j, imageWidth, getY() + getHeight() - progress, progress);
                }
                int x = width % imageWidth;
                if (x > 0) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX() + width - x, x, getY() + getHeight() - progress, progress);
                }
            }
            case UP_DOWN -> {
                int i = Mth.ceil(getProgress(amount, capacity) * (getHeight() - 1));
                for (int j = 0; j < width / imageWidth; j++) {
                    // Dessine un bloc complet du sprite de largeur imageWidth et hauteur i, aligné en haut
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX() + imageWidth * j, imageWidth, getY(), i);
                }
                int x = width % imageWidth;
                if (x > 0) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX() + width - x, x, getY(), i);
                }
            }
            case LEFT_RIGHT -> {
                int i = Mth.ceil(getProgress(amount, capacity) * (getWidth() - 1));
                for (int j = 0; j < height / imageHeight; j++) {
                    // Dessine un bloc complet du sprite de largeur i et hauteur imageHeight, aligné à gauche
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX(), i, getY() + imageHeight * j, imageHeight);
                }
                int y = height % imageHeight;
                if (y > 0) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX(), i, getY() + height - y, y);
                }
            }
            case RIGHT_LEFT -> {
                int i = Mth.ceil(getProgress(amount, capacity) * (getWidth() - 1));
                for (int j = 0; j < height / imageHeight; j++) {
                    // Dessine un bloc complet du sprite de largeur i et hauteur imageHeight, aligné à droite
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX() + getWidth() - i, i, getY() + imageHeight * j, imageHeight);
                }
                int y = height % imageHeight;
                if (y > 0) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX() + getWidth() - i, i, getY() + height - y, y);
                }
            }
        }
        if (this.overlaySprite != null) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, overlaySprite, getX(), getY(), width, height);
        }
    }


    public void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY, Font font) {
        this.renderTooltips(graphics, mouseX, mouseY, font, list -> {
        });
    }


    public void renderTooltips(GuiGraphics graphics, int mouseX, int mouseY, Font font, Consumer<List<ClientTooltipComponent>> components) {
        UniversalFluidStorage fluids = fluidStorage.get();
        long amount = fluids.getFluidInTank(tank).getAmount();
        long capacity = fluids.getTankCapacity(tank);

        String GaugeComponent = getMessage().getString() + " : " + amount + " / " + capacity;
        ClientTooltipComponent capacityComponent;

        if (amount >= capacity) {
            capacityComponent = GUIUtils.getMessageComponent(GaugeComponent, "Lime");
        }
        else if (amount <= 0) {
            capacityComponent = GUIUtils.getMessageComponent(GaugeComponent, "Red");
        }
        else {
            capacityComponent = GUIUtils.getMessageComponent(GaugeComponent, "Orange");
        }

        List<ClientTooltipComponent> components1 = new ArrayList<>();
        components.accept(components1);
        components1.addFirst(capacityComponent);
        if (mouseX >= this.getX() && mouseX <= this.getX() + width && mouseY >= this.getY() && mouseY <= this.getY() + this.height) {
            graphics.renderTooltip(font, components1, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }



    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    protected double getProgress(Long amount, Long capacity) {
        return Mth.clamp((double) amount / (double) capacity, 0.0D, 1.0D);
    }

    public FluidGaugeWidget setOverlaySprite(@Nullable ResourceLocation overlaySprite) {
        this.overlaySprite = overlaySprite;
        return this;
    }

    private void updateLiquid(Fluid fluid) {
        TextureAtlasSprite sprite = FluidStackHooks.getStillTexture(fluid);
        if (sprite == null || fluid == Fluids.WATER)
            sprite = WATER_STILL_SPRITE;
        fluidColor = FluidStackHooks.getColor(fluid);

        assert sprite != null;
        SpriteContents contents = sprite.contents();
        this.imageHeight = contents.height();
        this.imageWidth = contents.width();

        this.sprite = sprite;
    }

}