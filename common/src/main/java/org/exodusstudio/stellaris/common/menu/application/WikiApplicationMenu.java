package org.exodusstudio.stellaris.common.menu.application;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class WikiApplicationMenu extends AbstractApplicationMenu {

    public WikiApplicationMenu(@Nullable MenuType<?> menuType, int syncId, Inventory playerInventory) {
        super(menuType, syncId, playerInventory);
    }

    @Override
    public WikiApplicationMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        return new WikiApplicationMenu(null, syncId, inventory);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
