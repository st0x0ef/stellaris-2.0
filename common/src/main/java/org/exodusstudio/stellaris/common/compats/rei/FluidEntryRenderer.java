package org.exodusstudio.stellaris.common.compats.rei;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.client.fluid.ClientFluidStackHooks;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.client.gui.compat.GuiGraphics;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;

public record FluidEntryRenderer(EntryRenderer<FluidStack> delegate) implements EntryRenderer<FluidStack> {

    @Override
    public void render(EntryStack<FluidStack> entry, GuiGraphics graphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
        FluidStack stack = entry.getValue();

        if (stack.isEmpty()) {
            return;
        }

        TextureAtlasSprite sprite = ClientFluidStackHooks.getStillTexture(stack);

        if (sprite == null) {
            return;
        }

        Float ratio = entry.get(EntryStack.Settings.FLUID_RENDER_RATIO);
        int filled = Mth.ceil(bounds.height * Mth.clamp(ratio == null ? 1.0F : ratio, 0.0F, 1.0F));

        if (filled <= 0) {
            return;
        }

        int color = ClientFluidStackHooks.getColor(stack);

        if ((color & 0xFF000000) == 0) {
            color |= 0xFF000000;
        }

        graphics.enableScissor(bounds.getMinX(), bounds.getMaxY() - filled, bounds.getMaxX(), bounds.getMaxY());
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, bounds.x, bounds.y, bounds.width, bounds.height, color);
        graphics.disableScissor();
    }

    @Override
    public Tooltip getTooltip(EntryStack<FluidStack> entry, TooltipContext context) {
        return delegate.getTooltip(entry, context);
    }
}
