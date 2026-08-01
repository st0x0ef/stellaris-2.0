package org.exodusstudio.stellaris.common.networks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.exodusstudio.stellaris.common.blocks.cables.ConnectionMode;

public record NetworkEndpoint(
        BlockPos cablePos,
        BlockPos targetPos,
        Direction direction,
        ConnectionMode mode
) {
    public boolean isPull() {
        return mode == ConnectionMode.PULL;
    }

    public boolean isPush() {
        return mode == ConnectionMode.PUSH;
    }
}