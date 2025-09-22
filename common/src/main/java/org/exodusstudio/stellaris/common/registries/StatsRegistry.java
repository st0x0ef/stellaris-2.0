package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

public class StatsRegistry {

    public static final DeferredRegister<ResourceLocation> STATS = DeferredRegister.create(Stellaris.MOD_ID, Registries.CUSTOM_STAT);

    public static RegistrySupplier<ResourceLocation> SPACE_TRAVEL = register("space_traveled", (value) -> value + " km");

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
