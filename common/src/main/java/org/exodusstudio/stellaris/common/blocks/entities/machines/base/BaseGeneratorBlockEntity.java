package org.exodusstudio.stellaris.common.blocks.entities.machines.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.utils.capabilities.energy.EnergyUtil;

/**
 * Base class for generator block entities that produce energy over time.
 */
public abstract class BaseGeneratorBlockEntity extends BaseEnergyContainerBlockEntity {

    protected int energyGeneratedPT;
    protected final int maxCapacity;
    private int generatedLastTick;

    public BaseGeneratorBlockEntity(BlockEntityType<?> entityType, BlockPos blockPos, BlockState blockState, int energyGeneratedPT, int maxCapacity) {
        super(entityType, blockPos, blockState, maxCapacity, 0, maxCapacity);
        this.energyGeneratedPT = energyGeneratedPT;
        this.maxCapacity = maxCapacity;
    }

    public int getEnergyGeneratedPT() {
        return energyGeneratedPT;
    }

    public int getGeneratedLastTick() {
        return generatedLastTick;
    }

    protected void generate(boolean active) {
        generatedLastTick = active ? energyContainer.insertWithoutLimits(energyGeneratedPT, false) : 0;
    }

    @Override
    public void setChanged() {
        if (this.level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 1);
            super.setChanged();
        }
    }

    public abstract boolean canGenerate();

    @Override
    public void tick(Level level, BlockState state) {
        generate(canGenerate());
        EnergyUtil.distributeEnergyNearby(level, worldPosition, maxCapacity);
    }
}
