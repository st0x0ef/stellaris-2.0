package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.blocks.entities.machines.CargoUnloaderBlockEntity;
import org.exodusstudio.stellaris.common.menus.base.BaseContainer;
import org.exodusstudio.stellaris.common.menus.slot.ResultSlot;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class CargoUnloaderMenu extends BaseContainer {

    private final Container inventory;
    private final CargoUnloaderBlockEntity blockEntity;

    public static CargoUnloaderMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        return new CargoUnloaderMenu(syncId, inventory, new SimpleContainer(30), (CargoUnloaderBlockEntity) inventory.player.level().getBlockEntity(data.readBlockPos()));
    }

    public CargoUnloaderMenu(int syncId, Inventory playerInventory, Container container, CargoUnloaderBlockEntity entity) {
        super(MenuTypesRegistry.CARGO_UNLOADER.get(), syncId, 30, playerInventory, 10, 142);

        checkContainerSize(container, 30);
        this.inventory = container;
        this.blockEntity = entity;

        addSlot(new ResultSlot(inventory, 0, 46, 42)); // rocket
        addSlot(new ResultSlot(inventory, 1, 96, 42)); // fuel input
        addSlot(new ResultSlot(inventory, 2, 124, 42)); // fuel output

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new ResultSlot(inventory, 3 + j + i * 9, 10 + j * 18, 77 + i * 18));
            }
        }

    }

    public CargoUnloaderBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public boolean stillValid(Player player) {
        return inventory.stillValid(player);
    }
}
