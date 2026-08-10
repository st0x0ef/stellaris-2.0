package org.exodusstudio.stellaris.client.renderers.space_farm;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.state.BlockState;

public class SpaceFarmRenderState extends BlockEntityRenderState {

    public BlockState cropState;
    public final BlockModelRenderState cropRenderState = new BlockModelRenderState();


}
