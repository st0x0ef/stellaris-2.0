package org.exodusstudio.stellaris.common.utils;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.exodusstudio.stellaris.common.menus.rocket_station.RocketStationMenu;
import org.exodusstudio.stellaris.common.menus.rocket_station.RocketUpgradeMenu;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for creating menu providers for various in-game menus.
 */
public class MenuUtils {

    public static ExtendedMenuProvider createRocketStationMenu(BlockPos pos) {
        return new ExtendedMenuProvider() {
            @Override
            public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                return RocketStationMenu.create(i, inventory, pos);
            }

            @Override
            public void saveExtraData(FriendlyByteBuf buf) {
                buf.writeBlockPos(pos);
            }
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.stellaris.rocket_crafting");
            }
        };
    }

    public static ExtendedMenuProvider createRocketUpgraderMenu(BlockPos pos) {
        return new ExtendedMenuProvider() {

            @Override
            public void saveExtraData(FriendlyByteBuf buf) {
                buf.writeBlockPos(pos);
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("container.stellaris.rocket_station");
            }

            @Override
            public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new RocketUpgradeMenu(containerId, inventory, ContainerLevelAccess.NULL, pos);
            }
        };

    }


}
