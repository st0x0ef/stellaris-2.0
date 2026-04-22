package org.exodusstudio.stellaris.common.utils;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.exodusstudio.stellaris.common.menus.engineering_station.RocketStationMenu;
import org.exodusstudio.stellaris.common.menus.engineering_station.EngineUpgradeMenu;
import org.exodusstudio.stellaris.common.menus.laboratory.ResearchMenu;
import org.exodusstudio.stellaris.common.menus.laboratory.VaccineMenu;

/**
 * Utility class for creating menu providers for various in-game menus.
 */
public class MenuUtils {

    public static ExtendedMenuProvider createRocketStationMenu(BlockPos pos) {
        return new ExtendedMenuProvider() {
            @Override
            public void saveExtraData(FriendlyByteBuf buf) {
                buf.writeBlockPos(pos);
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("stellaris.screen.engineering_station");
            }

            @Override
            public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                return RocketStationMenu.create(i, inventory, pos);
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
                return Component.translatable("stellaris.screen.engineering_station");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new EngineUpgradeMenu(containerId, inventory, ContainerLevelAccess.NULL, pos);
            }
        };
    }

    public static ExtendedMenuProvider createVaccineMenu(BlockPos pos) {
        return new ExtendedMenuProvider() {
            @Override
            public void saveExtraData(FriendlyByteBuf buf) {
                buf.writeBlockPos(pos);
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("stellaris.screen.laboratory");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new VaccineMenu(containerId, inventory, ContainerLevelAccess.NULL, pos);
            }
        };
    }

    public static ExtendedMenuProvider createResearchMenu(BlockPos pos) {
        return new ExtendedMenuProvider() {
            @Override
            public void saveExtraData(FriendlyByteBuf buf) {
                buf.writeBlockPos(pos);
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("stellaris.screen.laboratory");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return ResearchMenu.create(containerId, inventory, pos);
            }
        };
    }


}
