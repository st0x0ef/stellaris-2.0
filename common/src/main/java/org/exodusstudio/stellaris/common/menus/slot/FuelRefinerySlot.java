package org.exodusstudio.stellaris.common.menus.slot;

import com.fej1fun.potentials.capabilities.Capabilities;
import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.blocks.entities.machines.FuelRefineryBlockEntity;

public class FuelRefinerySlot extends Slot {

    private final int tank;
    private final FuelRefineryBlockEntity fr;

    public FuelRefinerySlot(Container container, int slot, int x, int y, int tank, FuelRefineryBlockEntity fr) {
        super(container, slot, x, y);
        this.tank = tank;
        this.fr = fr;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        UniversalFluidStorage fluidStorage = Capabilities.Fluid.ITEM.getCapability(stack);
        return true;
//        if (fluidStorage == null) {
//            return false;
//        }
//
//        //If the tank is -1, we are doing the logic for the ingredient tank
//        if(tank == -1) {
//            //If the main tank is not empty we check if it's the same fluid
//            if(!fr.getIngredientTank().isEmpty()) {
//                return fluidStorage.getFluidInTank(0).isFluidEqual(fr.getIngredientTank().getFluidInTank(0));
//            }
//
//            return fr.getIngredientTank().isFluidValid(0, fluidStorage.getFluidInTank(0));
//        }
//
//        return tank == 0 ?
//                fluidStorage.isFluidValid(0, fr.getOutputFuelTank().getFluidInTank(0)) :
//                fluidStorage.isFluidValid(0, fr.getOutputDieselTank().getFluidInTank(0));

    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

}
