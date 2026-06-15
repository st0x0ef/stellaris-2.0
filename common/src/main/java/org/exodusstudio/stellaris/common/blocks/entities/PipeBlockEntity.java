package org.exodusstudio.stellaris.common.blocks.entities;

import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.blocks.PipeBlock;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.transport.PassthroughFluidStorage;
import org.jetbrains.annotations.Nullable;

/**
 * A fluid pipe is a bufferless connector: it stores no fluid. Transport between Stellaris machines is
 * handled by {@link org.exodusstudio.stellaris.common.transport.TransportGraph}, which floods the
 * connected pipe network when a producer pushes into it. The pipe additionally exposes a stateless
 * passthrough fluid capability ({@link PassthroughFluidStorage}) so other mods' pipes/conduits can
 * push directly into a Stellaris line; that capability routes straight into the network and stores
 * nothing. The block entity itself neither ticks nor persists anything.
 */
public class PipeBlockEntity extends BlockEntity implements FluidProvider.BLOCK {

    public PipeBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.PIPE_ENTITY.get(), pos, state);
    }

    public static PipeBlockEntity create(BlockPos pos, BlockState state) {
        return new PipeBlockEntity(pos, state);
    }

    @Override
    public @Nullable UniversalFluidStorage getFluidTank(@Nullable Direction direction) {
        if (level == null || !(getBlockState().getBlock() instanceof PipeBlock pipe)) {
            return null;
        }
        return new PassthroughFluidStorage(level, worldPosition, direction, pipe.maxIn);
    }
}
