package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.exodusstudio.stellaris.common.blocks.base.BaseMachineBlock;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;

public class RocketStationBlock extends BaseMachineBlock {

    public static final MapCodec<RocketStationBlock> CODEC = simpleCodec(RocketStationBlock::new);
    private static final VoxelShape SHAPE = Block.box(0,0,0,16,12,16);

    public RocketStationBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(RocketStationBlock::new);
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return BlockEntitiesRegistry.ROCKET_STATION.get();
    }

    @Override
    public boolean hasTicker(Level level) {
        return !level.isClientSide;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
