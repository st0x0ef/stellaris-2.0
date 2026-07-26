package org.exodusstudio.stellaris.client.renderers.globe;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;

public class GlobeBlockRenderState extends BlockEntityRenderState {
    public float     yaw;
    public Direction facing;
    public SpriteId  material;
}
