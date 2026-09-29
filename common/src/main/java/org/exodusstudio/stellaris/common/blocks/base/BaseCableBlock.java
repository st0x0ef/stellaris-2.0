package org.exodusstudio.stellaris.common.blocks.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.exodusstudio.stellaris.common.blocks.entities.PipeBlockEntity;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseFacadeBlockEntity;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class BaseCableBlock extends BaseTickingEntityBlock {

    private static final Direction[] DIRECTIONS = Direction.values();
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty DOWN = PipeBlock.DOWN;
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;
    protected final VoxelShape[] shapeByIndex;

    public BaseCableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((this.stateDefinition.any())
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
        this.shapeByIndex = this.makeShapes(0.125f);
    }

    @Override
    protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        Level level = blockPlaceContext.getLevel();
        BlockPos blockPos = blockPlaceContext.getClickedPos();
        BlockState[] state = {this.defaultBlockState()};
        PROPERTY_BY_DIRECTION.forEach((direction, booleanProperty) ->
                state[0] = state[0].setValue(booleanProperty, isConnectable(level, blockPos.relative(direction), direction.getOpposite())));
        return state[0];
    }

    protected abstract boolean isConnectable(Level level, BlockPos pos, Direction direction);

    @Override
    protected BlockState updateShape(BlockState state, LevelReader levelReader, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        BlockEntity entity = levelReader.getBlockEntity(pos);
        if (entity != null) {
            if (isConnectable(entity.getLevel(), neighborPos, direction.getOpposite())) {
                return state.setValue(PROPERTY_BY_DIRECTION.get(direction), true);
            }
        }
        return state.setValue(PROPERTY_BY_DIRECTION.get(direction), false);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if(level.getBlockEntity(pos) instanceof BaseFacadeBlockEntity facadeBlockEntity && facadeBlockEntity.facadeState != null) {
            return facadeBlockEntity.facadeState.getShape(level, pos);
        }
        return this.shapeByIndex[this.getAABBIndex(state)];
    }

    private VoxelShape[] makeShapes(float apothem) {
        float f = 0.5F - apothem;
        float g = 0.5F + apothem;
        VoxelShape voxelShape = Block.box(f * 16.0F, f * 16.0F, f * 16.0F, g * 16.0F, g * 16.0F, g * 16.0F);
        VoxelShape[] voxelShapes = new VoxelShape[DIRECTIONS.length];

        for (int i = 0; i < DIRECTIONS.length; ++i) {
            Direction direction = DIRECTIONS[i];
            voxelShapes[i] = Shapes.box(0.5 + Math.min(-apothem, (double) direction.getStepX() * 0.5), 0.5 + Math.min(-apothem, (double) direction.getStepY() * 0.5), 0.5 + Math.min(-apothem, (double) direction.getStepZ() * 0.5), 0.5 + Math.max(apothem, (double) direction.getStepX() * 0.5), 0.5 + Math.max(apothem, (double) direction.getStepY() * 0.5), 0.5 + Math.max(apothem, (double) direction.getStepZ() * 0.5));
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

    protected int getAABBIndex(BlockState state) {
        int i = 0;

        for (int j = 0; j < DIRECTIONS.length; ++j) {
            if ((Boolean) state.getValue((Property<?>) PROPERTY_BY_DIRECTION.get(DIRECTIONS[j]))) {
                i |= 1 << j;
            }
        }

        return i;
    }

    @Override
    protected @NotNull InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (level.getBlockEntity(pos) instanceof BaseFacadeBlockEntity facadeBlockEntity) {

            if (player.isShiftKeyDown() && (itemStack.isEmpty() || facadeBlockEntity.facadeState != null)) {
                dropFacadeBlock(pos, facadeBlockEntity);
                facadeBlockEntity.setFacadeState(null);
                return InteractionResult.SUCCESS;

            } else if(itemStack.getItem() instanceof BlockItem blockItem)  {

                BlockState itemBlockState = blockItem.getBlock().defaultBlockState();

                if(!itemBlockState.is(TagsRegistry.BlockTags.PIPE_FACADE_BLACKLIST) &&//We don't want thoses block
                        !(itemBlockState.getBlock() instanceof BaseEntityBlock) &&//we don't want block entity
                        Block.isShapeFullBlock(itemBlockState.getShape(level, pos))) //And we only want full block
                {


                    if(itemBlockState.hasProperty(RotatedPillarBlock.AXIS)) {
                        itemBlockState = itemBlockState.setValue(RotatedPillarBlock.AXIS, hitResult.getDirection().getAxis());
                    }

                    itemStack.shrink(1);
                    dropFacadeBlock(pos, facadeBlockEntity);

                    facadeBlockEntity.setFacadeState(itemBlockState);

                    return InteractionResult.SUCCESS;

                }
            }
        }

        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {

        if(!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof PipeBlockEntity pipeBlockEntity) {
                dropFacadeBlock(pos, pipeBlockEntity);
            }
        }
        super.destroy(level, pos, state);
    }

    public void dropFacadeBlock(BlockPos pos, BaseFacadeBlockEntity facadeBlockEntity) {
        if(facadeBlockEntity.facadeState != null) {

            ItemEntity entity = new ItemEntity(facadeBlockEntity.getLevel(), pos.getX(), pos.getY() + 1, pos.getZ(),
                    facadeBlockEntity.facadeState.getCloneItemStack(facadeBlockEntity.getLevel(), pos, true));

            facadeBlockEntity.getLevel().addFreshEntity(entity);

        }

    }

}
