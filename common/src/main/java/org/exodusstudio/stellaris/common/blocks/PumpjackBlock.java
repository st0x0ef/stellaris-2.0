package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.exodusstudio.stellaris.common.blocks.base.BaseLitMachineBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.PumpjackBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class PumpjackBlock extends BaseLitMachineBlock {

    private static final Set<BlockPos> CLEANING_UP_MAINS = new HashSet<>();

    private static final VoxelShape SHAPE_NORTH_SOUTH = Block.box(
            0.0D, 0.0D, -16.0D,
            16.0D, 32.0D, 32.0D
    );

    private static final VoxelShape SHAPE_EAST_WEST = Block.box(
            -16.0D, 0.0D, 0.0D,
            32.0D, 32.0D, 16.0D
    );

    public PumpjackBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return BlockEntitiesRegistry.PUMPJACK.get();
    }

    @Override
    public boolean hasTicker(Level level) {
        return !level.isClientSide();
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(PumpjackBlock::new);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);

        if (state == null) {
            return null;
        }

        Level level = context.getLevel();
        BlockPos origin = context.getClickedPos();
        Direction facing = state.getValue(FACING);

        for (PumpjackProxyBlock.PumpjackProxyPart part : PumpjackProxyBlock.PumpjackProxyPart.values()) {
            BlockPos proxyPos = PumpjackProxyBlock.getProxyPos(origin, facing, part);

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
            placeProxyBlocks(level, pos, state.getValue(FACING));
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        removeProxyBlocks(level, pos, state.getValue(FACING));
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return openPumpjackMenu(level, pos, player);
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getPumpjackBox(state);
    }

    @Override
    protected @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getPumpjackBox(state);
    }

    @Override
    protected @NotNull VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return getPumpjackBox(state);
    }

    @Override
    protected @NotNull VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected @NotNull VoxelShape getOcclusionShape(BlockState state) {
        return Shapes.empty();
    }

    public static VoxelShape getPumpjackBox(BlockState state) {
        Direction facing = state.getValue(FACING);

        if (facing == Direction.EAST || facing == Direction.WEST) {
            return SHAPE_EAST_WEST;
        }

        return SHAPE_NORTH_SOUTH;
    }

    public static InteractionResult openPumpjackMenu(Level level, BlockPos mainPos, Player player) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }

        BlockEntity blockEntity = level.getBlockEntity(mainPos);

        if (!(blockEntity instanceof PumpjackBlockEntity pumpjackBlockEntity)) {
            return InteractionResult.PASS;
        }

        MenuRegistry.openExtendedMenu(serverPlayer, pumpjackBlockEntity, buf -> buf.writeBlockPos(mainPos));
        return InteractionResult.SUCCESS;
    }

    static boolean isCleaningUpMain(BlockPos mainPos) {
        return CLEANING_UP_MAINS.contains(mainPos);
    }

    private static void placeProxyBlocks(Level level, BlockPos origin, Direction facing) {
        for (PumpjackProxyBlock.PumpjackProxyPart part : PumpjackProxyBlock.PumpjackProxyPart.values()) {
            BlockPos proxyPos = PumpjackProxyBlock.getProxyPos(origin, facing, part);

            BlockState proxyState = BlocksRegistry.PUMPJACK_PROXY.get()
                    .defaultBlockState()
                    .setValue(PumpjackProxyBlock.FACING, facing)
                    .setValue(PumpjackProxyBlock.PART, part);

            level.setBlock(proxyPos, proxyState, 3);
        }
    }

    private static void removeProxyBlocks(Level level, BlockPos origin, Direction facing) {
        BlockPos immutableOrigin = origin.immutable();
        CLEANING_UP_MAINS.add(immutableOrigin);

        try {
            for (PumpjackProxyBlock.PumpjackProxyPart part : PumpjackProxyBlock.PumpjackProxyPart.values()) {
                BlockPos proxyPos = PumpjackProxyBlock.getProxyPos(origin, facing, part);
                BlockState proxyState = level.getBlockState(proxyPos);

                if (proxyState.getBlock() instanceof PumpjackProxyBlock) {
                    BlockPos linkedMain = PumpjackProxyBlock.getMainPos(proxyPos, proxyState);

                    if (linkedMain.equals(origin)) {
                        level.removeBlock(proxyPos, false);
                    }
                }
            }
        } finally {
            CLEANING_UP_MAINS.remove(immutableOrigin);
        }
    }
}