package org.exodusstudio.stellaris.client.renderers.gravity_manipulator;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class GravityManipulatorRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public boolean active;
    public float coreYRot;
    public float coreY = GravityManipulatorModel.CORE_BASE_Y;
}
