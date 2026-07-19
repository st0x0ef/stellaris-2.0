package org.exodusstudio.stellaris.common.menus.slot;

import com.fej1fun.potentials.capabilities.Capabilities;
import com.fej1fun.potentials.fluid.UniversalFluidItemStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import dev.architectury.fluid.FluidStack;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.blocks.entities.machines.ElectrolyzerBlockEntity;

public class ElectrolyzeSlot extends Slot {

    private final ElectrolyzerBlockEntity electrolyzerBlock;
    private final int tank;

    public ElectrolyzeSlot(Container container, int slot, int x, int y, ElectrolyzerBlockEntity electrolyzerBlock, int tank) {
        super(container, slot, x, y);
        this.electrolyzerBlock = electrolyzerBlock;
        this.tank = tank;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        //If the tank is -1, we are doing the logic for the ingredient tank
        if (tank == -1) {
            UniversalFluidItemStorage fluidStorage = Capabilities.Fluid.ITEM.getCapability(stack);
            if (fluidStorage == null) {
                return false;
            }

            //If the main tank is not empty we check if it's the same fluid
            if (!electrolyzerBlock.ingredientTank.isEmpty()) {
                return fluidStorage.getFluidInTank(0).isFluidEqual(electrolyzerBlock.ingredientTank.getFluidInTank(0));
            }

            return electrolyzerBlock.ingredientTank.isFluidValid(0, fluidStorage.getFluidInTank(0));
        }

        if (!(stack.getItem() instanceof FluidProvider.ITEM provider)) {
            return false;
        }
        UniversalFluidItemStorage fluidStorage = provider.getFluidTank(stack);
        if (fluidStorage == null) {
            return false;
        }

        FluidStack tankFluid = electrolyzerBlock.resultTanks.getFluidInTank(tank);

        if (tankFluid.isEmpty()) {
            return true;
        }

        return fluidStorage.isFluidValid(0, tankFluid);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
