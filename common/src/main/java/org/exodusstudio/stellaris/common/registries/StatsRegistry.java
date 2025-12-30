package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

public class StatsRegistry {

    public static final DeferredRegister<ResourceLocation> STATS = DeferredRegister.create(Stellaris.MOD_ID, Registries.CUSTOM_STAT);

    // region Test Stats
    //public static RegistrySupplier<ResourceLocation> SPACE_TRAVELED = register("space_traveled", (value) -> value + " km");
    //public static RegistrySupplier<ResourceLocation> SPACE_TRAVELED1 = register("space_traveled1", (value) -> value + " km");
    //public static RegistrySupplier<ResourceLocation> SPACE_TRAVELED2 = register("space_traveled2", (value) -> value + " km");
    //public static RegistrySupplier<ResourceLocation> SPACE_TRAVELED3 = register("space_traveled3", (value) -> value + " km");
    //public static RegistrySupplier<ResourceLocation> SPACE_TRAVELED4 = register("space_traveled4", (value) -> value + " km");
    //public static RegistrySupplier<ResourceLocation> SPACE_TRAVELED5 = register("space_traveled5", (value) -> value + " km");
    //public static RegistrySupplier<ResourceLocation> SPACE_TRAVELED6 = register("space_traveled6", (value) -> value + " km");
    //public static RegistrySupplier<ResourceLocation> SPACE_TRAVELED7 = register("space_traveled7", (value) -> value + " km");
    //public static RegistrySupplier<ResourceLocation> SPACE_TRAVELED8 = register("space_traveled8", (value) -> value + " km");
    // endregion

    public static RegistrySupplier<ResourceLocation> register(String key) {
        return register(key, StatFormatter.DEFAULT);
    }

    public static RegistrySupplier<ResourceLocation> register(String key, StatFormatter formatter) {
        ResourceLocation resourceLocation = ResourceLocationUtils.id(key);
        RegistrySupplier<ResourceLocation> supplier = STATS.register(key, () -> resourceLocation);
        //Stats.CUSTOM.get(resourceLocation, formatter); // TODO : find a way to make this working on neoforge
        return supplier;
    }

}
