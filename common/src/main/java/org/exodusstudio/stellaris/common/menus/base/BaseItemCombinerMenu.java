package org.exodusstudio.stellaris.common.menus.base;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Base class for item combiner menus.
 * Handles input and output slots, inventory management, and interaction validation.
 * Made by @Fej
 */
public abstract class BaseItemCombinerMenu extends AbstractContainerMenu {

    protected final ContainerLevelAccess access;
    protected final Player player;
    protected final Container inputSlots;
    private final List<ItemCombinerMenuSlotDefinition.SlotDefinition> inputSlotIndexes;
    protected final ResultContainer resultSlots = new ResultContainer();
    private final int resultSlotIndex;

    protected abstract boolean mayPickup(Player player, boolean hasStack);

    protected abstract void onTake(Player player, ItemStack stack);

    protected abstract boolean isValidBlock(BlockState state);

    public BaseItemCombinerMenu(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(type, containerId);
        this.access = access;
        this.player = playerInventory.player;

        ItemCombinerMenuSlotDefinition itemCombinerMenuSlotDefinition = this.createInputSlotDefinitions();
        this.inputSlots = this.createContainer(itemCombinerMenuSlotDefinition.getNumOfInputSlots());
        this.inputSlotIndexes = itemCombinerMenuSlotDefinition.getSlots();
        this.resultSlotIndex = itemCombinerMenuSlotDefinition.getResultSlotIndex();
        this.createInputSlots(itemCombinerMenuSlotDefinition);
        this.createResultSlot(itemCombinerMenuSlotDefinition);
        this.createInventorySlots(playerInventory);
    }

    private void createInputSlots(ItemCombinerMenuSlotDefinition slotDefinition) {
        for (final ItemCombinerMenuSlotDefinition.SlotDefinition slotDefinition2 : slotDefinition.getSlots()) {
            this.addSlot(new Slot(this.inputSlots, slotDefinition2.slotIndex(), slotDefinition2.x(), slotDefinition2.y()) {

                public boolean mayPlace(ItemStack stack) {
                    return slotDefinition2.mayPlace().test(stack);
                }
            });
        }

    }

    private void createResultSlot(ItemCombinerMenuSlotDefinition slotDefinition) {
        this.addSlot(new Slot(this.resultSlots, slotDefinition.getResultSlot().slotIndex(), slotDefinition.getResultSlot().x(), slotDefinition.getResultSlot().y()) {

            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            public boolean mayPickup(Player player) {
                return BaseItemCombinerMenu.this.mayPickup(player, this.hasItem());
            }

            public void onTake(Player player, ItemStack stack) {
                BaseItemCombinerMenu.this.onTake(player, stack);
            }
        });
    }

    private void createInventorySlots(Inventory inventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 10 + j * 18, 106 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 10 + i * 18, 164));
        }
    }

    public abstract void createResult();

    protected abstract ItemCombinerMenuSlotDefinition createInputSlotDefinitions();

    private SimpleContainer createContainer(int size) {
        return new SimpleContainer(size) {

            public void setChanged() {
                super.setChanged();
                BaseItemCombinerMenu.this.slotsChanged(this);
            }
        };
    }

    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (container == this.inputSlots) {
            this.createResult();
        }

    }

    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, blockPos) -> this.clearContainer(player, this.inputSlots));
    }

    public boolean stillValid(Player player) {
        return this.access.evaluate((level, blockPos) -> this.isValidBlock(level.getBlockState(blockPos)) && player.isWithinBlockInteractionRange(blockPos, 4.0F), true);
    }

    private boolean isInputSlot(int index) {
        for (ItemCombinerMenuSlotDefinition.SlotDefinition slotDefinition : this.inputSlotIndexes) {
            if (slotDefinition.slotIndex() == index) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= this.slots.size()) {
            return ItemStack.EMPTY;
        }

        ItemStack copiedStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack movingStack = slot.getItem();
            copiedStack = movingStack.copy();

            int playerInventoryStart = this.getInventorySlotStart();
            int playerInventoryEnd = this.getInventorySlotEnd();
            int hotbarStart = this.getUseRowStart();
            int hotbarEnd = this.getUseRowEnd();

            if (index == this.getResultSlot()) {
                if (!this.moveItemStackTo(movingStack, playerInventoryStart, hotbarEnd, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(movingStack, copiedStack);
            } else if (this.isInputSlot(index)) {
                if (!this.moveItemStackTo(movingStack, playerInventoryStart, hotbarEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= playerInventoryStart && index < hotbarEnd) {
                if (this.canMoveIntoInputSlots(movingStack)) {
                    int targetSlot = this.getSlotToQuickMoveTo(movingStack);

                    if (!this.moveItemStackTo(movingStack, targetSlot, this.getResultSlot(), false)) {
                        if (index >= playerInventoryStart && index < playerInventoryEnd) {
                            if (!this.moveItemStackTo(movingStack, hotbarStart, hotbarEnd, false)) {
                                return ItemStack.EMPTY;
                            }
                        } else if (index >= hotbarStart && index < hotbarEnd) {
                            if (!this.moveItemStackTo(movingStack, playerInventoryStart, playerInventoryEnd, false)) {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                } else if (index >= playerInventoryStart && index < playerInventoryEnd) {
                    if (!this.moveItemStackTo(movingStack, hotbarStart, hotbarEnd, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= hotbarStart && index < hotbarEnd) {
                    if (!this.moveItemStackTo(movingStack, playerInventoryStart, playerInventoryEnd, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (movingStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (movingStack.getCount() == copiedStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, movingStack);
        }

        return copiedStack;
    }

    protected boolean canMoveIntoInputSlots(ItemStack stack) {
        return true;
    }

    public int getSlotToQuickMoveTo(ItemStack stack) {
        return this.inputSlotIndexes.isEmpty() ? 0 : this.inputSlotIndexes.getFirst().slotIndex();
    }

    public int getResultSlot() {
        return this.resultSlotIndex;
    }

    private int getInventorySlotStart() {
        return this.getResultSlot() + 1;
    }

    private int getInventorySlotEnd() {
        return this.getInventorySlotStart() + 27;
    }

    private int getUseRowStart() {
        return this.getInventorySlotEnd();
    }

    private int getUseRowEnd() {
        return this.getUseRowStart() + 9;
    }
}
