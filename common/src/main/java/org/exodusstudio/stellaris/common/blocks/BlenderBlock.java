package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import org.exodusstudio.stellaris.common.blocks.base.BaseMachineBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.BlenderBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class BlenderBlock extends BaseMachineBlock {

    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    private static final int REDSTONE_DELAY = 4;

    private static final Set<BlockPos> CLEANING_UP_MAINS = new HashSet<>();

    public BlenderBlock(BlockBehaviour.Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState().setValue(TRIGGERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TRIGGERED);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                   @Nullable Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);

        boolean powered = level.hasNeighborSignal(pos);
        if (powered == state.getValue(TRIGGERED)) {
            return;
        }

        if (powered) {
            level.scheduleTick(pos, this, REDSTONE_DELAY);
        }

        level.setBlock(pos, state.setValue(TRIGGERED, powered), UPDATE_CLIENTS);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof BlenderBlockEntity blender) {
            blender.requestBlend();
        }
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return BlockEntitiesRegistry.BLENDER.get();
    }

    @Override
    public boolean hasTicker(Level level) {
        return !level.isClientSide();
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(BlenderBlock::new);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            return null;
        }

        BlockPos proxyPos = context.getClickedPos().above();
        Level level = context.getLevel();

        if (level.isOutsideBuildHeight(proxyPos) || !level.getBlockState(proxyPos).canBeReplaced(context)) {
            return null;
        }

        return state;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide()) {
            level.setBlock(pos.above(), BlocksRegistry.BLENDER_PROXY.get()
                    .defaultBlockState()
                    .setValue(BlenderProxyBlock.FACING, state.getValue(FACING)), 3);
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        removeProxyBlock(level, pos);
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    static boolean isCleaningUpMain(BlockPos mainPos) {
        return CLEANING_UP_MAINS.contains(mainPos);
    }

    private static void removeProxyBlock(Level level, BlockPos mainPos) {
        BlockPos immutableMain = mainPos.immutable();
        CLEANING_UP_MAINS.add(immutableMain);

        try {
            BlockPos proxyPos = immutableMain.above();
            BlockState proxyState = level.getBlockState(proxyPos);

            if (proxyState.getBlock() instanceof BlenderProxyBlock
                    && BlenderProxyBlock.getMainPos(proxyPos).equals(immutableMain)) {
                level.removeBlock(proxyPos, false);
            }
        } finally {
            CLEANING_UP_MAINS.remove(immutableMain);
        }
    }

    public static InteractionResult openBlenderMenu(Level level, BlockPos mainPos, Player player) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }

        if (!(level.getBlockEntity(mainPos) instanceof BlenderBlockEntity blender)) {
            return InteractionResult.PASS;
        }

        MenuRegistry.openExtendedMenu(serverPlayer, blender, buf -> buf.writeBlockPos(mainPos));
        return InteractionResult.SUCCESS;
    }
}
