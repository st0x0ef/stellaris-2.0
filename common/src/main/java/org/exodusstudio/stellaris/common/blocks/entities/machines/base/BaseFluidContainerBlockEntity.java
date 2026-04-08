package org.exodusstudio.stellaris.common.blocks.entities.machines.base;

import com.fej1fun.potentials.providers.FluidProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.fluid.SingleFluidStorage;

/**
 * Base class for block entities that have an energy storage and an inventory.
 */
public abstract class BaseFluidContainerBlockEntity extends BaseEnergyContainerBlockEntity implements FluidProvider.BLOCK {

    public final SingleFluidStorage resultTank;



    public BaseFluidContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int initialMaxCapacity, SingleFluidStorage resultTank) {
        super(type, pos, state, initialMaxCapacity);
        this.resultTank = resultTank;
    }



}