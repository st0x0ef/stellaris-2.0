package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

// Proxy block for the Pumpjack multiblock, basically just place them everywhere the model goes :)

public class PumpjackProxyBlock extends Block implements MultiblockProxyBlock {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<PumpjackProxyPart> PART = EnumProperty.create("part", PumpjackProxyPart.class);

    private static final VoxelShape FULL_BLOCK = Block.box(
            0.0D, 0.0D, 0.0D,
            16.0D, 16.0D, 16.0D
    );

    public PumpjackProxyBlock(Properties properties) {
        super(properties);

        registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, PumpjackProxyPart.CENTER_TOP));
    }

    @Override
    protected @NotNull MapCodec<? extends Block> codec() {
        return simpleCodec(PumpjackProxyBlock::new);
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
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return PumpjackBlock.openPumpjackMenu(level, getMainPos(pos, state), player);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            BlockPos mainPos = getMainPos(pos, state);

            if (!PumpjackBlock.isCleaningUpMain(mainPos)) {
                BlockState mainState = level.getBlockState(mainPos);

                if (mainState.getBlock() instanceof PumpjackBlock) {
                    level.destroyBlock(mainPos, !player.isCreative());
                }
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }


    @Override
    protected @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return FULL_BLOCK;
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getTranslatedMainPumpjackBox(state, level, pos);
    }

    @Override
    protected @NotNull VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return getTranslatedMainPumpjackBox(state, level, pos);
    }

    private static VoxelShape getTranslatedMainPumpjackBox(BlockState proxyState, BlockGetter level, BlockPos proxyPos) {
        BlockPos mainPos = getMainPos(proxyPos, proxyState);
        BlockState mainState = level.getBlockState(mainPos);

        if (!(mainState.getBlock() instanceof PumpjackBlock)) {
            return FULL_BLOCK;
        }

        double xOffset = mainPos.getX() - proxyPos.getX();
        double yOffset = mainPos.getY() - proxyPos.getY();
        double zOffset = mainPos.getZ() - proxyPos.getZ();

        return PumpjackBlock.getPumpjackBox(mainState).move(xOffset, yOffset, zOffset);
    }

    @Override
    protected @NotNull VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected @NotNull VoxelShape getOcclusionShape(BlockState state) {
        return Shapes.empty();
    }

    public static BlockPos getProxyPos(BlockPos mainPos, Direction facing, PumpjackProxyPart part) {
        return mainPos
                .relative(facing, part.forwardOffset)
                .above(part.upOffset);
    }

    public static BlockPos getMainPos(BlockPos proxyPos, BlockState proxyState) {
        Direction facing = proxyState.getValue(FACING);
        PumpjackProxyPart part = proxyState.getValue(PART);

        return proxyPos
                .relative(facing, -part.forwardOffset)
                .below(part.upOffset);
    }

    @Override
    public BlockPos getControllerPos(BlockPos proxyPos, BlockState proxyState) {
        return getMainPos(proxyPos, proxyState);
    }

    public enum PumpjackProxyPart implements StringRepresentable {
        CENTER_TOP(0, 1),
        FRONT_BOTTOM(1, 0),
        FRONT_TOP(1, 1),
        BACK_BOTTOM(-1, 0),
        BACK_TOP(-1, 1);

        private final int forwardOffset;
        private final int upOffset;
        private final String serializedName;

        PumpjackProxyPart(int forwardOffset, int upOffset) {
            this.forwardOffset = forwardOffset;
            this.upOffset = upOffset;
            this.serializedName = name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String getSerializedName() {
            return serializedName;
        }
    }
}