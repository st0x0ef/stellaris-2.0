package org.exodusstudio.stellaris.common.blocks.entities.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;

/**
 * A cable is a bufferless connector: it stores no energy and exposes no capability. Transport is
 * handled entirely by {@link org.exodusstudio.stellaris.common.transport.TransportGraph}, which floods
 * the connected cable network when a producer pushes energy into it. This block entity therefore
 * exists only so the cable can be an entity block; it neither ticks nor persists anything.
 */
public class CableBlockEntity extends BlockEntity {

    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.CABLES.get(), pos, state);
    }
}
