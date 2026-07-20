package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public class RocketLaunchPadProxyBlock extends Block implements MultiblockProxyBlock {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<RocketLaunchPadProxyPart> PART = EnumProperty.create("part", RocketLaunchPadProxyPart.class);

    private static final VoxelShape PLATFORM_SHAPE = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.187, 1.0);

    private static final VoxelShape ARM_OUTER_CCW = Block.box(4, 0, 6, 15, 16, 10);
    private static final VoxelShape ARM_OUTER_CW = Block.box(1, 0, 6, 12, 16, 10);
    private static final VoxelShape ARM_INNER_CCW = Block.box(0, 0, 6, 5, 16, 10);
    private static final VoxelShape ARM_INNER_CW = Block.box(11, 0, 6, 16, 16, 10);

    private static final Map<Direction, VoxelShape> ARM_OUTER_CCW_BY_FACING = precomputeRotations(ARM_OUTER_CCW);
    private static final Map<Direction, VoxelShape> ARM_OUTER_CW_BY_FACING = precomputeRotations(ARM_OUTER_CW);
    private static final Map<Direction, VoxelShape> ARM_INNER_CCW_BY_FACING = precomputeRotations(ARM_INNER_CCW);
    private static final Map<Direction, VoxelShape> ARM_INNER_CW_BY_FACING = precomputeRotations(ARM_INNER_CW);

    private static final Map<RocketLaunchPadProxyPart, Map<Direction, VoxelShape>> OUTER_PLATFORM_SHAPES = buildOuterPlatformShapes();

    public RocketLaunchPadProxyBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, RocketLaunchPadProxyPart.PLATFORM_FRONT));
    }

    @Override
    protected @NotNull MapCodec<? extends Block> codec() {
        return simpleCodec(RocketLaunchPadProxyBlock::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockPos mainPos = getMainPos(pos, state);

        if (stack.is(ItemsRegistry.LAUNCH_PAD_TOWERS.get())) {
            return RocketLaunchPadBlock.tryEnableTowers(level, mainPos, level.getBlockState(mainPos), player, stack);
        }

        if (stack.is(BlocksRegistry.ANTENNA.item().get())) {
            return RocketLaunchPadBlock.tryPlaceAntenna(level, mainPos, level.getBlockState(mainPos), player, stack);
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        InteractionResult result = RocketLaunchPadBlock.tryOpenAntenna(level, getMainPos(pos, state), player, hitResult);
        if (result != InteractionResult.PASS) {
            return result;
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            BlockPos mainPos = getMainPos(pos, state);

            if (!RocketLaunchPadBlock.isCleaningUpMain(mainPos)) {
                BlockState mainState = level.getBlockState(mainPos);

                if (mainState.getBlock() instanceof RocketLaunchPadBlock) {
                    level.destroyBlock(mainPos, !player.isCreative());
                }
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(PART).isArmPart() ? getArmShape(state) : getPlatformShape(state);
    }

    @Override
    protected @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(PART).isArmPart() ? Shapes.empty() : getPlatformShape(state);
    }

    @Override
    protected @NotNull VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(PART).isArmPart() ? getArmShape(state) : getPlatformShape(state);
    }

    private static VoxelShape getPlatformShape(BlockState state) {
        RocketLaunchPadProxyPart part = state.getValue(PART);
        Map<Direction, VoxelShape> byFacing = OUTER_PLATFORM_SHAPES.get(part);
        return byFacing == null ? PLATFORM_SHAPE : byFacing.getOrDefault(state.getValue(FACING), PLATFORM_SHAPE);
    }

    private static Map<RocketLaunchPadProxyPart, Map<Direction, VoxelShape>> buildOuterPlatformShapes() {
        Map<RocketLaunchPadProxyPart, Map<Direction, VoxelShape>> map = new EnumMap<>(RocketLaunchPadProxyPart.class);
        for (RocketLaunchPadProxyPart part : RocketLaunchPadProxyPart.values()) {
            if (part.isOuterPlatform()) {
                map.put(part, precomputeRotations(northPlatformFootprint(part)));
            }
        }
        return map;
    }

    private static VoxelShape northPlatformFootprint(RocketLaunchPadProxyPart part) {
        double x1 = part.cwOffset == -2 ? 8 : 0;
        double x2 = part.cwOffset == 2 ? 8 : 16;
        double z1 = part.forwardOffset == 2 ? 8 : 0;
        double z2 = part.forwardOffset == -2 ? 8 : 16;
        return Block.box(x1, 0, z1, x2, 3, z2);
    }

    private static VoxelShape getArmShape(BlockState state) {
        Direction facing = state.getValue(FACING);
        RocketLaunchPadProxyPart part = state.getValue(PART);
        boolean outer = Math.abs(part.cwOffset) == 2;
        boolean clockwiseSide = part.cwOffset > 0;

        Map<Direction, VoxelShape> shapes = outer
                ? (clockwiseSide ? ARM_OUTER_CW_BY_FACING : ARM_OUTER_CCW_BY_FACING)
                : (clockwiseSide ? ARM_INNER_CW_BY_FACING : ARM_INNER_CCW_BY_FACING);

        return shapes.getOrDefault(facing, Shapes.block());
    }

    private static Map<Direction, VoxelShape> precomputeRotations(VoxelShape northShape) {
        Map<Direction, VoxelShape> map = new EnumMap<>(Direction.class);
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            int times = Math.floorMod(facing.get2DDataValue() - Direction.NORTH.get2DDataValue(), 4);
            map.put(facing, rotateY(northShape, times));
        }
        return map;
    }

    private static VoxelShape rotateY(VoxelShape shape, int times) {
        VoxelShape result = shape;
        for (int i = 0; i < times; i++) {
            VoxelShape src = result;
            VoxelShape[] acc = { Shapes.empty() };
            src.forAllBoxes((x1, y1, z1, x2, y2, z2) ->
                    acc[0] = Shapes.or(acc[0], Shapes.box(1 - z2, y1, x1, 1 - z1, y2, x2)));
            result = acc[0];
        }
        return result;
    }

    @Override
    protected @NotNull VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected @NotNull VoxelShape getOcclusionShape(BlockState state) {
        return Shapes.empty();
    }

    public static BlockPos getProxyPos(BlockPos mainPos, Direction facing, RocketLaunchPadProxyPart part) {
        return mainPos
                .relative(facing, part.forwardOffset)
                .relative(facing.getClockWise(), part.cwOffset)
                .above(part.upOffset);
    }

    public static BlockPos getMainPos(BlockPos proxyPos, BlockState proxyState) {
        Direction facing = proxyState.getValue(FACING);
        RocketLaunchPadProxyPart part = proxyState.getValue(PART);

        return proxyPos
                .relative(facing, -part.forwardOffset)
                .relative(facing.getClockWise(), -part.cwOffset)
                .below(part.upOffset);
    }

    @Override
    public BlockPos getControllerPos(BlockPos proxyPos, BlockState proxyState) {
        return getMainPos(proxyPos, proxyState);
    }

    public enum RocketLaunchPadProxyPart implements StringRepresentable {
        PLATFORM_FRONT            ( 1,  0, 0),
        PLATFORM_BACK             (-1,  0, 0),
        PLATFORM_LEFT             ( 0, -1, 0),
        PLATFORM_RIGHT            ( 0,  1, 0),
        PLATFORM_FRONT_LEFT       ( 1, -1, 0),
        PLATFORM_FRONT_RIGHT      ( 1,  1, 0),
        PLATFORM_BACK_LEFT        (-1, -1, 0),
        PLATFORM_BACK_RIGHT       (-1,  1, 0),

        PLATFORM_FAR_FRONT_FAR_LEFT  ( 2, -2, 0),
        PLATFORM_FAR_FRONT_LEFT      ( 2, -1, 0),
        PLATFORM_FAR_FRONT           ( 2,  0, 0),
        PLATFORM_FAR_FRONT_RIGHT     ( 2,  1, 0),
        PLATFORM_FAR_FRONT_FAR_RIGHT ( 2,  2, 0),

        PLATFORM_FAR_BACK_FAR_LEFT   (-2, -2, 0),
        PLATFORM_FAR_BACK_LEFT       (-2, -1, 0),
        PLATFORM_FAR_BACK            (-2,  0, 0),
        PLATFORM_FAR_BACK_RIGHT      (-2,  1, 0),
        PLATFORM_FAR_BACK_FAR_RIGHT  (-2,  2, 0),

        PLATFORM_FAR_LEFT_FRONT      ( 1, -2, 0),
        PLATFORM_FAR_LEFT            ( 0, -2, 0),
        PLATFORM_FAR_LEFT_BACK       (-1, -2, 0),

        PLATFORM_FAR_RIGHT_FRONT     ( 1,  2, 0),
        PLATFORM_FAR_RIGHT           ( 0,  2, 0),
        PLATFORM_FAR_RIGHT_BACK      (-1,  2, 0),

        ARM_CCW_1            ( 0, -2, 1),
        ARM_CCW_2            ( 0, -2, 2),
        ARM_CCW_3            ( 0, -2, 3),
        ARM_CCW_4            ( 0, -2, 4),
        ARM_CCW_5            ( 0, -2, 5),

        ARM_CW_1             ( 0,  2, 1),
        ARM_CW_2             ( 0,  2, 2),
        ARM_CW_3             ( 0,  2, 3),
        ARM_CW_4             ( 0,  2, 4),
        ARM_CW_5             ( 0,  2, 5);

        public final int forwardOffset;
        public final int cwOffset;
        public final int upOffset;
        private final String serializedName;

        RocketLaunchPadProxyPart(int forwardOffset, int cwOffset, int upOffset) {
            this.forwardOffset = forwardOffset;
            this.cwOffset = cwOffset;
            this.upOffset = upOffset;
            this.serializedName = name().toLowerCase(Locale.ROOT);
        }

        public boolean isArmPart() {
            return upOffset > 0;
        }

        public boolean isOuterPlatform() {
            return upOffset == 0 && (Math.abs(cwOffset) == 2 || Math.abs(forwardOffset) == 2);
        }

        @Override
        public String getSerializedName() {
            return serializedName;
        }
    }
}
