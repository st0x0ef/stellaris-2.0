package org.exodusstudio.stellaris.client.renderers.trophy;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import org.exodusstudio.stellaris.common.data.trophy.BossTrophy;

public class BossTrophyRenderState extends BlockEntityRenderState {
    public Direction facing;
    public BossTrophy boss;
}
