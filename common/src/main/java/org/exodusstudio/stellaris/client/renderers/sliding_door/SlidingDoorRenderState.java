package org.exodusstudio.stellaris.client.renderers.sliding_door;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;

public class SlidingDoorRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public boolean slidesLeft;
    public float openness;
    public SpriteId sprite;
}
