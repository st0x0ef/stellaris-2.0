package org.exodusstudio.stellaris.common.menus.engineering_station;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.client.screens.engineering_station.SpaceStationPlannerScreen;
import org.exodusstudio.stellaris.common.blocks.entities.machines.EngineeringStationBlockEntity;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationRecipe;
import org.exodusstudio.stellaris.common.menus.base.BaseContainer;
import org.exodusstudio.stellaris.common.menus.slot.SpecificItemsSlot;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SpaceStationPlannerMenu extends BaseContainer implements ContainerListener {

    private final Container inventory;
    private final Player player;
    private ItemStack card = ItemStack.EMPTY;
    private int resultSlotId;
    public final BlockPos engineeringStationPos;
    private final EngineeringStationBlockEntity blockEntity;

    public List<Slot> materialSlot = new ArrayList<>();

    public boolean checked = false;

    public static SpaceStationPlannerMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        EngineeringStationBlockEntity be = (EngineeringStationBlockEntity) inventory.player.level().getBlockEntity(pos);
        SimpleContainer container = new SimpleContainer(10);
        if (be != null) {
            for (int i = 0; i < 10; i++) container.setItem(i, be.spaceStationPlannerItems.get(i));
        }
        return new SpaceStationPlannerMenu(syncId, inventory, container, pos, be);
    }

    public SpaceStationPlannerMenu(int syncId, Inventory playerInventory, Container container, BlockPos pos, EngineeringStationBlockEntity blockEntity) {
        super(MenuTypesRegistry.SPACE_STATION_PLANNER.get(), syncId, 10, playerInventory, 10, 142);

        checkContainerSize(container, 10);
        this.engineeringStationPos = pos;
        this.blockEntity = blockEntity;
        this.inventory = container;
        this.player = playerInventory.player;
        this.resultSlotId = this.addSlot(new SpecificItemsSlot(this.inventory, 0, 122, 56, ItemsRegistry.SPACE_STATION_BLUEPRINT.get())).index;
        addMaterialsSlots(30, 48);
        this.addSlotListener(this);
    }

    public void addMaterialsSlots(int xStart, int yStart) {
        int id = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlot(new Slot(this.inventory, ++id, xStart + j * 18, yStart + i * 18));
                materialSlot.add(this.slots.getLast());
            }
        }
    }


    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (blockEntity != null && blockEntity.isTabSwitching()) {
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                blockEntity.spaceStationPlannerItems.set(i, inventory.getItem(i).copy());
            }
        } else {
            this.clearContainer(player, this.inventory);
        }
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


    @Override
    public void slotChanged(AbstractContainerMenu containerToSend, int dataSlotIndex, ItemStack stack) {
        if(materialSlot.contains(getSlot(dataSlotIndex))) {
            this.checked = false;
            checkStateChange();
        }
    }

    public void checkItems(SpaceStationRecipe recipe) {
        this.checked = recipe.hasMaterials(this.materialSlot);
        checkStateChange();
    }

    public void checkStateChange() {
        if(Minecraft.getInstance().screen instanceof SpaceStationPlannerScreen screen) {
            screen.onCheckChange(this.checked);
        }
    }

    public void planStation(SpaceStationRecipe recipe) {

        if(this.getSlot(this.resultSlotId).hasItem()) {

            recipe.removeMaterials(this.materialSlot);

            ItemStack stack = this.getSlot(this.resultSlotId).getItem().copy();
            stack.set(DataComponentsRegistry.SPACE_STATION_BLUEPRINT.get(), recipe);
            this.inventory.setItem(0, stack);
            this.broadcastChanges();
        }
    }

    @Override
    public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {

    }
}
