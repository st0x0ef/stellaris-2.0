package org.exodusstudio.stellaris.common.utils.capabilities.energy;

import com.fej1fun.potentials.energy.BaseEnergyStorage;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class EnergyStorage extends BaseEnergyStorage {

    public EnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public EnergyStorage(int capacity) {
        super(capacity);
    }

    @Override
    public int insert(int amount, boolean simulate) {
        int inserted = super.insert(amount, simulate);
        if (!simulate) {
            onChange();
        }
        return inserted;
    }

    @Override
    public int extract(int amount, boolean simulate) {
        int extracted = super.extract(amount, simulate);
        if (!simulate) {
            onChange();
        }
        return extracted;
    }

    public void save(ValueOutput output, String name) {
        output.putInt("energy-" + name, energy);
    }

    public void load(ValueInput input, String name) {
        if (input != null && input.getInt("energy-" + name).isPresent()) {
            this.energy = input.getInt("energy-" + name).get();
        }

    }

    protected abstract void onChange();
}
