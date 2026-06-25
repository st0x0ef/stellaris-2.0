package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;

public class LunarVinesPlantBlock extends GrowingPlantBodyBlock {
    public static final MapCodec<LunarVinesPlantBlock> CODEC = simpleCodec(LunarVinesPlantBlock::new);
    private static final VoxelShape SHAPE = Block.column((double)8.0F, (double)0.0F, (double)16.0F);

    public MapCodec<LunarVinesPlantBlock> codec() {
        return CODEC;
    }

    public LunarVinesPlantBlock(final BlockBehaviour.Properties properties) {
        super(properties, Direction.UP, SHAPE, false);
    }

    protected GrowingPlantHeadBlock getHeadBlock() {
        return (GrowingPlantHeadBlock) BlocksRegistry.LUNAR_VINES.block().get();
    }
}
