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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SlidingDoorBlock extends DoorBlock implements EntityBlock {
    public static final MapCodec<SlidingDoorBlock> CODEC = simpleCodec(SlidingDoorBlock::new);

    private static final Map<ShapeKey, VoxelShape> SHAPES = new ConcurrentHashMap<>();

    public SlidingDoorBlock(Properties properties) {
        super(BlockSetType.IRON, properties.dynamicShape());
    }

    @Override
    public MapCodec<SlidingDoorBlock> codec() {
        return CODEC;
    }

    public record Layout(Direction slideDirection, int index, int size) {
        public float travel(float openness) {
            return openness * (16 * size - 1);
        }
    }

    private record Row(BlockPos first, Direction along, int size, int index) {
        BlockPos get(int i) {
            return first.relative(along, i);
        }
    }

    private static boolean sameRow(BlockState state, BlockState other) {
        return other.getBlock() instanceof SlidingDoorBlock
                && other.getValue(FACING).getAxis() == state.getValue(FACING).getAxis()
                && other.getValue(HALF) == state.getValue(HALF);
    }

    private static int countRow(BlockGetter level, BlockPos pos, BlockState state, Direction direction) {
        BlockPos.MutableBlockPos cursor = pos.mutable();
        int count = 0;
        while (count < 16 && sameRow(state, level.getBlockState(cursor.move(direction)))) count++;
        return count;
    }

    private static Row getRow(BlockGetter level, BlockPos pos, BlockState state) {
        Direction along = Direction.fromAxisAndDirection(state.getValue(FACING).getClockWise().getAxis(), Direction.AxisDirection.POSITIVE);
        int before = countRow(level, pos, state, along.getOpposite());
        int after = countRow(level, pos, state, along);
        return new Row(pos.relative(along.getOpposite(), before), along, before + after + 1, before);
    }

    public static Layout getLayout(BlockGetter level, BlockPos pos, BlockState state) {
        Row row = getRow(level, pos, state);
        Direction negative = row.along().getOpposite();
        BlockState first = row.index() == 0 ? state : level.getBlockState(row.first());
        int negativeSize = row.size() / 2 + (row.size() % 2 == 1 && getExtraSide(first) == negative ? 1 : 0);
        return row.index() < negativeSize
                ? new Layout(negative, row.index(), negativeSize)
                : new Layout(row.along(), row.size() - 1 - row.index(), row.size() - negativeSize);
    }

    private static Direction getExtraSide(BlockState state) {
        Direction facing = state.getValue(FACING);
        return state.getValue(HINGE) == DoorHingeSide.LEFT ? facing.getCounterClockWise() : facing.getClockWise();
    }

    private static DoorHingeSide hingeTowards(BlockState state, Direction side) {
        return state.getValue(FACING).getCounterClockWise() == side ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockPos lower = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
        int progress = level.getBlockEntity(lower) instanceof SlidingDoorBlockEntity door
                ? door.getProgress()
                : state.getValue(OPEN) ? SlidingDoorBlockEntity.SLIDE_TICKS : 0;
        return SHAPES.computeIfAbsent(new ShapeKey(getLayout(level, pos, state), progress), SlidingDoorBlock::buildShape);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) return null;
        Direction facing = state.getValue(FACING);
        for (Direction side : new Direction[]{facing.getCounterClockWise(), facing.getClockWise()}) {
            BlockState neighbor = context.getLevel().getBlockState(context.getClickedPos().relative(side));
            if (sameRow(state, neighbor)) {
                state = state.setValue(HINGE, hingeTowards(state, getExtraSide(neighbor)));
                return state.getValue(OPEN) ? state : state.setValue(OPEN, neighbor.getValue(OPEN)).setValue(POWERED, neighbor.getValue(POWERED));
            }
        }
        return state;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston) {
        Row row = getRow(level, pos, state);
        int signals = 0;
        int signalSum = 0;
        for (int i = 0; i < row.size(); i++) {
            BlockPos member = row.get(i);
            if (hasSignal(level, member, level.getBlockState(member))) {
                signals++;
                signalSum += i;
            }
        }
        boolean powered = signals > 0;

        Direction extraSide = null;
        if (powered && !state.getValue(POWERED) && row.size() % 2 == 1) {
            int middle = row.size() / 2;
            float average = (float) signalSum / signals;
            if (average < middle) extraSide = row.along().getOpposite();
            else if (average > middle) extraSide = row.along();
            else extraSide = getSignalSide(level, row.get(middle), level.getBlockState(row.get(middle)));
        }

        boolean toggled = false;
        for (int i = 0; i < row.size(); i++) {
            BlockPos member = row.get(i);
            BlockState memberState = level.getBlockState(member);
            BlockState updated = memberState.setValue(POWERED, powered).setValue(OPEN, powered);
            if (extraSide != null) updated = updated.setValue(HINGE, hingeTowards(updated, extraSide));
            if (updated != memberState) {
                toggled |= memberState.getValue(OPEN) != powered;
                level.setBlock(member, updated, UPDATE_CLIENTS);
            }
        }

        if (toggled) {
            level.playSound(null, pos, powered ? type().doorOpen() : type().doorClose(), SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
            level.gameEvent(null, powered ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        }
    }

    private static @Nullable Direction getSignalSide(Level level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        BlockPos other = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
        boolean left = hasSideSignal(level, pos, other, facing.getCounterClockWise());
        boolean right = hasSideSignal(level, pos, other, facing.getClockWise());
        if (left == right) return null;
        return left ? facing.getCounterClockWise() : facing.getClockWise();
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

    private record ShapeKey(Layout layout, int progress) {}

    private static VoxelShape buildShape(ShapeKey key) {
        Layout layout = key.layout();
        float openness = SlidingDoorBlockEntity.ease((float) key.progress() / SlidingDoorBlockEntity.SLIDE_TICKS);
        int start = 16 * layout.index();
        int end = 16 * layout.size();
        double min = Math.max(-15 * openness, start) - start;
        double max = Math.min(end - openness * (end - 1), start + 16) - start;
        if (max - min < 0.01) return Shapes.empty();

        Direction direction = layout.slideDirection();
        double low = direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? min : 16 - max;
        double high = direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? max : 16 - min;
        return direction.getAxis() == Direction.Axis.X
                ? Block.box(low, 0, 7, high, 16, 9)
                : Block.box(7, 0, low, 9, 16, high);
    }
}
