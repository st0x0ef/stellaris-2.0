package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.commands.arguments.PlanetArgument;
import org.exodusstudio.stellaris.common.commands.arguments.PlanetArgumentInfo;

public class ArgumentsTypesRegistry {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create(Stellaris.MOD_ID, Registries.COMMAND_ARGUMENT_TYPE);

    public static final RegistrySupplier<PlanetArgumentInfo> PLANET = ARGUMENT_TYPES.register("planet", PlanetArgumentInfo::new);

    public static void init() {
        ARGUMENT_TYPES.register();

        PLANET.listen(info -> {
            ArgumentTypeInfos.BY_CLASS.put(PlanetArgument.class, info);
        });
    }
}
