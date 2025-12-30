package org.exodusstudio.stellaris.common.menus;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.RocketStationBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.RocketStationBlockEntity;
import org.exodusstudio.stellaris.common.menus.slot.ResultSlot;
import org.exodusstudio.stellaris.common.network.packets.OpenRocketStationMenusPacket;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class RocketStationMenu extends AbstractContainerMenu {

    private final Container inventory;
    private final Player player;
    private final RocketStationBlockEntity blockEntity;

    public static RocketStationMenu createFromBuffer(int syncId, Inventory inventory, FriendlyByteBuf buffer) {
        RocketStationBlockEntity blockEntity = (RocketStationBlockEntity) inventory.player.level().getBlockEntity(buffer.readBlockPos());

        return new RocketStationMenu(syncId, inventory, new SimpleContainer(15), blockEntity);
    }

    public static RocketStationMenu create(int syncId, Inventory inventory, BlockPos pos) {
        RocketStationBlockEntity blockEntity = (RocketStationBlockEntity) inventory.player.level().getBlockEntity(pos);
        return new RocketStationMenu(syncId, inventory, new SimpleContainer(15), blockEntity);
    }

    public RocketStationMenu(int syncId, Inventory playerInventory, Container container, RocketStationBlockEntity blockEntity) {
        super(MenuTypesRegistry.ROCKET_STATION.get(), syncId);

        checkContainerSize(container, 15);
        this.inventory = container;
        this.player = playerInventory.player;
        this.blockEntity = blockEntity;
        addSlots(inventory);

        addPlayerHotbar(playerInventory);
        addPlayerInventory(playerInventory);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.getContainerSize()) {
                if (!this.moveItemStackTo(originalStack, this.inventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(originalStack, 0, this.inventory.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }
            else {
                slot.setChanged();
            }
        }

        return newStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }


    private void addSlots(Container inventory) {
        this.addSlot(new Slot(inventory, 0, 63, 22));

        this.addSlot(new Slot(inventory, 1, 54, 40));
        this.addSlot(new Slot(inventory, 2, 72, 40));

        this.addSlot(new Slot(inventory, 3, 54, 58));
        this.addSlot(new Slot(inventory, 4, 72, 58));

        this.addSlot(new Slot(inventory, 5, 54, 76));
        this.addSlot(new Slot(inventory, 6, 72, 76));

        this.addSlot(new Slot(inventory, 7, 36, 94));
        this.addSlot(new Slot(inventory, 8, 54, 94));
        this.addSlot(new Slot(inventory, 9, 72, 94));
        this.addSlot(new Slot(inventory, 10, 90, 94));

        this.addSlot(new Slot(inventory, 11, 36, 112));
        this.addSlot(new Slot(inventory, 12, 63, 112));
        this.addSlot(new Slot(inventory, 13, 90, 112));

        this.addSlot(new ResultSlot(inventory, 14, 118, 54));

    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 10 + l * 18, (84 + i * 18) + 58));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 10 + i * 18, 200));
        }
    }

    public void openUpgradeScreen() {
        this.player.closeContainer();
        NetworkManager.sendToServer(new OpenRocketStationMenusPacket("upgrade", this.blockEntity.getBlockPos()));
    }
}
