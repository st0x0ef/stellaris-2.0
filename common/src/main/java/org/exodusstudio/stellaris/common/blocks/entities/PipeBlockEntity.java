package org.exodusstudio.stellaris.common.blocks.entities;

import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.blocks.PipeBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.TickingBlockEntity;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
import org.exodusstudio.stellaris.common.fluid.SingleFluidStorage;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.jetbrains.annotations.Nullable;

public class PipeBlockEntity extends BlockEntity implements FluidProvider.BLOCK, TickingBlockEntity {

    private final SingleFluidStorage fluidTank;

    public PipeBlockEntity(BlockPos pos, BlockState blockState, long capacity, long maxIn, long maxOut) {
        super(BlockEntitiesRegistry.PIPE_ENTITY.get(), pos, blockState);
        this.fluidTank = new SingleFluidStorage(capacity, maxIn, maxOut) {
            @Override
            protected void onChange() {
                setChanged();
            }
        };
    }

    public static PipeBlockEntity create(BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof PipeBlock block) {
            return new PipeBlockEntity(pos, state, block.capacity, block.maxIn, block.maxOut);
        }
        return new PipeBlockEntity(pos, state, 0, 0, 0);
    }

    @Override
    public @Nullable UniversalFluidStorage getFluidTank(@Nullable Direction direction) {
        return fluidTank;
    }


    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        fluidTank.load(input, "base");
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        fluidTank.save(output, "base");
    }


    @Override
    public void tick(Level level, BlockState state) {
        FluidUtil.distributeFluidNearby(level, worldPosition, fluidTank.getFluidInTank(0));
    }
}
