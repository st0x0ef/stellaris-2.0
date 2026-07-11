package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.world.processor.VoidProcessor;

public class ProcessorsRegistry {

    public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSORS =
            DeferredRegister.create(Stellaris.MOD_ID, Registries.STRUCTURE_PROCESSOR);

    public static final RegistrySupplier<StructureProcessorType<VoidProcessor>> STRUCTURE_VOID_PROCESSOR =
            STRUCTURE_PROCESSORS.register("structure_void_processor", () -> () -> VoidProcessor.CODEC);
}
