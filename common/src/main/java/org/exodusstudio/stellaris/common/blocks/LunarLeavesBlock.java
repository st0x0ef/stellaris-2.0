package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;

public class LunarLeavesBlock extends LeavesBlock {
    public LunarLeavesBlock(Properties properties) {
        super(0f, properties);
    }

    @Override
    public MapCodec<? extends LeavesBlock> codec() {
        return simpleCodec(LunarLeavesBlock::new);
    }

    @Override
    protected void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random) {

    }
}
