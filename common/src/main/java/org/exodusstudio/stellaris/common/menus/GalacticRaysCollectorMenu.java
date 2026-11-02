package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.NonInteractiveResultSlot;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GalacticRaysCollectorBlockEntity;
import org.exodusstudio.stellaris.common.menus.base.BaseContainer;
import org.exodusstudio.stellaris.common.menus.base.GeneratedEnergyDataSlot;
import org.exodusstudio.stellaris.common.menus.slot.ComponentsSlot;
import org.exodusstudio.stellaris.common.menus.slot.ResultSlot;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class GalacticRaysCollectorMenu extends BaseContainer {

    private final Container inventory;
    private final GalacticRaysCollectorBlockEntity entity;
    private final GeneratedEnergyDataSlot generatedEnergy;

    public final static int CONTAINER_SIZE = 3;

    public static GalacticRaysCollectorMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        GalacticRaysCollectorBlockEntity entity = (GalacticRaysCollectorBlockEntity) inventory.player.level().getBlockEntity(data.readBlockPos());

        return new GalacticRaysCollectorMenu(syncId, inventory, new SimpleContainer(CONTAINER_SIZE), entity);
    }

    public GalacticRaysCollectorMenu(int syncId, Inventory playerInventory, Container container, GalacticRaysCollectorBlockEntity entity) {
        super(MenuTypesRegistry.SOLAR_RADIATION_CAPTER.get(), syncId, CONTAINER_SIZE, playerInventory, 10, 106);

        checkContainerSize(container, CONTAINER_SIZE);
        this.inventory = container;
        this.entity = entity;

        this.addSlot(new ComponentsSlot(inventory, 0, 50, 54, DataComponentsRegistry.RADIATION_HOLDER.get()));
        this.addSlot(new NonInteractiveResultSlot(inventory, 1, 68, 54));
        this.addSlot(new ResultSlot(inventory, 2, 86, 54));

        this.generatedEnergy = playerInventory.player.level().isClientSide() || entity == null ? GeneratedEnergyDataSlot.client() : GeneratedEnergyDataSlot.server(entity);
        addDataSlot(generatedEnergy);
    }

    public GalacticRaysCollectorBlockEntity getBlockEntity() {
        return entity;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }


    public int getGeneratedEnergy() {
        return generatedEnergy.get();
    }
}
