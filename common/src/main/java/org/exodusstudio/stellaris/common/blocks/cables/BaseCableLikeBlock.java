package org.exodusstudio.stellaris.common.blocks.cables;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public abstract class BaseCableLikeBlock extends BaseEntityBlock {

    public static final Direction[] DIRECTIONS = Direction.values();

    // Boolean properties for physical rendering
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty DOWN = PipeBlock.DOWN;
    protected static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;

    // Enum properties for configuration mode
    public static final Map<Direction, EnumProperty<ConnectionMode>> MODE_BY_DIRECTION = Util.make(new EnumMap<>(Direction.class), map -> {
        for (Direction dir : Direction.values()) {
            map.put(dir, EnumProperty.create(dir.getName() + "_mode", ConnectionMode.class));
        }
    });

    // Sub-segment AABBs for raytracing
    public static final AABB CENTER_AABB = new AABB(0.375, 0.375, 0.375, 0.625, 0.625, 0.625);
    public static final Map<Direction, AABB> ARM_AABBS = Util.make(new EnumMap<>(Direction.class), map -> {
        map.put(Direction.DOWN,  new AABB(0.375, 0.0,   0.375, 0.625, 0.375, 0.625));
        map.put(Direction.UP,    new AABB(0.375, 0.625, 0.375, 0.625, 1.0,   0.625));
        map.put(Direction.NORTH, new AABB(0.375, 0.375, 0.0,   0.625, 0.625, 0.375));
        map.put(Direction.SOUTH, new AABB(0.375, 0.375, 0.625, 0.625, 0.625, 1.0));
        map.put(Direction.WEST,  new AABB(0.0,   0.375, 0.375, 0.375, 0.625, 0.625));
        map.put(Direction.EAST,  new AABB(0.625, 0.375, 0.375, 1.0,   0.625, 0.625));
    });

    protected final VoxelShape[] shapeByIndex;

    protected BaseCableLikeBlock(Properties properties) {
        super(properties);

        BlockState defaultState = this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false);

        for (Direction dir : Direction.values()) {
            defaultState = defaultState.setValue(MODE_BY_DIRECTION.get(dir), ConnectionMode.NORMAL);
        }
        this.registerDefaultState(defaultState);

        this.shapeByIndex = this.makeShapes(0.125f);
    }

    // ==========================================
    // ABSTRACT METHODS
    // ==========================================

    protected abstract boolean isConnectable(Level level, BlockPos pos, Direction direction);

    // ==========================================
    // BLOCKSTATE & PLACEMENT LOGIC
    // ==========================================

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = this.defaultBlockState();

        for (Direction dir : Direction.values()) {
            state = state.setValue(PROPERTY_BY_DIRECTION.get(dir), isConnectable(level, pos, dir));
        }
        return state;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader levelReader, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (levelReader instanceof Level level) {
            boolean connectable = isConnectable(level, pos, direction);
            return state.setValue(PROPERTY_BY_DIRECTION.get(direction), connectable);
        }
        return super.updateShape(state, levelReader, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
        MODE_BY_DIRECTION.values().forEach(builder::add);
    }

    @Override
    protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    // ==========================================
    // DYNAMIC VOXEL SHAPE RAYTRACING (Mekanism Style)
    // ==========================================

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape realShape = this.shapeByIndex[this.getAABBIndex(state)];

        if (context instanceof EntityCollisionContext entityContext && entityContext.getEntity() instanceof Player player) {
            if (isConfiguratorTool(player.getMainHandItem()) || isConfiguratorTool(player.getOffhandItem())) {
                return getSubSegmentShape(state, pos, player, realShape);
            }
        }

        return realShape;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.shapeByIndex[this.getAABBIndex(state)];
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        return this.shapeByIndex[this.getAABBIndex(state)];
    }

    private VoxelShape getSubSegmentShape(BlockState state, BlockPos pos, Player player, VoxelShape fallback) {
        Vec3 start = player.getEyePosition(1.0F);
        Vec3 look = player.getViewVector(1.0F);
        double reach = player.blockInteractionRange();
        Vec3 end = start.add(look.x * reach, look.y * reach, look.z * reach);

        Direction targetedDir = null;
        double closestDistance = Double.MAX_VALUE;

        // 1. Check center box intersection
        Optional<Vec3> centerHit = CENTER_AABB.move(pos).clip(start, end);
        if (centerHit.isPresent()) {
            closestDistance = centerHit.get().distanceToSqr(start);
        }

        // 2. Check active arm box intersections
        for (Direction dir : DIRECTIONS) {
            if (state.getValue(PROPERTY_BY_DIRECTION.get(dir))) {
                AABB armBox = ARM_AABBS.get(dir).move(pos);
                Optional<Vec3> hit = armBox.clip(start, end);
                if (hit.isPresent()) {
                    double dist = hit.get().distanceToSqr(start);
                    if (dist < closestDistance) {
                        closestDistance = dist;
                        targetedDir = dir;
                    }
                }
            }
        }

        // 3. Return targeted arm box or default center box
        if (targetedDir != null) {
            return Shapes.create(ARM_AABBS.get(targetedDir));
        }

        return Shapes.create(CENTER_AABB);
    }

    public static Direction getTargetedDirection(BlockPos pos, Vec3 hitLocation, Direction fallback) {
        Vec3 rel = hitLocation.subtract(pos.getX(), pos.getY(), pos.getZ());

        for (Direction dir : DIRECTIONS) {
            if (ARM_AABBS.get(dir).inflate(0.005).contains(rel)) {
                return dir;
            }
        }
        return fallback;
    }

    protected int getAABBIndex(BlockState state) {
        int i = 0;
        for (int j = 0; j < DIRECTIONS.length; ++j) {
            if (state.getValue(PROPERTY_BY_DIRECTION.get(DIRECTIONS[j]))) {
                i |= 1 << j;
            }
        }
        return i;
    }

    private VoxelShape[] makeShapes(float apothem) {
        float f = 0.5F - apothem;
        float g = 0.5F + apothem;
        VoxelShape voxelShape = Shapes.box(f, f, f, g, g, g);
        VoxelShape[] voxelShapes = new VoxelShape[DIRECTIONS.length];

        for (int i = 0; i < DIRECTIONS.length; ++i) {
            Direction direction = DIRECTIONS[i];
            voxelShapes[i] = Shapes.box(
                    0.5 + Math.min(-apothem, (double) direction.getStepX() * 0.5),
                    0.5 + Math.min(-apothem, (double) direction.getStepY() * 0.5),
                    0.5 + Math.min(-apothem, (double) direction.getStepZ() * 0.5),
                    0.5 + Math.max(apothem, (double) direction.getStepX() * 0.5),
                    0.5 + Math.max(apothem, (double) direction.getStepY() * 0.5),
                    0.5 + Math.max(apothem, (double) direction.getStepZ() * 0.5)
            );
        }

        VoxelShape[] voxelShapes2 = new VoxelShape[64];

        for (int j = 0; j < 64; ++j) {
            VoxelShape voxelShape2 = voxelShape;

            for (int k = 0; k < DIRECTIONS.length; ++k) {
                if ((j & 1 << k) != 0) {
                    voxelShape2 = Shapes.or(voxelShape2, voxelShapes[k]);
                }
            }

            voxelShapes2[j] = voxelShape2;
        }

        return voxelShapes2;
    }

    protected boolean isConfiguratorTool(ItemStack stack) {
        return stack.is(TagsRegistry.ItemTags.WRENCH);
    }
}
