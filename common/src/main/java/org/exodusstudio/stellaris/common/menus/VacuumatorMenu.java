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
import net.minecraft.world.item.Items;
import org.exodusstudio.stellaris.common.blocks.entities.machines.VacuumatorBlockEntity;
import org.exodusstudio.stellaris.common.menus.slot.FoodSlot;
import org.exodusstudio.stellaris.common.menus.slot.ResultSlot;
import org.exodusstudio.stellaris.common.menus.slot.SpecificItemsSlot;
import org.exodusstudio.stellaris.common.menus.slot.SpecificTagsSlot;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.jetbrains.annotations.NotNull;

public class VacuumatorMenu extends AbstractContainerMenu {
    private final Container container;
    private VacuumatorBlockEntity entity;
    private final ContainerData data;

    public static VacuumatorMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        VacuumatorBlockEntity entity = (VacuumatorBlockEntity) inventory.player.level().getBlockEntity(data.readBlockPos());

        return new VacuumatorMenu(syncId, inventory, new SimpleContainer(5), entity, new SimpleContainerData(2));
    }

    public VacuumatorMenu(int syncId, Inventory inventory, Container container, VacuumatorBlockEntity entity, ContainerData containerData) {
        super(MenuTypesRegistry.VACUUMATOR.get(), syncId);

        checkContainerSize(container, 5);
        this.container = container;
        this.entity = entity;

        this.data = containerData;

        addSlot(new SpecificTagsSlot(container, 0, 56, 43, TagsRegistry.ItemTags.CAN));
        addSlot(new FoodSlot(container, 1, 82, 43));
        addSlot(new SpecificItemsSlot(container, 2, 108, 43, Items.GLASS_BOTTLE));

        addSlot(new ResultSlot(container, 3, 68, 69));
        addSlot(new ResultSlot(container, 4, 96, 69));

        addPlayerHotbar(inventory);
        addPlayerInventory(inventory);

        addDataSlots(containerData);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int invSlot) {
        return MenuQuickMoveHelper.quickMoveMachineFirst(this, player, invSlot, 5);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
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

    public VacuumatorBlockEntity getBlockEntity() {
        return this.entity;
    }

    public float getLitProgress() {
        int i = this.data.get(1);
        if (i == 0) {
            i = 200;
        }

        return Mth.clamp((float) this.data.get(0) / (float) i, 0.0F, 1.0F);
    }

    public boolean isLit() {
        return this.data.get(0) > 0;
    }
}
