package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.blocks.entities.machines.PumpjackBlockEntity;
import org.exodusstudio.stellaris.common.menus.base.BaseContainer;
import org.exodusstudio.stellaris.common.menus.slot.ResultSlot;
import org.exodusstudio.stellaris.common.menus.slot.SpecificFluidContainerSlot;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class PumpjackMenu extends BaseContainer {

    private final Container container;
    private final PumpjackBlockEntity blockEntity;

    public static PumpjackMenu create(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        PumpjackBlockEntity blockEntity = (PumpjackBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos());
        return new PumpjackMenu(containerId, inventory, new SimpleContainer(2), blockEntity);
    }

    public PumpjackMenu(int containerId, Inventory inventory, Container container, PumpjackBlockEntity blockEntity) {
        super(MenuTypesRegistry.PUMPJACK.get(), containerId, 2, inventory, 10, 106);
        this.container = container;
        this.blockEntity = blockEntity;
        checkContainerSize(container, 2);

        // Result tank
        addSlot(new SpecificFluidContainerSlot(container, FluidsRegistry.OIL_STILL.get() ,0, 136, 44, false));
        addSlot(new ResultSlot(container, 1, 136, 78));
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    public PumpjackBlockEntity getBlockEntity() {
        return blockEntity;
    }
}
