package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.exodusstudio.stellaris.common.blocks.entities.SlidingDoorBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.jetbrains.annotations.Nullable;

public class SlidingDoorBlock extends DoorBlock implements EntityBlock {
    public static final MapCodec<SlidingDoorBlock> CODEC = simpleCodec(SlidingDoorBlock::new);

    private static final VoxelShape[][][] SHAPES = buildShapes();

    public SlidingDoorBlock(Properties properties) {
        super(BlockSetType.IRON, properties.dynamicShape());
    }

    @Override
    public MapCodec<SlidingDoorBlock> codec() {
        return CODEC;
    }

    public static @Nullable Direction getPartnerSide(BlockGetter level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        Direction found = null;
        for (Direction side : new Direction[]{facing.getCounterClockWise(), facing.getClockWise()}) {
            BlockState neighbor = level.getBlockState(pos.relative(side));
            if (neighbor.getBlock() instanceof SlidingDoorBlock
                    && neighbor.getValue(FACING).getAxis() == facing.getAxis()
                    && neighbor.getValue(HALF) == state.getValue(HALF)) {
                if (found != null) return null;
                found = side;
            }
        }
        return found;
    }

    public static Direction getSlideDirection(BlockGetter level, BlockPos pos, BlockState state) {
        Direction partner = getPartnerSide(level, pos, state);
        if (partner != null) return partner.getOpposite();
        Direction facing = state.getValue(FACING);
        return state.getValue(HINGE) == DoorHingeSide.LEFT ? facing.getCounterClockWise() : facing.getClockWise();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockPos lower = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
        int progress = level.getBlockEntity(lower) instanceof SlidingDoorBlockEntity door
                ? door.getProgress()
                : state.getValue(OPEN) ? SlidingDoorBlockEntity.SLIDE_TICKS : 0;
        Direction facing = state.getValue(FACING);
        int side = getSlideDirection(level, pos, state) == facing.getCounterClockWise() ? 0 : 1;
        return SHAPES[facing.get2DDataValue()][side][progress];
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null || state.getValue(OPEN)) return state;
        Direction partner = getPartnerSide(context.getLevel(), context.getClickedPos(), state);
        if (partner != null && context.getLevel().getBlockState(context.getClickedPos().relative(partner)).getValue(OPEN)) {
            return state.setValue(OPEN, true).setValue(POWERED, true);
        }
        return state;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston) {
        Direction partner = getPartnerSide(level, pos, state);
        BlockPos partnerPos = partner == null ? null : pos.relative(partner);
        boolean powered = hasSignal(level, pos, state) || (partnerPos != null && hasSignal(level, partnerPos, level.getBlockState(partnerPos)));

        if (powered != state.getValue(POWERED)) {
            if (powered != state.getValue(OPEN)) {
                level.playSound(null, pos, powered ? type().doorOpen() : type().doorClose(), SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
                level.gameEvent(null, powered ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
            }
            BlockState newState = state.setValue(POWERED, powered).setValue(OPEN, powered);
            if (powered && partner == null) {
                DoorHingeSide sourceSide = getSignalSide(level, pos, newState);
                if (sourceSide != null) newState = newState.setValue(HINGE, sourceSide);
            }
            level.setBlock(pos, newState, UPDATE_CLIENTS);
        }

        if (partnerPos != null) {
            BlockState partnerState = level.getBlockState(partnerPos);
            if (partnerState.getValue(POWERED) != powered) {
                level.setBlock(partnerPos, partnerState.setValue(POWERED, powered).setValue(OPEN, powered), UPDATE_CLIENTS);
            }
        }
    }

    private static @Nullable DoorHingeSide getSignalSide(Level level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        BlockPos other = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
        boolean left = hasSideSignal(level, pos, other, facing.getCounterClockWise());
        boolean right = hasSideSignal(level, pos, other, facing.getClockWise());
        if (left == right) return null;
        return left ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
    }

    private static boolean hasSideSignal(Level level, BlockPos pos, BlockPos other, Direction side) {
        return level.hasSignal(pos.relative(side), side) || level.hasSignal(other.relative(side), side);
    }

    private static boolean hasSignal(Level level, BlockPos pos, BlockState state) {
        BlockPos other = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
        return level.hasNeighborSignal(pos) || level.hasNeighborSignal(other);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? new SlidingDoorBlockEntity(pos, state) : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != BlockEntitiesRegistry.SLIDING_DOOR.get()) {
            return null;
        }
        return (BlockEntityTicker<T>) (BlockEntityTicker<SlidingDoorBlockEntity>) (l, pos, s, door) -> door.tick(s);
    }

    private static VoxelShape[][][] buildShapes() {
        int steps = SlidingDoorBlockEntity.SLIDE_TICKS;
        VoxelShape[][][] shapes = new VoxelShape[4][2][steps + 1];
        for (int facing = 0; facing < 4; facing++) {
            Direction direction = Direction.from2DDataValue(facing);
            for (int side = 0; side < 2; side++) {
                for (int progress = 0; progress <= steps; progress++) {
                    float slide = 16 * SlidingDoorBlockEntity.ease((float) progress / steps);
                    double min = side == 0 ? 0 : slide;
                    double max = side == 0 ? 16 - slide : 16;
                    shapes[facing][side][progress] = max - min < 0.01 ? Shapes.empty() : panel(direction, min, max);
                }
            }
        }
        return shapes;
    }

    private static VoxelShape panel(Direction facing, double min, double max) {
        return switch (facing) {
            case EAST -> Block.box(7, 0, min, 9, 16, max);
            case SOUTH -> Block.box(16 - max, 0, 7, 16 - min, 16, 9);
            case WEST -> Block.box(7, 0, 16 - max, 9, 16, 16 - min);
            default -> Block.box(min, 0, 7, max, 16, 9);
        };
    }
}
