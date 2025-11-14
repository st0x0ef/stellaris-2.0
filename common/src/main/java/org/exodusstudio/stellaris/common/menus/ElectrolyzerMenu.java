package org.exodusstudio.stellaris.common.menus;

import net.minecraft.world.level.material.Fluids;
import org.exodusstudio.stellaris.common.blocks.entities.machines.ElectrolyzerBlockEntity;
import org.exodusstudio.stellaris.common.menus.base.BaseContainer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.menus.slot.CustomResultSlot;
import org.exodusstudio.stellaris.common.menus.slot.SpecificFluidContainerSlot;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class ElectrolyzerMenu extends BaseContainer {

    private final Container container;
    private final ElectrolyzerBlockEntity blockEntity;

    public static ElectrolyzerMenu create(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        ElectrolyzerBlockEntity blockEntity = (ElectrolyzerBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos());
        return new ElectrolyzerMenu(containerId, inventory, new SimpleContainer(4), blockEntity);
    }

    public ElectrolyzerMenu(int containerId, Inventory inventory, Container container, ElectrolyzerBlockEntity blockEntity) {
        super(MenuTypesRegistry.ELECTROLYZER.get(), containerId, 4, inventory, 10, 142);
        this.container = container;
        this.blockEntity = blockEntity;

        addSlot(new CustomResultSlot(container, 0, 106, 113)); // Water tank output
        addSlot(new SpecificFluidContainerSlot(container, blockEntity.ingredientTank.getFluidInTank(0).getFluid(), 1, 60, 113, false)); // Water tank input
        addSlot(new SpecificFluidContainerSlot(container, blockEntity.resultTanks.getFluidInTank(0).getFluid(), 2, 20, 113, true)); // Hydrogen tank output
        addSlot(new SpecificFluidContainerSlot(container, blockEntity.resultTanks.getFluidInTank(1).getFluid(), 3, 144, 113, true)); // Oxygen tank output
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    public ElectrolyzerBlockEntity getBlockEntity() {
        return blockEntity;
    }
}