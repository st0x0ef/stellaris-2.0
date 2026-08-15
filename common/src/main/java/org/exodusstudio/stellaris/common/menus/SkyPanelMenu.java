package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.blocks.entities.machines.SkyPanelBlockEntity;
import org.exodusstudio.stellaris.common.menus.base.BaseContainer;
import org.exodusstudio.stellaris.common.menus.slot.EnergySlot;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class SkyPanelMenu extends BaseContainer {

    private final Container inventory;
    private final SkyPanelBlockEntity blockEntity;

    public static SkyPanelMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        SkyPanelBlockEntity entity = (SkyPanelBlockEntity) inventory.player.level().getBlockEntity(data.readBlockPos());
        return new SkyPanelMenu(syncId, inventory, new SimpleContainer(1), entity);
    }

    public SkyPanelMenu(int syncId, Inventory playerInventory, Container container, SkyPanelBlockEntity entity) {
        super(MenuTypesRegistry.SKY_PANEL.get(), syncId, 1, playerInventory, 10, 106);

        checkContainerSize(container, 1);
        this.inventory = container;
        this.blockEntity = entity;

        addSlot(new EnergySlot(inventory, 0, 82, 56));
    }

    public SkyPanelBlockEntity getBlockEntity() {
        return blockEntity;
    }


    @Override
    public boolean stillValid(Player player) {
        return inventory.stillValid(player);
    }
}
