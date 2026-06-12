package org.exodusstudio.stellaris.common.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;

/**
 * A fluid pipe is a bufferless connector: it stores no fluid and exposes no capability. Transport is
 * handled entirely by {@link org.exodusstudio.stellaris.common.transport.TransportGraph}, which floods
 * the connected pipe network when a producer pushes fluid into it. This block entity therefore exists
 * only so the pipe can be an entity block; it neither ticks nor persists anything.
 */
public class PipeBlockEntity extends BlockEntity {

    public PipeBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.PIPE_ENTITY.get(), pos, state);
    }

    public static PipeBlockEntity create(BlockPos pos, BlockState state) {
        return new PipeBlockEntity(pos, state);
    }
}
