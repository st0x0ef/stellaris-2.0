package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.blocks.entities.machines.FuelRefineryBlockEntity;
import org.exodusstudio.stellaris.common.menus.base.BaseContainer;
import org.exodusstudio.stellaris.common.menus.slot.ResultSlot;
import org.exodusstudio.stellaris.common.menus.slot.SpecificFluidContainerSlot;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class FuelRefineryMenu extends BaseContainer {

    private final Container container;
    private final FuelRefineryBlockEntity blockEntity;

    public static FuelRefineryMenu create(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        FuelRefineryBlockEntity blockEntity = (FuelRefineryBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos());
        return new FuelRefineryMenu(containerId, inventory, new SimpleContainer(6), blockEntity);
    }

    public FuelRefineryMenu(int containerId, Inventory inventory, Container container, FuelRefineryBlockEntity blockEntity) {
        super(MenuTypesRegistry.FUEL_REFINERY.get(), containerId, 6, inventory, 10, 142);
        this.container = container;
        this.blockEntity = blockEntity;

        // Ingredient tank
        addSlot(new SpecificFluidContainerSlot(container, FluidsRegistry.OIL_STILL.get(), 0, 14, 76, false));
        addSlot(new ResultSlot(container, 1, 14, 110));

        // Fuel tank
        addSlot(new SpecificFluidContainerSlot(container, FluidsRegistry.FUEL_STILL.get(), 2, 102, 76, false));
        addSlot(new ResultSlot(container, 3, 102, 110));

        // Diesel tank
        addSlot(new SpecificFluidContainerSlot(container, FluidsRegistry.DIESEL_STILL.get(), 4, 150, 76, false));
        addSlot(new ResultSlot(container, 5, 150, 110));
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    public FuelRefineryBlockEntity getBlockEntity() {
        return blockEntity;
    }
}
