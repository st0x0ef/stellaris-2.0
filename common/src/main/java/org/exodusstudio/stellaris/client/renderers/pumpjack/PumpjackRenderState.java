package org.exodusstudio.stellaris.client.renderers.pumpjack;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class PumpjackRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public long animationMillis;
}
