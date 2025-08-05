package org.exodusstudio.stellaris.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class MainTabletMenu extends AbstractContainerMenu {

    private final Container container;
    private final Inventory playerInventory;


    public MainTabletMenu(int syncId, Inventory inventory, FriendlyByteBuf buffer) {
        this(syncId, inventory, new SimpleContainer(13));
    }

    public MainTabletMenu(int syncId, Inventory playerInventory, Container container) {
        super(MenuTypesRegistry.TABLET.get(), syncId);

        this.container = container;
        this.playerInventory = playerInventory;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }
}
