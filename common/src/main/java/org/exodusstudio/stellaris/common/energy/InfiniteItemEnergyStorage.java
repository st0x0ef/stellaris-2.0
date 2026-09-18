package org.exodusstudio.stellaris.common.energy;

import com.fej1fun.potentials.energy.ItemEnergyStorage;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public class InfiniteItemEnergyStorage extends ItemEnergyStorage {
    public InfiniteItemEnergyStorage(ItemStack stack, DataComponentType<Integer> component, int capacity, int maxReceive, int maxExtract) {
        super(stack, component, capacity, maxReceive, maxExtract);
    }

    @Override
    public int getEnergy() {
        return capacity;
    }

    @Override
    public void setEnergyStored(int energy) {
    }
}
