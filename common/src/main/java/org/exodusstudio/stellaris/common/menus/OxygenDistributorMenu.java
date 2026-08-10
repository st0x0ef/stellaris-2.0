package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.blocks.entities.machines.OxygenDistributorBlockEntity;
import org.exodusstudio.stellaris.common.menus.base.BaseContainer;
import org.exodusstudio.stellaris.common.menus.slot.FluidContainerSlot;
import org.exodusstudio.stellaris.common.menus.slot.SpecificFluidContainerSlot;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class OxygenDistributorMenu extends BaseContainer {

    private final Container inventory;
    private final OxygenDistributorBlockEntity entity;


    public static OxygenDistributorMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        OxygenDistributorBlockEntity entity = (OxygenDistributorBlockEntity) inventory.player.level().getBlockEntity(data.readBlockPos());

        return new OxygenDistributorMenu(syncId, inventory, new SimpleContainer(2), entity);
    }

    public OxygenDistributorMenu(int syncId, Inventory playerInventory, Container container, OxygenDistributorBlockEntity entity) {
        super(MenuTypesRegistry.OXYGEN_DISTRIBUTOR.get(), syncId, 2, playerInventory, 10, 106);

        checkContainerSize(container, 2);
        this.inventory = container;
        this.entity = entity;

        this.addSlot(new SpecificFluidContainerSlot(inventory, FluidsRegistry.OXYGEN_STILL.get(), OxygenDistributorBlockEntity.INPUT_SLOT, 48, 40, false));
        this.addSlot(new FluidContainerSlot(inventory, OxygenDistributorBlockEntity.OUTPUT_SLOT, 48, 74, true));
    }

    public OxygenDistributorBlockEntity getBlockEntity() {
        return entity;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }
}
