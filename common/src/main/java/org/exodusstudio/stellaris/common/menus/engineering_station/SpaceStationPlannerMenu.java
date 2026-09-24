package org.exodusstudio.stellaris.common.menus.engineering_station;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import org.exodusstudio.stellaris.common.menus.slot.ResultSlot;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;
import org.jetbrains.annotations.Nullable;

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

    private List<ItemStack> materialSnapshot = List.of();

    public static SpaceStationPlannerMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        BlockPos pos = data.readBlockPos();
        EngineeringStationBlockEntity be = (EngineeringStationBlockEntity) inventory.player.level().getBlockEntity(pos);
        return new SpaceStationPlannerMenu(syncId, inventory, new SimpleContainer(10), pos, be);
    }

    public SpaceStationPlannerMenu(int syncId, Inventory playerInventory, Container container, BlockPos pos, EngineeringStationBlockEntity blockEntity) {
        super(MenuTypesRegistry.SPACE_STATION_PLANNER.get(), syncId, 10, playerInventory, 10, 142);

        checkContainerSize(container, 10);
        this.engineeringStationPos = pos;
        this.blockEntity = blockEntity;
        this.inventory = container;
        this.player = playerInventory.player;
        // Output only - the blueprint to be planned goes in the grid with the materials.
        this.resultSlotId = this.addSlot(new ResultSlot(this.inventory, 0, 122, 56)).index;
        addMaterialsSlots(30, 48);
        this.addSlotListener(this);

        if (blockEntity != null) {
            blockEntity.restoreSpaceStationPlannerItems(this.player, this.inventory);
        }
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
        if (blockEntity == null) {
            this.clearContainer(player, this.inventory);
        } else if (blockEntity.isTabSwitching()) {
            blockEntity.stashSpaceStationPlannerItems(player, this.inventory);
        } else {
            this.clearContainer(player, this.inventory);
            this.clearContainer(player, blockEntity.takeStashedItems(player));
        }
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

    /**
     * Reports whether the material grid changed since the last call. A menu's ContainerListener
     * only ever runs on the server, so the screen polls this rather than waiting to be told.
     */
    public boolean pollMaterialsChanged() {
        List<ItemStack> current = new ArrayList<>(this.materialSlot.size());
        for (Slot slot : this.materialSlot) {
            current.add(slot.getItem().copy());
        }

        boolean changed = current.size() != this.materialSnapshot.size();
        for (int i = 0; !changed && i < current.size(); i++) {
            changed = !ItemStack.matches(current.get(i), this.materialSnapshot.get(i));
        }

        this.materialSnapshot = current;
        return changed;
    }

    public void checkItems(SpaceStationRecipe recipe) {
        this.checked = findBlueprintSlot() != null && recipe.hasMaterials(this.materialSlot);
        checkStateChange();
    }

    public void checkStateChange() {
        if (!this.player.level().isClientSide()) {
            return;
        }

        if (Minecraft.getInstance().screen instanceof SpaceStationPlannerScreen screen) {
            screen.onCheckChange(this.checked);
        }
    }

    public void planStation(SpaceStationRecipe recipe) {
        Slot blueprintSlot = findBlueprintSlot();
        if (blueprintSlot == null) {
            // Silence here reads as a dead button, so say what is missing.
            this.player.sendSystemMessage(Component.translatable("message.stellaris.space_station.planner_no_blueprint"));
            return;
        }

        ItemStack stamped = blueprintSlot.getItem().copyWithCount(1);
        stamped.set(DataComponentsRegistry.SPACE_STATION_BLUEPRINT.get(), recipe);

        Slot resultSlot = this.getSlot(this.resultSlotId);
        ItemStack currentResult = resultSlot.getItem();
        boolean stacksOntoResult = !currentResult.isEmpty()
                && ItemStack.isSameItemSameComponents(currentResult, stamped)
                && currentResult.getCount() < Math.min(resultSlot.getMaxStackSize(), stamped.getMaxStackSize());

        // Check there is somewhere to put the result before spending anything.
        if (!currentResult.isEmpty() && !stacksOntoResult) {
            this.player.sendSystemMessage(Component.translatable("message.stellaris.space_station.result_blocked"));
            return;
        }

        if (!recipe.removeMaterials(this.materialSlot)) {
            this.player.sendSystemMessage(Component.translatable("message.stellaris.space_station.missing_materials"));
            return;
        }

        blueprintSlot.remove(1);

        if (currentResult.isEmpty()) {
            resultSlot.set(stamped);
        } else {
            currentResult.grow(1);
            resultSlot.setChanged();
        }

        this.broadcastChanges();
    }

    /**
     * The blueprint shares the 3x3 grid with the eight materials, so it can sit in any of the nine
     * slots. A recipe never asks for a blueprint itself, so the materials pass leaves it alone.
     */
    @Nullable
    private Slot findBlueprintSlot() {
        for (Slot slot : this.materialSlot) {
            if (slot.getItem().is(ItemsRegistry.SPACE_STATION_BLUEPRINT.get())) {
                return slot;
            }
        }
        return null;
    }

    @Override
    public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {

    }
}
