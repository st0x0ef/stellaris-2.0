package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BlenderProxyBlock extends Block implements MultiblockProxyBlock {

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    private static final VoxelShape JAR = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 10.0D, 14.0D);
    private static final VoxelShape LID = Block.box(0.0D, 10.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    private static final VoxelShape HANDLES_ALONG_X = Shapes.or(
            Block.box(0.0D, 0.0D, 7.0D, 2.0D, 10.0D, 9.0D),
            Block.box(14.0D, 0.0D, 7.0D, 16.0D, 10.0D, 9.0D));
    private static final VoxelShape HANDLES_ALONG_Z = Shapes.or(
            Block.box(7.0D, 0.0D, 0.0D, 9.0D, 10.0D, 2.0D),
            Block.box(7.0D, 0.0D, 14.0D, 9.0D, 10.0D, 16.0D));

    private static final VoxelShape SHAPE_HANDLES_ALONG_X = Shapes.or(JAR, LID, HANDLES_ALONG_X);
    private static final VoxelShape SHAPE_HANDLES_ALONG_Z = Shapes.or(JAR, LID, HANDLES_ALONG_Z);

    public BlenderProxyBlock(Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull MapCodec<? extends Block> codec() {
        return simpleCodec(BlenderProxyBlock::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return BlenderBlock.openBlenderMenu(level, getMainPos(pos), player);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            BlockPos mainPos = getMainPos(pos);

            if (!BlenderBlock.isCleaningUpMain(mainPos) && level.getBlockState(mainPos).getBlock() instanceof BlenderBlock) {
                level.destroyBlock(mainPos, !player.isCreative());
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    /**
     * Runs for every removal of the proxy, not just player breaks: explosions, {@code /setblock}, other
     * mods. {@link #playerWillDestroy} only covers the player case, so without this the proxy could
     * disappear and leave the blender standing.
     */
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        BlockPos mainPos = getMainPos(pos);

        if (!BlenderBlock.isCleaningUpMain(mainPos) && level.getBlockState(mainPos).getBlock() instanceof BlenderBlock) {
            level.destroyBlock(mainPos, true);
        }

        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getBlenderTopBox(state);
    }

    @Override
    protected @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getBlenderTopBox(state);
    }

    @Override
    protected @NotNull VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return getBlenderTopBox(state);
    }

    @Override
    protected @NotNull VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected @NotNull VoxelShape getOcclusionShape(BlockState state) {
        return Shapes.empty();
    }

    private static VoxelShape getBlenderTopBox(BlockState state) {
        return state.getValue(FACING).getAxis() == Direction.Axis.Z
                ? SHAPE_HANDLES_ALONG_X
                : SHAPE_HANDLES_ALONG_Z;
    }

    public static BlockPos getMainPos(BlockPos proxyPos) {
        return proxyPos.below();
    }

    @Override
    public BlockPos getControllerPos(BlockPos proxyPos, BlockState proxyState) {
        return getMainPos(proxyPos);
    }
}
