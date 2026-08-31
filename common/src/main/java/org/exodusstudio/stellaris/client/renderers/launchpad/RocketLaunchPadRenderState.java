package org.exodusstudio.stellaris.client.renderers.launchpad;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class RocketLaunchPadRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public boolean towers;
    public boolean antenna;
    public float barAngle;
}
