package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.entities.FlagBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.jetbrains.annotations.Nullable;


public class FlagBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    private static final Set<BlockPos> CLEANING_UP = new HashSet<>();

    static boolean isCleaningUp(BlockPos pos) {
        return CLEANING_UP.contains(pos);
    }

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    public FlagBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return BaseEntityBlock.simpleCodec(FlagBlock::new);
    }

    private static final VoxelShape FLAG_SHAPE = Shapes.or(
            // Rod
            Shapes.box(
                    6.5D / 16.0D, 0.0D, 6.5D / 16.0D,
                    9.5D / 16.0D, 3.0D, 9.5D / 16.0D
            ),

            // Bottom base
            Shapes.box(
                    3.0D / 16.0D, 0.0D, 3.0D / 16.0D,
                    13.0D / 16.0D, 2.0D / 16.0D, 13.0D / 16.0D
            )
    );

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return FLAG_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return FLAG_SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();

        Direction facing = context.getHorizontalDirection();
        if (blockpos.getY() < level.getMaxY() - 2
                && context.getLevel().getBlockState(blockpos.above()).canBeReplaced(context)
                && context.getLevel().getBlockState(blockpos.above(2)).canBeReplaced(context)
                && context.getLevel().getBlockState(blockpos.above(2).relative(facing.getClockWise())).canBeReplaced(context)) {
            boolean flag = context.getLevel().getFluidState(context.getClickedPos()).is(Fluids.WATER);
            return this.defaultBlockState().setValue(FACING, facing).setValue(WATERLOGGED, flag);
        }
        else {
            return null;
        }
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if(worldIn.isClientSide()) return;

        BlockEntity blockEntity = worldIn.getBlockEntity(pos);

        if(blockEntity instanceof FlagBlockEntity flagBlockEntity) {

            //Handle Player Head
            if(placer instanceof Player player) {
                flagBlockEntity.setProfile(ResolvableProfile.createResolved(player.getGameProfile()));
            }
            if(stack.has(DataComponents.BASE_COLOR)) {
                DyeColor dyeColor = stack.get(DataComponents.BASE_COLOR);
                flagBlockEntity.setDyeColor(dyeColor);
            }
            if(stack.has(DataComponents.PROFILE)) {
                ResolvableProfile profile  = stack.get(DataComponents.PROFILE);
                flagBlockEntity.setProfile(profile);
            }
        }
        super.setPlacedBy(worldIn, pos, state.setValue(WATERLOGGED, false), placer, stack);

        Direction facing = state.getValue(FACING);
        worldIn.setBlock(pos.above(), BlocksRegistry.FLAG_PROXY.get().defaultBlockState().setValue(FlagProxyBlock.PART, 1).setValue(FlagProxyBlock.FACING, facing), 3);
        worldIn.setBlock(pos.above(2), BlocksRegistry.FLAG_PROXY.get().defaultBlockState().setValue(FlagProxyBlock.PART, 2).setValue(FlagProxyBlock.FACING, facing), 3);
        worldIn.setBlock(pos.above(2).relative(facing.getClockWise()), BlocksRegistry.FLAG_PROXY.get().defaultBlockState().setValue(FlagProxyBlock.PART, 3).setValue(FlagProxyBlock.FACING, facing), 3);
    }

    public static InteractionResult handleInteraction(ItemStack stack, Level level, BlockPos mainPos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(mainPos);
        if (blockEntity instanceof FlagBlockEntity flagBlockEntity) {
            if (stack.getItem() instanceof DyeItem dyeItem) {
                flagBlockEntity.setDyeColor(dyeItem.components().get(DataComponents.DYE));
                BlockState mainState = level.getBlockState(mainPos);
                level.sendBlockUpdated(mainPos, mainState, mainState, 3);
                return InteractionResult.SUCCESS;
            }

            if (stack.has(DataComponents.PROFILE)) {
                Stellaris.LOG.error("Setting profile from itemstack");
                ResolvableProfile resolvableprofile = stack.get(DataComponents.PROFILE);
                flagBlockEntity.setProfile(resolvableprofile);
                BlockState mainState = level.getBlockState(mainPos);
                level.sendBlockUpdated(mainPos, mainState, mainState, 3);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        InteractionResult result = handleInteraction(stack, level, pos, player, hand, hitResult);
        if (result != InteractionResult.PASS) {
            return result;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FlagBlockEntity(pos, state);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        state = state.setValue(WATERLOGGED, false);
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        BlockPos immutable = pos.immutable();
        CLEANING_UP.add(immutable);
        try {
            for (int offset = 1; offset <= 2; offset++) {
                BlockPos proxyPos = pos.above(offset);
                BlockState proxyState = level.getBlockState(proxyPos);
                if (proxyState.getBlock() instanceof FlagProxyBlock
                        && FlagProxyBlock.getMainPos(proxyPos, proxyState).equals(pos)) {
                    level.removeBlock(proxyPos, false);
                }
            }
            Direction facing = state.getValue(FACING);
            BlockPos lateralProxyPos = pos.above(2).relative(facing.getClockWise());
            BlockState lateralState = level.getBlockState(lateralProxyPos);
            if (lateralState.getBlock() instanceof FlagProxyBlock
                    && FlagProxyBlock.getMainPos(lateralProxyPos, lateralState).equals(pos)) {
                level.removeBlock(lateralProxyPos, false);
            }
        } finally {
            CLEANING_UP.remove(immutable);
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    // Basically the Events file handles the drop if player is shifting, this handles it if it's normal
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        Entity entity = params.getOptionalParameter(LootContextParams.THIS_ENTITY);

        if (entity instanceof Player player && player.isCrouching() && !player.getAbilities().instabuild) {
            return Collections.emptyList();
        }

        return super.getDrops(state, params);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING, WATERLOGGED);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE; // we only want to render our custom model
    }
}
