package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import org.exodusstudio.stellaris.common.menus.base.BaseContainer;
import org.exodusstudio.stellaris.common.menus.slot.CoalGeneratorSlot;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class SDCardReaderApplicationMenu extends BaseContainer {

    private final Container inventory;
    private final ContainerData data;

    public static SDCardReaderApplicationMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        return new SDCardReaderApplicationMenu(syncId, inventory, new SimpleContainer(1), new SimpleContainerData(2));
    }

    public SDCardReaderApplicationMenu(int syncId, Inventory playerInventory, Container container, ContainerData containerData) {
        super(MenuTypesRegistry.COAL_GENERATOR_MENU.get(), syncId, 1, playerInventory, 10, 106);

        checkContainerSize(container, 1);
        this.inventory = container;
        this.data = containerData;

        this.addSlot(new CoalGeneratorSlot(inventory, 0, 68, 54));

        addDataSlots(containerData);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

}
