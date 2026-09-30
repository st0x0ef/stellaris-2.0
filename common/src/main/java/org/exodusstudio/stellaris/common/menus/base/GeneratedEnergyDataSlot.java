package org.exodusstudio.stellaris.common.menus.base;

import net.minecraft.world.inventory.DataSlot;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseGeneratorBlockEntity;
import org.jetbrains.annotations.Nullable;

public class GeneratedEnergyDataSlot extends DataSlot {

    @Nullable
    private final BaseGeneratorBlockEntity generator;
    private int value;

    private GeneratedEnergyDataSlot(@Nullable BaseGeneratorBlockEntity generator) {
        this.generator = generator;
    }

    public static GeneratedEnergyDataSlot server(BaseGeneratorBlockEntity generator) {
        return new GeneratedEnergyDataSlot(generator);
    }

    public static GeneratedEnergyDataSlot client() {
        return new GeneratedEnergyDataSlot(null);
    }

    @Override
    public int get() {
        return generator != null ? generator.getGeneratedLastTick() : value;
    }

    @Override
    public void set(int value) {
        this.value = value;
    }
}
