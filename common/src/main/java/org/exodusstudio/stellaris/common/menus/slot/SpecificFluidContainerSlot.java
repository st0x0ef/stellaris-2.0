package org.exodusstudio.stellaris.common.menus.slot;

import com.fej1fun.potentials.capabilities.Capabilities;
import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import dev.architectury.fluid.FluidStack;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Predicate;

public class SpecificFluidContainerSlot extends Slot {

    private final boolean allowsEmpty;
    private final Predicate<Fluid> fluid;

    public SpecificFluidContainerSlot(Container container, Fluid fluid, int slot, int x, int y, boolean allowsEmpty) {
        this(container, matching(fluid), slot, x, y, allowsEmpty);
    }

    public SpecificFluidContainerSlot(Container container, TagKey<Fluid> tag, int slot, int x, int y, boolean allowsEmpty) {
        this(container, matching(tag), slot, x, y, allowsEmpty);
    }

    public SpecificFluidContainerSlot(Container container, Predicate<Fluid> fluid, int slot, int x, int y, boolean allowsEmpty) {
        super(container, slot, x, y);
        this.allowsEmpty = allowsEmpty;
        this.fluid = fluid;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return accepts(stack, fluid, allowsEmpty);
    }

    public static boolean accepts(ItemStack stack, Fluid fluid, boolean allowsEmpty) {
        return accepts(stack, matching(fluid), allowsEmpty);
    }

    public static boolean accepts(ItemStack stack, TagKey<Fluid> tag, boolean allowsEmpty) {
        return accepts(stack, matching(tag), allowsEmpty);
    }

    public static boolean accepts(ItemStack stack, Predicate<Fluid> fluid, boolean allowsEmpty) {
        UniversalFluidStorage fluidStorage = Capabilities.Fluid.ITEM.getCapability(stack);
        if (fluidStorage == null) {
            return false;
        }

        if (allowsEmpty) {
            for (FluidStack fluidStack : fluidStorage) {
                if (fluidStack.isEmpty()) {
                    return true;
                }
            }
        }

        for (FluidStack fluidStack : fluidStorage) {
            if (fluid.test(fluidStack.getFluid())) {
                return true;
            }
        }

        return false;
    }

    private static Predicate<Fluid> matching(Fluid fluid) {
        return other -> other.isSame(fluid);
    }

    private static Predicate<Fluid> matching(TagKey<Fluid> tag) {
        return other -> other.defaultFluidState().is(tag);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
