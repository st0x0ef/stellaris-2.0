package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.blocks.entities.machines.BlenderBlockEntity;
import org.exodusstudio.stellaris.common.menus.slot.ResultSlot;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;
import org.jetbrains.annotations.NotNull;

public class BlenderMenu extends AbstractContainerMenu {
    private static final int GRID_X = 34;
    private static final int GRID_Y = 40;
    private static final int SLOT_SIZE = 18;

    private static final int RESULT_X = 126;
    private static final int RESULT_Y = 58;

    public static final int BLEND_BUTTON = 0;

    private final Container container;
    private final BlenderBlockEntity entity;
    private final ContainerData data;

    public static BlenderMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        BlenderBlockEntity entity = (BlenderBlockEntity) inventory.player.level().getBlockEntity(data.readBlockPos());

        return new BlenderMenu(syncId, inventory, new SimpleContainer(BlenderBlockEntity.CONTAINER_SIZE), entity,
                new SimpleContainerData(3));
    }

    public BlenderMenu(int syncId, Inventory inventory, Container container, BlenderBlockEntity entity, ContainerData containerData) {
        super(MenuTypesRegistry.BLENDER.get(), syncId);

        checkContainerSize(container, BlenderBlockEntity.CONTAINER_SIZE);
        this.container = container;
        this.entity = entity;
        this.data = containerData;

        for (int row = 0; row < BlenderBlockEntity.GRID_HEIGHT; row++) {
            for (int column = 0; column < BlenderBlockEntity.GRID_WIDTH; column++) {
                addSlot(new Slot(container, row * BlenderBlockEntity.GRID_WIDTH + column,
                        GRID_X + column * SLOT_SIZE, GRID_Y + row * SLOT_SIZE));
            }
        }

        addSlot(new ResultSlot(container, BlenderBlockEntity.RESULT_SLOT, RESULT_X, RESULT_Y));

        addPlayerHotbar(inventory);
        addPlayerInventory(inventory);

        addDataSlots(containerData);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int invSlot) {
        return MenuQuickMoveHelper.quickMoveMachineFirst(this, player, invSlot, BlenderBlockEntity.CONTAINER_SIZE);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id != BLEND_BUTTON || this.entity == null) {
            return false;
        }

        this.entity.requestBlend();
        return true;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 10 + l * 18, (95 + i * 18) + 11));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 10 + i * 18, 164));
        }
    }

    public BlenderBlockEntity getBlockEntity() {
        return this.entity;
    }

    public float getBlendProgress() {
        return Mth.clamp((float) this.data.get(0) / (float) BlenderBlockEntity.PROGRESS_SCALE, 0.0F, 1.0F);
    }

    public boolean isBlending() {
        return this.data.get(1) > 0;
    }

    public boolean canBlend() {
        return this.data.get(2) > 0;
    }
}
