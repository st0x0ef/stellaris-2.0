package org.exodusstudio.stellaris.common.menus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.blocks.entities.FluidTankBlockEntity;
import org.exodusstudio.stellaris.common.menus.base.BaseContainer;
import org.exodusstudio.stellaris.common.menus.slot.FluidContainerSlot;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class FluidTankMenu extends BaseContainer {

    private final Container inventory;
    private final FluidTankBlockEntity blockEntity;

    public static FluidTankMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        return new FluidTankMenu(syncId, inventory, new SimpleContainer(2), (FluidTankBlockEntity) inventory.player.level().getBlockEntity(data.readBlockPos()));
    }

    public FluidTankMenu(int syncId, Inventory playerInventory, Container container, FluidTankBlockEntity entity) {
        super(MenuTypesRegistry.FLUID_TANK_MENU.get(), syncId, 2, playerInventory, 10, 106);

        checkContainerSize(container, 2);
        this.inventory = container;
        this.blockEntity = entity;

        addSlot(new FluidContainerSlot(inventory, 0, 52, 50, false)); // INSERT
        addSlot(new FluidContainerSlot(inventory, 1, 112, 50, false)); // EXTRACT
    }

    public FluidTankBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public boolean stillValid(Player player) {
        return inventory.stillValid(player);
    }
}
