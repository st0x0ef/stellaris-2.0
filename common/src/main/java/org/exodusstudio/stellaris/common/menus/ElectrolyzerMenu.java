package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;
import org.exodusstudio.stellaris.common.blocks.entities.machines.ElectrolyzerBlockEntity;
import org.exodusstudio.stellaris.common.menus.base.BaseContainer;
import org.exodusstudio.stellaris.common.menus.slot.ElectrolyzeSlot;
import org.exodusstudio.stellaris.common.menus.slot.ResultSlot;
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

        addSlot(new ResultSlot(container, 0, 106, 113)); // Water tank output
        addSlot(new ElectrolyzeSlot(container, 1, 60, 113, blockEntity, -1)); // Water tank input
        addSlot(new ElectrolyzeSlot(container, 2, 20, 113, blockEntity, 0)); // Hydrogen tank output
        addSlot(new ElectrolyzeSlot(container,  3, 144, 113, blockEntity, 1)); // Oxygen tank output
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    public ElectrolyzerBlockEntity getBlockEntity() {
        return blockEntity;
    }
}