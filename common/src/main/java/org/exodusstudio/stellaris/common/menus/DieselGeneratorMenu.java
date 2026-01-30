package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.blocks.entities.machines.DieselGeneratorBlockEntity;
import org.exodusstudio.stellaris.common.menus.base.BaseContainer;
import org.exodusstudio.stellaris.common.menus.slot.ResultSlot;
import org.exodusstudio.stellaris.common.menus.slot.SpecificFluidContainerSlot;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class DieselGeneratorMenu extends BaseContainer {

    private final Container container;
    private final DieselGeneratorBlockEntity blockEntity;

    public static DieselGeneratorMenu create(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        DieselGeneratorBlockEntity blockEntity = (DieselGeneratorBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos());
        return new DieselGeneratorMenu(containerId, inventory, new SimpleContainer(2), blockEntity);
    }

    public DieselGeneratorMenu(int containerId, Inventory inventory, Container container, DieselGeneratorBlockEntity blockEntity) {
        super(MenuTypesRegistry.DIESEL_GENERATOR.get(), containerId, 2, inventory, 10, 106);
        this.container = container;
        this.blockEntity = blockEntity;


        checkContainerSize(container, 2);

        addSlot(new SpecificFluidContainerSlot(container, FluidsRegistry.FLOWING_DIESEL.get(), 0, 42, 40, false));
        addSlot(new ResultSlot(container, 1, 42, 74));
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    public DieselGeneratorBlockEntity getBlockEntity() {
        return blockEntity;
    }
}
