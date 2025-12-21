package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.exodusstudio.stellaris.common.menus.UpgradeStationMenu;
import org.jetbrains.annotations.Nullable;

public class RocketStationBlock extends Block {

    public static final MapCodec<RocketStationBlock> CODEC = simpleCodec(RocketStationBlock::new);
    private static final Component CONTAINER_TITLE = Component.translatable("container.stellaris.rocket_station");
    private static final VoxelShape SHAPE = Block.box(0,0,0,16,12,16);


    public RocketStationBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @Nullable ExtendedMenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new ExtendedMenuProvider() {

            @Override
            public void saveExtraData(FriendlyByteBuf buf) {
            }

            @Override
            public Component getDisplayName() {
                return CONTAINER_TITLE;
            }

            @Override
            public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new UpgradeStationMenu(containerId, inventory, ContainerLevelAccess.create(level, pos));
            }
        };

    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        else {
            MenuRegistry.openExtendedMenu((ServerPlayer) player, this.getMenuProvider(state, level, pos));
            return InteractionResult.CONSUME;
        }
    }

}
