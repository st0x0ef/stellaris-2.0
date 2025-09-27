package org.exodusstudio.stellaris.common.menus;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.menus.base.BaseContainer;
import org.exodusstudio.stellaris.common.menus.slot.SDCardSlot;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;
import org.jetbrains.annotations.NotNull;

public class SDCardReaderApplicationMenu extends BaseContainer {

    private final Container inventory;

    private ItemStack card = ItemStack.EMPTY;

    public static SDCardReaderApplicationMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        return new SDCardReaderApplicationMenu(syncId, inventory, new SimpleContainer(1));
    }

    public SDCardReaderApplicationMenu(int syncId, Inventory playerInventory, Container container) {
        super(MenuTypesRegistry.SD_CARD_READER_MENU.get(), syncId, 1, playerInventory, 27, 93);

        checkContainerSize(container, 1);
        this.inventory = container;

        this.addSlot(new SDCardSlot(this, this.inventory, 0, 39, 50));
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.clearContainer(player, this.inventory);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.getSlot(index);
        if (slot != null && slot.hasItem()) {
            if (index == 36) {
                if (!this.moveItemStackTo(slot.getItem(), 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(slot.getItem(), 36, 37, true)) {
                    return ItemStack.EMPTY;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public void setCard(ItemStack card) { this.card = card; }

    public ItemStack getCard() { return this.card; }

    public boolean hasCard() { return this.card.is(ItemsRegistry.SD_CARD.get()); }

    public static ExtendedMenuProvider createProvider() {
        return new ExtendedMenuProvider() {
            @Override
            public void saveExtraData(FriendlyByteBuf buf) {

            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
                return new SDCardReaderApplicationMenu(syncId, inventory, new SimpleContainer(1));
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("container.stellaris.sd_card_reader");
            }
        };
    }

}
