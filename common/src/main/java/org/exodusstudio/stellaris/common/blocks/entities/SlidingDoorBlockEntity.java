package org.exodusstudio.stellaris.common.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;

public class SlidingDoorBlockEntity extends BlockEntity {
    public static final int SLIDE_TICKS = 12;

    private int progress;
    private int progressO;
    private boolean initialized;

    public SlidingDoorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.SLIDING_DOOR.get(), pos, state);
    }

    public void tick(BlockState state) {
        int target = state.getValue(DoorBlock.OPEN) ? SLIDE_TICKS : 0;
        if (!initialized) {
            progress = target;
            initialized = true;
        }
        progressO = progress;
        if (progress < target) progress++;
        else if (progress > target) progress--;
    }

    public int getProgress() {
        if (!initialized) {
            return getBlockState().getValue(DoorBlock.OPEN) ? SLIDE_TICKS : 0;
        }
        return progress;
    }

    public float getOpenness(float partialTick) {
        float t = initialized ? Mth.lerp(partialTick, progressO, progress) / SLIDE_TICKS : (float) getProgress() / SLIDE_TICKS;
        return ease(t);
    }

    public static float ease(float t) {
        return t * t * (3.0F - 2.0F * t);
    }
}
