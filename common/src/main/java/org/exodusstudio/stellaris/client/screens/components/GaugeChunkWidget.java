package org.exodusstudio.stellaris.client.screens.components;

import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTexture;
import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.client.fluid.ClientFluidStackHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.exodusstudio.stellaris.client.registry.FluidInfosRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * A Widget that render a big fluid gauge on the screen
 */
//TODO: change the system to stop using FluidInfo
public class GaugeChunkWidget extends GaugeWidget {

    protected boolean spriteChanged;
    public TextureAtlasSprite sprite2;

    public int color;
    public FluidStack stack;
    protected int imageWidth;
    protected int imageHeight;

    public GaugeChunkWidget(int x, int y, int width, int height, FluidStack fluidStack, @Nullable Identifier overlay_sprite, long capacity, Direction4 direction) {
        this(x, y,width, height, FluidInfosRegistry.getFluidComponent(fluidStack.getFluid()), FluidInfosRegistry.getFluidTexture(fluidStack), overlay_sprite, capacity, direction);
        this.stack = fluidStack;
        spriteChanged = true;
    }

    public GaugeChunkWidget(int x, int y, int width, int height, Component message, Identifier sprite, @Nullable Identifier overlay_sprite, long capacity, Direction4 direction) {
        super(x, y, width, height, message, sprite, overlay_sprite, capacity, direction);
        spriteChanged = true;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        if(stack == null || stack.isEmpty()) return;

        if (spriteChanged) {

            try {
                this.sprite2 = ClientFluidStackHooks.getStillTexture(stack);
                this.color = ClientFluidStackHooks.getColor(stack);
                this.imageHeight = this.height;
                this.imageWidth = this.width / 2;
                spriteChanged = false;
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        if(sprite == null || this.imageWidth == 0 || this.imageHeight == 0) return;

        switch (DIRECTION) {
            case DOWN_UP -> {
                int i = Mth.ceil(getProgress(amount, capacity) * (getHeight() - 1));
                for (int j = 0; j < width / imageWidth; j++) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite2, getX() + imageWidth * j, getY() + getHeight() - i, imageWidth, i, this.color);
                    //guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, imageWidth, getHeight(), 0, getHeight() - i, getX() + imageWidth * j, getY() + getHeight() - i, imageWidth, i);
                }
                int x = width % imageWidth;
                if (x > 0) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite2, getX() + width - x, getY() + getHeight() - i, x, i, this.color);
                    //guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, getHeight(), 0, getHeight() - i, getX() + width - x, getY() + getHeight() - i, x, i);
                }
            }
            case UP_DOWN -> {
                int i = Mth.ceil(getProgress(amount, capacity) * (getHeight() - 1));
                for (int j = 0; j < width / imageWidth; j++) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite2, getX() + imageWidth * j, getY(), imageWidth, i, this.color);
                }
                int x = width % imageWidth;
                if (x > 0) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite2, getX() + width - x, getY(), x, i, this.color);
                }
            }
            case LEFT_RIGHT -> {
                int i = Mth.ceil(getProgress(amount, capacity) * (getWidth() - 1));
                for (int j = 0; j < height / imageHeight; j++) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite2, getX(), getY() + imageHeight * j, i, imageHeight, this.color);
                }
                int y = height % imageHeight;
                if (y > 0) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite2, getX(), getY() + height - y, i, y, this.color);
                }
            }
            case RIGHT_LEFT -> {
                int i = Mth.ceil(getProgress(amount, capacity) * (getWidth() - 1));
                for (int j = 0; j < height / imageHeight; j++) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite2, getX() + getWidth() - i, getY() + imageHeight * j, i, imageHeight, this.color);
                }
                int y = height % imageHeight;
                if (y > 0) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite2, getX() + getWidth() - i, getY() + height - y, i, y, this.color);
                }
            }
        }
        if (this.overlay_sprite != null) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, overlay_sprite, getX(), getY(), width, height);
        }
    }

    /**
     * Update the gauge amount from a fluid storage
     * Allow to sync fluid texture/component
     * @param storage
     * @param tank
     */
    public void updateAmount(UniversalFluidStorage storage, int tank) {
        this.updateAmount(storage.getFluidInTank(tank));
    }

    public void updateAmount(FluidStack stack) {
        this.updateAmount(stack.getAmount());
        this.setMessage(FluidInfosRegistry.getFluidComponent(stack.getFluid()));
        this.updateSprite(FluidInfosRegistry.getFluidTexture(stack));

        this.stack = stack;
        this.spriteChanged = true;
    }

    @Override
    public void updateSprite(Identifier sprite) {
        super.updateSprite(sprite);
        spriteChanged = true;
    }
}