package org.exodusstudio.stellaris.common.menus.base;

import net.minecraft.world.inventory.DataSlot;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyContainerBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.IntSupplier;

public final class EnergyUsageSync {

    private final DataSlot current;
    private final DataSlot max;

    private EnergyUsageSync(DataSlot current, DataSlot max) {
        this.current = current;
        this.max = max;
    }

    public static EnergyUsageSync create(@Nullable BaseEnergyContainerBlockEntity machine, Consumer<DataSlot> addDataSlot) {
        boolean server = machine != null && machine.getLevel() != null && !machine.getLevel().isClientSide();

        DataSlot current = server ? reading(machine::getCurrentEnergyUsage) : DataSlot.standalone();
        DataSlot max = server ? reading(machine::getMaxEnergyUsage) : DataSlot.standalone();
        addDataSlot.accept(current);
        addDataSlot.accept(max);
        return new EnergyUsageSync(current, max);
    }

    public int current() {
        return current.get();
    }

    public int max() {
        return max.get();
    }

    private static DataSlot reading(IntSupplier value) {
        return new DataSlot() {
            @Override
            public int get() {
                return value.getAsInt();
            }

            @Override
            public void set(int value) {
            }
        };
    }
}
