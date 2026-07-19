package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.entities.*;
import org.exodusstudio.stellaris.common.blocks.entities.machines.*;

import java.util.Set;
import java.util.function.Supplier;

public class BlockEntitiesRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE = DeferredRegister.create(Stellaris.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    /** MACHINES */
    public static final Supplier<BlockEntityType<?>> SOLAR_PANEL = BLOCK_ENTITY_TYPE.register("solar_panel",
            () -> new BlockEntityType<>(SolarPanelBlockEntity::new, Set.of(BlocksRegistry.SOLAR_PANEL.block().get())));
    public static final Supplier<BlockEntityType<?>> COAL_GENERATOR = BLOCK_ENTITY_TYPE.register("coal_generator",
            () -> new BlockEntityType<>(CoalGeneratorBlockEntity::new, Set.of(BlocksRegistry.COAL_GENERATOR.block().get())));
    public static final Supplier<BlockEntityType<?>> DIESEL_GENERATOR = BLOCK_ENTITY_TYPE.register("diesel_generator",
            () -> new BlockEntityType<>(DieselGeneratorBlockEntity::new, Set.of(BlocksRegistry.DIESEL_GENERATOR.block().get())));

    public static final Supplier<BlockEntityType<?>> VACUUMATOR = BLOCK_ENTITY_TYPE.register("vacuumator",
            () -> new BlockEntityType<>(VacuumatorBlockEntity::new, Set.of(BlocksRegistry.VACUUMATOR.block().get())));

    public static final Supplier<BlockEntityType<?>> PUMPJACK = BLOCK_ENTITY_TYPE.register("pumpjack",
            () -> new BlockEntityType<>(PumpjackBlockEntity::new, Set.of(BlocksRegistry.PUMPJACK.block().get())));
    public static final Supplier<BlockEntityType<?>> FUEL_REFINERY = BLOCK_ENTITY_TYPE.register("fuel_refinery",
            () -> new BlockEntityType<>(FuelRefineryBlockEntity::new, Set.of(BlocksRegistry.FUEL_REFINERY.block().get())));

    public static final Supplier<BlockEntityType<?>> WATER_PUMP = BLOCK_ENTITY_TYPE.register("water_pump",
            () -> new BlockEntityType<>(WaterPumpBlockEntity::new, Set.of(BlocksRegistry.WATER_PUMP.block().get())));

    public static final Supplier<BlockEntityType<?>> CARGO_UNLOADER = BLOCK_ENTITY_TYPE.register("cargo_unloader",
            () -> new BlockEntityType<>(CargoUnloaderBlockEntity::new, Set.of(BlocksRegistry.CARGO_UNLOADER.block().get())));

    public static final Supplier<BlockEntityType<?>> POWER_BANKS = BLOCK_ENTITY_TYPE.register("power_bank",
            () -> new BlockEntityType<>(PowerBankBlockEntity::new, Set.of(BlocksRegistry.POWER_BANK_T1.block().get())));

    public static final Supplier<BlockEntityType<?>> CABLES = BLOCK_ENTITY_TYPE.register("cables",
            () -> new BlockEntityType<>(CableBlockEntity::new, Set.of(BlocksRegistry.CABLE_T1.block().get())));

    public static final Supplier<BlockEntityType<?>> ELECTROLYZER = BLOCK_ENTITY_TYPE.register("electrolyzer",
            () -> new BlockEntityType<>(ElectrolyzerBlockEntity::new, Set.of(BlocksRegistry.ELECTROLYZER.block().get())));

    public static final Supplier<BlockEntityType<?>> ENGINEERING_STATION = BLOCK_ENTITY_TYPE.register("engineering_station",
            () -> new BlockEntityType<>(EngineeringStationBlockEntity::new, Set.of(BlocksRegistry.ENGINEERING_STATION.block().get())));

    public static final Supplier<BlockEntityType<?>> GRAVITY_MANIPULATOR = BLOCK_ENTITY_TYPE.register("gravity_manipulator",
            () -> new BlockEntityType<>(GravityManipulatorBlockEntity::new, Set.of(BlocksRegistry.GRAVITY_MANIPULATOR.block().get())));

    public static final Supplier<BlockEntityType<FlagBlockEntity>> FLAG = BLOCK_ENTITY_TYPE.register("flag",
            () -> new BlockEntityType<>(FlagBlockEntity::new, Set.of(BlocksRegistry.FLAG.block().get())));

    public static final Supplier<BlockEntityType<GlobeBlockEntity>> GLOBE = BLOCK_ENTITY_TYPE.register("globe",
            () -> new BlockEntityType<>(GlobeBlockEntity::new, Set.of(BlocksRegistry.EARTH_GLOBE.block().get(), BlocksRegistry.MOON_GLOBE.block().get())));

    public static final Supplier<BlockEntityType<ModSignBlockEntity>> MOD_SIGN = BLOCK_ENTITY_TYPE.register("sign",
            () -> new BlockEntityType<>(ModSignBlockEntity::new, Set.of(BlocksRegistry.LUNAR_SIGN.get(), BlocksRegistry.LUNAR_WALL_SIGN.get())));

    public static final Supplier<BlockEntityType<ModHangingSignBlockEntity>> MOD_HANGING_SIGN = BLOCK_ENTITY_TYPE.register("hanging_sign",
            () -> new BlockEntityType<>(ModHangingSignBlockEntity::new, Set.of(BlocksRegistry.LUNAR_HANGING_SIGN.get(), BlocksRegistry.LUNAR_WALL_HANGING_SIGN.get())));

    public static final Supplier<BlockEntityType<?>> OXYGEN_DISTRIBUTOR = BLOCK_ENTITY_TYPE.register("oxygen_distributor",
            () -> new BlockEntityType<>(OxygenDistributorBlockEntity::new, Set.of(BlocksRegistry.OXYGEN_DISTRIBUTOR.block().get())));

    public static final Supplier<BlockEntityType<?>> OXYGEN_PROPAGATOR = BLOCK_ENTITY_TYPE.register("oxygen_propagator",
            () -> new BlockEntityType<>(OxygenPropagatorBlockEntity::new, Set.of(BlocksRegistry.OXYGEN_PROPAGATOR.block().get())));

    public static final Supplier<BlockEntityType<?>> PIPE_ENTITY = BLOCK_ENTITY_TYPE.register("pipe",
            () -> new BlockEntityType<>(PipeBlockEntity::create, Set.of(BlocksRegistry.T1_PIPE.block().get())));

    public static final Supplier<BlockEntityType<?>> FLUID_TANK = BLOCK_ENTITY_TYPE.register("fluid_tank",
            () -> new BlockEntityType<>(FluidTankBlockEntity::new, Set.of(BlocksRegistry.FLUID_TANK_T1.block().get())));

    public static final Supplier<BlockEntityType<?>> ANTENNA = BLOCK_ENTITY_TYPE.register("antenna",
            () -> new BlockEntityType<>(AntennaBlockEntity::new, Set.of(BlocksRegistry.ANTENNA.block().get())));

    public static final Supplier<BlockEntityType<?>> LABORATORY = BLOCK_ENTITY_TYPE.register("laboratory",
            () -> new BlockEntityType<>(LaboratoryBlockEntity::new, Set.of(BlocksRegistry.LABORATORY.block().get())));

    public static final Supplier<BlockEntityType<?>> ROCKET_LAUNCH_PAD = BLOCK_ENTITY_TYPE.register("rocket_launch_pad",
            () -> new BlockEntityType<>(RocketLaunchPadBlockEntity::new, Set.of(BlocksRegistry.ROCKET_LAUNCH_PAD.block().get())));

}
