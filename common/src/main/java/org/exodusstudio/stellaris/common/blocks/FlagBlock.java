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
import java.util.List;

import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import org.jetbrains.annotations.Nullable;


public class FlagBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

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

        if (blockpos.getY() < level.getMaxY() - 1 && context.getLevel().getBlockState(blockpos.above()).canBeReplaced(context)) {
            boolean flag = context.getLevel().getFluidState(context.getClickedPos()).is(Fluids.WATER);
            return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(WATERLOGGED, flag);
        }
        else {
            return null;
        }
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if(worldIn.isClientSide()) return;


        BlockEntity blockEntity = worldIn.getBlockEntity(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()));

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
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof FlagBlockEntity flagBlockEntity) {
            if(stack.getItem() instanceof DyeItem dyeItem) {
                flagBlockEntity.setDyeColor(dyeItem.components().get(DataComponents.DYE));
                level.sendBlockUpdated(pos, state, state, 3);
                return InteractionResult.SUCCESS;
            }

            if(stack.has(DataComponents.PROFILE)) {
                Stellaris.LOG.error("Setting profile from itemstack");
                ResolvableProfile resolvableprofile = stack.get(DataComponents.PROFILE);
                flagBlockEntity.setProfile(resolvableprofile);
                level.sendBlockUpdated(pos, state, state, 3);
                return InteractionResult.SUCCESS;
            }
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
