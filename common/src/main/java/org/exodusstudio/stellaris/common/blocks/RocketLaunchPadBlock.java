package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.exodusstudio.stellaris.common.blocks.entities.RocketLaunchPadBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class RocketLaunchPadBlock extends BaseEntityBlock  {

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty TOWERS = BooleanProperty.create("towers");
    public static final BooleanProperty ANTENNA = BooleanProperty.create("antenna");

    private static final Set<BlockPos> CLEANING_UP_MAINS = new HashSet<>();

    public static final VoxelShape SHAPE_NORMAL = Shapes.box(0, 0, 0, 1, 0.187, 1);

    public RocketLaunchPadBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(TOWERS, false)
                .setValue(ANTENNA, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(RocketLaunchPadBlock::new);
    }

    static boolean isCleaningUpMain(BlockPos pos) {
        return CLEANING_UP_MAINS.contains(pos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());

        Level level = context.getLevel();
        BlockPos origin = context.getClickedPos();
        Direction facing = state.getValue(FACING);

        for (RocketLaunchPadProxyBlock.RocketLaunchPadProxyPart part : RocketLaunchPadProxyBlock.RocketLaunchPadProxyPart.values()) {
            if (part.isArmPart()) {
                continue;
            }

            BlockPos proxyPos = RocketLaunchPadProxyBlock.getProxyPos(origin, facing, part);

            if (level.isOutsideBuildHeight(proxyPos)) {
                return null;
            }

            if (!level.getBlockState(proxyPos).canBeReplaced(context)) {
                return null;
            }
        }

        return state;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide()) {
            placeProxyBlocks(level, pos, state.getValue(FACING), false);
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        removeProxyBlocks(level, pos, state.getValue(FACING));

        if (state.getValue(TOWERS)) {
            popResource(level, pos, new ItemStack(ItemsRegistry.LAUNCH_PAD_TOWERS.get()));
        }

        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    private static void placeProxyBlocks(Level level, BlockPos origin, Direction facing, boolean includeArms) {
        for (RocketLaunchPadProxyBlock.RocketLaunchPadProxyPart part : RocketLaunchPadProxyBlock.RocketLaunchPadProxyPart.values()) {
            if (!includeArms && part.isArmPart()) {
                continue;
            }

            BlockPos proxyPos = RocketLaunchPadProxyBlock.getProxyPos(origin, facing, part);

            BlockState proxyState = BlocksRegistry.ROCKET_LAUNCH_PAD_PROXY.get()
                    .defaultBlockState()
                    .setValue(RocketLaunchPadProxyBlock.FACING, facing)
                    .setValue(RocketLaunchPadProxyBlock.PART, part);

            level.setBlock(proxyPos, proxyState, 3);
        }
    }

    private static void removeProxyBlocks(Level level, BlockPos origin, Direction facing) {
        BlockPos immutableOrigin = origin.immutable();
        CLEANING_UP_MAINS.add(immutableOrigin);

        try {
            for (RocketLaunchPadProxyBlock.RocketLaunchPadProxyPart part : RocketLaunchPadProxyBlock.RocketLaunchPadProxyPart.values()) {
                BlockPos proxyPos = RocketLaunchPadProxyBlock.getProxyPos(origin, facing, part);
                BlockState proxyState = level.getBlockState(proxyPos);

                if (proxyState.getBlock() instanceof RocketLaunchPadProxyBlock) {
                    BlockPos linkedMain = RocketLaunchPadProxyBlock.getMainPos(proxyPos, proxyState);

                    if (linkedMain.equals(origin)) {
                        level.removeBlock(proxyPos, false);
                    }
                }
            }
        } finally {
            CLEANING_UP_MAINS.remove(immutableOrigin);
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(ItemsRegistry.LAUNCH_PAD_TOWERS.get())) {
            return tryEnableTowers(level, pos, state, player, stack);
        }

        if (stack.is(BlocksRegistry.ANTENNA.item().get())) {
            return tryPlaceAntenna(level, pos, state, player, stack);
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        InteractionResult result = tryOpenAntenna(level, pos, player, hitResult);
        if (result != InteractionResult.PASS) {
            return result;
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    /**
     * Places an antenna in the block directly below the launch pad center, if that block is
     * replaceable ({@link TagsRegistry.BlockTags#ANTENNA_REPLACEABLES}). Callable from the main
     * block or any proxy (resolved to its main position).
     */
    public static InteractionResult tryPlaceAntenna(Level level, BlockPos mainPos, BlockState mainState, Player player, ItemStack stack) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockPos belowPos = mainPos.below();

        if (level.getBlockState(belowPos).is(TagsRegistry.BlockTags.ANTENNA_REPLACEABLES)) {
            level.setBlock(belowPos, BlocksRegistry.ANTENNA.block().get().defaultBlockState(), 3);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            level.setBlock(mainPos, mainState.setValue(ANTENNA, true), 3);



            return InteractionResult.SUCCESS;
        }

        player.sendSystemMessage(Component.translatable("message.stellaris.launch_pad_antenna_obstructed"));
        return InteractionResult.FAIL;
    }

    /**
     * Opens the GUI of the antenna directly below the launch pad center, if present. Returns
     * {@link InteractionResult#PASS} when there is no antenna there.
     */
    public static InteractionResult tryOpenAntenna(Level level, BlockPos mainPos, Player player, BlockHitResult hitResult) {
        BlockPos belowPos = mainPos.below();
        BlockState belowState = level.getBlockState(belowPos);

        if (belowState.getBlock() instanceof AntennaBlock antennaBlock) {
            return antennaBlock.useWithoutItem(belowState, level, belowPos, player, hitResult);
        }

        return InteractionResult.PASS;
    }

    /**
     * Enables the towers on the launch pad at {@code mainPos}. Callable from the main block or
     * from any of its proxy blocks (which resolve their main position first).
     */
    public static InteractionResult tryEnableTowers(Level level, BlockPos mainPos, BlockState mainState, Player player, ItemStack stack) {
        if (!(mainState.getBlock() instanceof RocketLaunchPadBlock) || mainState.getValue(TOWERS)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        Direction facing = mainState.getValue(FACING);

        for (RocketLaunchPadProxyBlock.RocketLaunchPadProxyPart part : RocketLaunchPadProxyBlock.RocketLaunchPadProxyPart.values()) {
            if (!part.isArmPart()) {
                continue;
            }

            BlockPos proxyPos = RocketLaunchPadProxyBlock.getProxyPos(mainPos, facing, part);

            if (level.isOutsideBuildHeight(proxyPos) || !level.getBlockState(proxyPos).canBeReplaced()) {
                player.sendSystemMessage(Component.translatable("message.stellaris.launch_pad_towers_obstructed"));
                return InteractionResult.FAIL;
            }
        }

        placeProxyBlocks(level, mainPos, facing, true);
        level.setBlock(mainPos, mainState.setValue(TOWERS, true), 3);

        if (!player.isCreative()) {
            stack.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RocketLaunchPadBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (!level.isClientSide()) {
            return null;
        }

        return createTickerHelper(blockEntityType, BlockEntitiesRegistry.ROCKET_LAUNCH_PAD.get(),
                (_, _, _, be) -> ((RocketLaunchPadBlockEntity) be).clientTick());
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TOWERS, ANTENNA);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return !world.getBlockState(pos.below()).is(state.getBlock());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE_NORMAL;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        level.scheduleTick(pos, this, 1);
    }

    @Override
    protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
        return false;
    }
}
