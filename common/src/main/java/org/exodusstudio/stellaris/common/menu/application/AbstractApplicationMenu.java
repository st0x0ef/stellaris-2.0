package org.exodusstudio.stellaris.common.menu.application;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractApplicationMenu extends AbstractContainerMenu {

    private final Player player;

    abstract public AbstractApplicationMenu create(int syncId, Inventory inventory, FriendlyByteBuf data);

    public AbstractApplicationMenu(@Nullable MenuType<?> menuType, int syncId, Inventory playerInventory) {
        super(menuType, syncId);
        this.player = playerInventory.player;
    }

    public Player getPlayer() {
        return player;
    }


}
