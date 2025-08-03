package org.exodusstudio.stellaris.client.screen.tablet.application;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.exodusstudio.stellaris.common.menu.application.AbstractApplicationMenu;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractApplicationScreen<T extends AbstractApplicationMenu> extends AbstractContainerScreen<T> {


    public AbstractApplicationScreen(T menu, Inventory playerInventory) {
        super(menu, playerInventory, Component.empty());
    }

    public Player getPlayer() {
        return this.getMenu().getPlayer();
    }

    abstract Component getName();

    abstract Component getDescription();

    abstract Screen getScreen();

    abstract ResourceLocation getIconLocation();

    @Override
    public @NotNull Component getTitle() {
        return this.getName();
    }

    public void openMenu() {
        ExtendedMenuProvider provider = new ExtendedMenuProvider() {
            @Override
            public void saveExtraData(FriendlyByteBuf buffer) {
            }

            @Override
            public @NotNull Component getDisplayName() {
                return AbstractApplicationScreen.this.getName();
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                return AbstractApplicationScreen.this.getMenu().create(syncId, inv, buffer);
            }
        };

        if (this.getPlayer() instanceof ServerPlayer serverPlayer) {
            MenuRegistry.openExtendedMenu(serverPlayer, provider);
        }
    }
}
