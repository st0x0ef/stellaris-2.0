package org.exodusstudio.stellaris.common.blocks.base;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseContainerEntityBlock extends BaseTickingEntityBlock {

    protected BaseContainerEntityBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BaseContainerBlockEntity) {
                MenuRegistry.openExtendedMenu((ServerPlayer) player, getMenuProvider(state, level, pos));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    protected ExtendedMenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof BaseContainerBlockEntity containerBlockEntity) {
            return new MenuProvider(pos, containerBlockEntity);
        }
        return null;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof BaseContainerBlockEntity containerBlockEntity) {
            Containers.dropContents(level, pos, containerBlockEntity);
            level.updateNeighbourForOutputSignal(pos, this);
            state.updateNeighbourShapes(level, pos, UPDATE_NEIGHBORS);
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    protected record MenuProvider(BlockPos pos,
                                  BaseContainerBlockEntity containerBlockEntity) implements ExtendedMenuProvider {

        @Override
        public void saveExtraData(FriendlyByteBuf buf) {
            buf.writeBlockPos(pos);
        }

        @Override
        public Component getDisplayName() {
            return containerBlockEntity.getDisplayName();
        }

        @Nullable
        @Override
        public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
            return containerBlockEntity.createMenu(syncId, inventory, player);
        }
    }
}
