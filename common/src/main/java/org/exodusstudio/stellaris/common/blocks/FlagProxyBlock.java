package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class FlagProxyBlock extends Block implements MultiblockProxyBlock {

    public static final IntegerProperty PART = IntegerProperty.create("part", 1, 3);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    private static final VoxelShape ROD_SHAPE = Block.box(6.5D, 0.0D, 6.5D, 9.5D, 16.0D, 9.5D);

    // Part 3 shapes (flag cloth block, adjacent to pole top)
    private static final VoxelShape FLAG_CLOTH_NORTH = Block.box( 0, 2, 7, 13, 12,  9);
    private static final VoxelShape FLAG_CLOTH_EAST  = Block.box( 7, 2, 0,  9, 12, 13);
    private static final VoxelShape FLAG_CLOTH_SOUTH = Block.box( 3, 2, 7, 16, 12,  9);
    private static final VoxelShape FLAG_CLOTH_WEST  = Block.box( 7, 2, 3,  9, 12, 16);

    // Part 2 shapes (rod + cloth portion that spills into this block)
    private static final VoxelShape PART2_NORTH = Shapes.or(ROD_SHAPE, Block.box( 9, 2, 7, 16, 12,  9));
    private static final VoxelShape PART2_EAST  = Shapes.or(ROD_SHAPE, Block.box( 7, 2, 9,  9, 12, 16));
    private static final VoxelShape PART2_SOUTH = Shapes.or(ROD_SHAPE, Block.box( 0, 2, 7,  7, 12,  9));
    private static final VoxelShape PART2_WEST  = Shapes.or(ROD_SHAPE, Block.box( 7, 2, 0,  9, 12,  7));

    private static VoxelShape getClothShape(BlockState state) {
        return switch (state.getValue(FACING)) {
            case EAST  -> FLAG_CLOTH_EAST;
            case SOUTH -> FLAG_CLOTH_SOUTH;
            case WEST  -> FLAG_CLOTH_WEST;
            default    -> FLAG_CLOTH_NORTH;
        };
    }

    private static VoxelShape getPart2Shape(BlockState state) {
        return switch (state.getValue(FACING)) {
            case EAST  -> PART2_EAST;
            case SOUTH -> PART2_SOUTH;
            case WEST  -> PART2_WEST;
            default    -> PART2_NORTH;
        };
    }

    public FlagProxyBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(PART, 1).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull MapCodec<? extends Block> codec() {
        return simpleCodec(FlagProxyBlock::new);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(PART)) {
            case 3 -> getClothShape(state);
            case 2 -> getPart2Shape(state);
            default -> ROD_SHAPE;
        };
    }

    @Override
    protected @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(PART)) {
            case 3 -> getClothShape(state);
            case 2 -> getPart2Shape(state);
            default -> ROD_SHAPE;
        };
    }

    @Override
    protected @NotNull VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected @NotNull VoxelShape getOcclusionShape(BlockState state) {
        return Shapes.empty();
    }

    public static BlockPos getMainPos(BlockPos proxyPos, BlockState proxyState) {
        int part = proxyState.getValue(PART);
        if (part == 3) {
            return proxyPos.below(2).relative(proxyState.getValue(FACING).getCounterClockWise());
        }
        return proxyPos.below(part);
    }

    @Override
    public BlockPos getControllerPos(BlockPos proxyPos, BlockState proxyState) {
        return getMainPos(proxyPos, proxyState);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return FlagBlock.handleInteraction(stack, level, getMainPos(pos, state), player, hand, hitResult);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            BlockPos mainPos = getMainPos(pos, state);
            if (!FlagBlock.isCleaningUp(mainPos) && level.getBlockState(mainPos).getBlock() instanceof FlagBlock) {
                level.destroyBlock(mainPos, !player.isCreative());
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    /**
     * Runs for every removal of a proxy, not just player breaks: explosions, {@code /setblock}, other
     * mods. {@link #playerWillDestroy} only covers the player case, so without this a proxy could
     * disappear and leave the flag and its remaining proxies standing.
     */
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        BlockPos mainPos = getMainPos(pos, state);

        if (!FlagBlock.isCleaningUp(mainPos)) {
            if (level.getBlockState(mainPos).getBlock() instanceof FlagBlock) {
                level.destroyBlock(mainPos, true);
            } else {
                // The flag is already gone, so nothing else will ever clean these up: sweep the leftovers.
                FlagBlock.removeProxyBlocks(level, mainPos, state.getValue(FACING));
            }
        }

        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, FACING);
    }
}
