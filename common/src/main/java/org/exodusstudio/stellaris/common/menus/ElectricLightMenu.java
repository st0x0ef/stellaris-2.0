package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.blocks.entities.machines.ElectricLightBlockEntity;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class ElectricLightMenu extends AbstractContainerMenu {

    private final ElectricLightBlockEntity entity;

    public static ElectricLightMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        ElectricLightBlockEntity entity = (ElectricLightBlockEntity) inventory.player.level().getBlockEntity(data.readBlockPos());

        return new ElectricLightMenu(syncId, entity);
    }

    public ElectricLightMenu(int syncId, ElectricLightBlockEntity entity) {
        super(MenuTypesRegistry.ELECTRIC_LIGHT.get(), syncId);

        this.entity = entity;
    }

    public ElectricLightBlockEntity getBlockEntity() {
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
