package org.exodusstudio.stellaris.common.menus.engineering_station;

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
import org.exodusstudio.stellaris.common.menus.slot.SpecificItemsSlot;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;
import org.jetbrains.annotations.NotNull;

public class SpaceStationPlannerMenu extends BaseContainer {

    private final Container inventory;
    private final Player player;
    private ItemStack card = ItemStack.EMPTY;

    public static SpaceStationPlannerMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        return new SpaceStationPlannerMenu(syncId, inventory, new SimpleContainer(10));
    }

    public SpaceStationPlannerMenu(int syncId, Inventory playerInventory, Container container) {
        super(MenuTypesRegistry.SPACE_STATION_PLANNER.get(), syncId, 10, playerInventory, 112, 100);

        checkContainerSize(container, 10);
        this.inventory = container;
        this.player = playerInventory.player;
        this.addSlot(new SpecificItemsSlot(this.inventory, 0, 235, 30, ItemsRegistry.SPACE_STATION_BLUEPRINT.get()));
        addMaterialsSlots(120, 30);
    }

    public void addMaterialsSlots(int xStart, int yStart) {
        int id = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlot(new Slot(this.inventory, ++id, xStart + j * 18, yStart + i * 18));
            }
        }
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
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

    public Player getPlayer() {
        return player;
    }

    public boolean hasCard() { return this.card.is(ItemsRegistry.SD_CARD.get()); }

    public static ExtendedMenuProvider createProvider() {
        return new ExtendedMenuProvider() {
            @Override
            public void saveExtraData(FriendlyByteBuf buf) {

            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
                return new SpaceStationPlannerMenu(syncId, inventory, new SimpleContainer(1));
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("container.stellaris.sd_card_reader");
            }
        };
    }

}
