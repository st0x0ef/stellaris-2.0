package org.exodusstudio.stellaris.common.registries;

import com.fej1fun.potentials.capabilities.Capabilities;

public class CapabilitiesRegistry {

    public static void init() {
        registerEnergyBlockEntities();
        registerEnergyItems();

        registerFluidBlockEntities();
        registerFluidItems();
    }

    private static void registerEnergyBlockEntities() {
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.SOLAR_PANEL);
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.COAL_GENERATOR);
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.DIESEL_GENERATOR);
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.CABLES);
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.POWER_BANKS);
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.VACUUMATOR);
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.ELECTROLYZER);
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.GRAVITY_MANIPULATOR);
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.OXYGEN_DISTRIBUTOR);
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.OXYGEN_PROPAGATOR);
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.FUEL_REFINERY);
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.PUMPJACK);
        Capabilities.Energy.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.CARGO_UNLOADER);
    }

    private static void registerEnergyItems() {
        Capabilities.Energy.ITEM.registerForItem(BlocksRegistry.POWER_BANK_T1.getAsItem());
        Capabilities.Energy.ITEM.registerForItem(ItemsRegistry.OIL_FINDER);

        Capabilities.Energy.ITEM.registerForItem(ItemsRegistry.SPACE_SUIT_HELMET);
    }

    private static void registerFluidBlockEntities() {
        Capabilities.Fluid.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.ELECTROLYZER);
        Capabilities.Fluid.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.DIESEL_GENERATOR);
        Capabilities.Fluid.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.FUEL_REFINERY);
        Capabilities.Fluid.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.PUMPJACK);
        Capabilities.Fluid.BLOCK.registerForBlockEntity(BlockEntitiesRegistry.PIPE_ENTITY);
    }

    private static void registerFluidItems() {
        Capabilities.Fluid.ITEM.registerForItem(ItemsRegistry.FLUID_CELL);

        Capabilities.Fluid.ITEM.registerForItem(ItemsRegistry.SPACE_SUIT_HELMET);
        Capabilities.Fluid.ITEM.registerForItem(ItemsRegistry.SPACE_SUIT_CHESTPLATE);
    }
}
