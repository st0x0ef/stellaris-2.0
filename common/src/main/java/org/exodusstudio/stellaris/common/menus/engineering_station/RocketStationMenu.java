package org.exodusstudio.stellaris.common.menus.engineering_station;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.blocks.entities.machines.EngineeringStationBlockEntity;
import org.exodusstudio.stellaris.common.menus.MenuQuickMoveHelper;
import org.exodusstudio.stellaris.common.menus.slot.ResultSlot;
import org.exodusstudio.stellaris.common.networking.packets.OpenBlockEntityMenusPacket;
import org.exodusstudio.stellaris.common.registries.MenuProviderRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;


public class RocketStationMenu extends AbstractContainerMenu {

    private final Container inventory;
    private final Player player;
    private final EngineeringStationBlockEntity blockEntity;
    public final BlockPos engineeringStationPos;

    public static RocketStationMenu create(int syncId, Inventory inventory, FriendlyByteBuf buffer) {
        return create(syncId, inventory, buffer.readBlockPos());
    }

    public static RocketStationMenu create(int syncId, Inventory inventory, BlockPos pos) {
        EngineeringStationBlockEntity blockEntity = (EngineeringStationBlockEntity) inventory.player.level().getBlockEntity(pos);
        return new RocketStationMenu(syncId, inventory, blockEntity, blockEntity);
    }

    public RocketStationMenu(int syncId, Inventory playerInventory, Container container, EngineeringStationBlockEntity blockEntity) {
        super(MenuTypesRegistry.ROCKET_STATION.get(), syncId);

        checkContainerSize(container, 15);
        this.inventory = container;
        this.player = playerInventory.player;
        this.blockEntity = blockEntity;
        this.engineeringStationPos = blockEntity.getBlockPos();
        addSlots(inventory);

        addPlayerHotbar(playerInventory);
        addPlayerInventory(playerInventory);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        return MenuQuickMoveHelper.quickMoveMachineFirst(this, player, invSlot, 15);
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
        NetworkManager.sendToServer(new OpenBlockEntityMenusPacket(MenuProviderRegistry.ROCKET_UPGRADE, this.blockEntity.getBlockPos()));
    }
}
