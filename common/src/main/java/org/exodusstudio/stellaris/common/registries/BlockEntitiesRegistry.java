package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.entities.machines.CoalGeneratorBlockEntity;
import org.exodusstudio.stellaris.common.blocks.entities.machines.SolarPanelBlockEntity;

import java.util.Set;
import java.util.function.Supplier;

public class BlockEntitiesRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE = DeferredRegister.create(Stellaris.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final Supplier<BlockEntityType<?>> SOLAR_PANEL = BLOCK_ENTITY_TYPE.register("solar_panel",
            () -> new BlockEntityType<>(SolarPanelBlockEntity::new, Set.of(BlocksRegistry.SOLAR_PANEL.get())));
    public static final Supplier<BlockEntityType<?>> COAL_GENERATOR = BLOCK_ENTITY_TYPE.register("coal_generator",
            () -> new BlockEntityType<>(CoalGeneratorBlockEntity::new, Set.of(BlocksRegistry.COAL_GENERATOR.get())));

}
