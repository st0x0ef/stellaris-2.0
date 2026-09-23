package org.exodusstudio.stellaris.common.compats.rei;

import dev.architectury.fluid.FluidStack;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.util.ClientEntryStacks;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import dev.architectury.fluid.FluidStackTemplate;
import net.minecraft.resources.Identifier;

public final class REIWidgets {

    private REIWidgets() {
    }

    public static Point origin(Rectangle bounds, int width, int height) {
        return new Point(bounds.getCenterX() - width / 2, bounds.getCenterY() - height / 2);
    }

    public static Widget background(Identifier texture, Point origin, int width, int height) {
        return Widgets.createTexturedWidget(texture, new Rectangle(origin.x, origin.y, width, height));
    }

    public static Slot slot(Point origin, int x, int y) {
        return Widgets.createSlot(new Point(origin.x + x, origin.y + y)).disableBackground();
    }

    public static Slot tank(Point origin, int x, int y, int width, int height) {
        return Widgets.createSlot(new Rectangle(origin.x + x - 1, origin.y + y - 1, width + 2, height + 2))
                .disableBackground();
    }

    public static EntryStack<FluidStack> fluid(FluidStackTemplate template, long capacity) {
        EntryStack<FluidStack> stack = EntryStacks.ofFluidHolder(template.fluid(), template.amount());
        return ClientEntryStacks.setFluidRenderRatio(stack, Math.min(1.0F, (float) template.amount() / capacity));
    }
}
