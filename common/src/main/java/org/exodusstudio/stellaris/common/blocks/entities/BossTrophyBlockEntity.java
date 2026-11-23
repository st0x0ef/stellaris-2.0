package org.exodusstudio.stellaris.common.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;

public class BossTrophyBlockEntity extends BlockEntity {

    public static final int ANIMATE_EVENT = 1;

    private long animationStart = -1;

    public BossTrophyBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.BOSS_TROPHY.get(), pos, state);
    }

    @Override
    public boolean triggerEvent(int id, int param) {
        if (id == ANIMATE_EVENT) {
            if (this.level != null) {
                this.animationStart = this.level.getGameTime();
            }
            return true;
        }
        return super.triggerEvent(id, param);
    }

    public float getAnimationTicks(float partialTicks) {
        if (this.animationStart < 0 || this.level == null) {
            return -1.0F;
        }
        return this.level.getGameTime() - this.animationStart + partialTicks;
    }
}
