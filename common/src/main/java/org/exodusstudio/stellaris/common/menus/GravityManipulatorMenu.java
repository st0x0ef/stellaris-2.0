package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class GravityManipulatorMenu extends AbstractContainerMenu {

    private final GravityManipulatorBlockEntity entity;


    public static GravityManipulatorMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        GravityManipulatorBlockEntity entity = (GravityManipulatorBlockEntity) inventory.player.level().getBlockEntity(data.readBlockPos());

        return new GravityManipulatorMenu(syncId, entity);
    }

    public GravityManipulatorMenu(int syncId, GravityManipulatorBlockEntity entity) {
        super(MenuTypesRegistry.GRAVITY_MANIPULATOR.get(), syncId);

        this.entity = entity;
    }

    public GravityManipulatorBlockEntity getBlockEntity() {
        return entity;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
