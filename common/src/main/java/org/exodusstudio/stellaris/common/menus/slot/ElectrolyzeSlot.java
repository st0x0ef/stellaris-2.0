package org.exodusstudio.stellaris.common.menus.slot;

import com.fej1fun.potentials.capabilities.Capabilities;
import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.blocks.entities.machines.ElectrolyzerBlockEntity;
import org.exodusstudio.stellaris.common.data.recipe.ElectrolyzeRecipeData;
import org.jetbrains.annotations.Nullable;

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
        UniversalFluidStorage fluidStorage = Capabilities.Fluid.ITEM.getCapability(stack);
        if (fluidStorage == null) {
            return false;
        }

        //If the tank is -1, we are doing the logic for the ingredient tank
        if(tank == -1) {

            //If the main tank is not empty we check if it's the same fluid
            if(!electrolyzerBlock.ingredientTank.isEmpty()) {
                return fluidStorage.isFluidValid(0, electrolyzerBlock.ingredientTank.getFluidInTank(0));
            }
            //Else, we just check if the fluid is in the recipe registry
            //TODO need to fix that we can use an other fluid when the ingredient tank is empty
            return ElectrolyzeRecipeData.RECIPES.containsKey(fluidStorage.getFluidInTank(0).getFluid());
        }
        if (electrolyzerBlock.ingredientTank.isEmpty()) return false;
        return electrolyzerBlock.resultTanks.getFluidInTank(tank).isFluidEqual(fluidStorage.getFluidInTank(0));

    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
