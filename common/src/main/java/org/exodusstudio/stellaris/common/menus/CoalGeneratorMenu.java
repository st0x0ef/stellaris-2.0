package org.exodusstudio.stellaris.common.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import org.exodusstudio.stellaris.common.blocks.entities.machines.CoalGeneratorBlockEntity;
import org.exodusstudio.stellaris.common.menus.base.BaseContainer;
import org.exodusstudio.stellaris.common.menus.base.GeneratedEnergyDataSlot;
import org.exodusstudio.stellaris.common.menus.slot.CoalGeneratorSlot;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;

public class CoalGeneratorMenu extends BaseContainer {

    private final Container inventory;
    private final CoalGeneratorBlockEntity entity;
    private final ContainerData data;
    private final GeneratedEnergyDataSlot generatedEnergy;


    public static CoalGeneratorMenu create(int syncId, Inventory inventory, FriendlyByteBuf data) {
        CoalGeneratorBlockEntity entity = (CoalGeneratorBlockEntity) inventory.player.level().getBlockEntity(data.readBlockPos());

        return new CoalGeneratorMenu(syncId, inventory, new SimpleContainer(1), entity, new SimpleContainerData(2));
    }

    public CoalGeneratorMenu(int syncId, Inventory playerInventory, Container container, CoalGeneratorBlockEntity entity, ContainerData containerData) {
        super(MenuTypesRegistry.COAL_GENERATOR.get(), syncId, 1, playerInventory, 10, 106);

        checkContainerSize(container, 1);
        this.inventory = container;
        this.entity = entity;
        this.data = containerData;

        this.addSlot(new CoalGeneratorSlot(inventory, 0, 68, 54));

        addDataSlots(containerData);
        this.generatedEnergy = playerInventory.player.level().isClientSide() || entity == null ? GeneratedEnergyDataSlot.client() : GeneratedEnergyDataSlot.server(entity);
        addDataSlot(generatedEnergy);
    }

    public CoalGeneratorBlockEntity getBlockEntity() {
        return entity;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }


    public float getLitProgress() {
        int i = this.data.get(1);
        if (i == 0) {
            i = 200;
        }

        return Mth.clamp((float) this.data.get(0) / (float) i, 0.0F, 1.0F);
    }

    public boolean isLit() {
        return this.data.get(0) > 0;
    }

    public int getGeneratedEnergy() {
        return generatedEnergy.get();
    }
}
