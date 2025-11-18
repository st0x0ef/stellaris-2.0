package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.blocks.entities.machines.SolarPanelBlockEntity;
import org.exodusstudio.stellaris.common.menus.base.BaseContainer;
import org.exodusstudio.stellaris.common.menus.slot.EnergySlot;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class SolarPanelMenu extends BaseContainer {

    private final Container inventory;
    private final SolarPanelBlockEntity blockEntity;

    public static SolarPanelMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        SolarPanelBlockEntity entity = (SolarPanelBlockEntity) inventory.player.level().getBlockEntity(data.readBlockPos());
        return new SolarPanelMenu(syncId, inventory, new SimpleContainer(1), entity);
    }

    public SolarPanelMenu(int syncId, Inventory playerInventory, Container container, SolarPanelBlockEntity entity) {
        super(MenuTypesRegistry.SOLAR_PANEL.get(), syncId, 1, playerInventory, 10, 106);

        checkContainerSize(container, 1);
        this.inventory = container;
        this.blockEntity = entity;

        addSlot(new EnergySlot(inventory, 0, 82, 56));
    }

    public SolarPanelBlockEntity getBlockEntity() {
        return blockEntity;
    }


    @Override
    public boolean stillValid(Player player) {
        return inventory.stillValid(player);
    }
}
