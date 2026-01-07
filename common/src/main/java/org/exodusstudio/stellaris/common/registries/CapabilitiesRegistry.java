package org.exodusstudio.stellaris.common.registries;

import com.fej1fun.potentials.capabilities.Capabilities;

public class CapabilitiesRegistry {

    public static void init() {
        registerEnergyItems();
        registerEnergyBlockEntities();

        registerFluidItems();
    }

    private static void registerEnergyBlockEntities() {
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.SOLAR_PANEL);
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.COAL_GENERATOR);
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.CABLES);
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.POWER_BANKS);
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.VACUUMATOR);
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.ELECTROLYZER);
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.GRAVITY_MANIPULATOR);
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.OXYGEN_DISTRIBUTOR);
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.OXYGEN_PROPAGATOR);
    }

    private static void registerEnergyItems() {
        Capabilities.Energy.ITEM.registerForItem(BlocksRegistry.POWER_BANK_T1.getAsItem());
    }

    private static void registerFluidItems() {
        Capabilities.Fluid.ITEM.registerForItem(ItemsRegistry.FLUID_CELL);
    }
}
