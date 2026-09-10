package org.exodusstudio.stellaris.common.menus.slot;

import com.fej1fun.potentials.fluid.UniversalFluidItemStorage;
import dev.architectury.fluid.FluidStack;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.entities.vehicles.base.FuelledVehicle;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
import org.exodusstudio.stellaris.common.fluid.VehicleFuelStorage;
import org.jetbrains.annotations.Nullable;

public class VehicleFuelSlot extends Slot {

    private final @Nullable FuelledVehicle vehicle;

    public VehicleFuelSlot(Container container, @Nullable FuelledVehicle vehicle, int index, int x, int y) {
        super(container, index, x, y);
        this.vehicle = vehicle;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (vehicle == null) {
            return false;
        }

        VehicleFuelStorage tank = vehicle.getFuelTank();
        if (tank == null) {
            return false;
        }

        UniversalFluidItemStorage storage = FluidUtil.getItemFluidStorage(stack);
        if (storage == null) {
            return false;
        }

        for (FluidStack fluidStack : storage) {
            if (!fluidStack.isEmpty() && tank.isFluidValid(0, fluidStack)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
