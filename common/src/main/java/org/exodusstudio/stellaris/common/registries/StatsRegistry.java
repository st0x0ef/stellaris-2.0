package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class StatsRegistry {

    public static final DeferredRegister<Identifier> STATS = DeferredRegister.create(Stellaris.MOD_ID, Registries.CUSTOM_STAT);

    // region Test Stats

    public static RegistrySupplier<Identifier> SPACE_TRAVELED = register("space_traveled", (value) -> value + " km");
    public static RegistrySupplier<Identifier> SD_CARD_READ = register("sd_card_read");

    //public static RegistrySupplier<Identifier> SPACE_TRAVELED2 = register("space_traveled2", (value) -> value + " km");
    //public static RegistrySupplier<Identifier> SPACE_TRAVELED3 = register("space_traveled3", (value) -> value + " km");
    //public static RegistrySupplier<Identifier> SPACE_TRAVELED4 = register("space_traveled4", (value) -> value + " km");
    //public static RegistrySupplier<Identifier> SPACE_TRAVELED5 = register("space_traveled5", (value) -> value + " km");
    //public static RegistrySupplier<Identifier> SPACE_TRAVELED6 = register("space_traveled6", (value) -> value + " km");
    //public static RegistrySupplier<Identifier> SPACE_TRAVELED7 = register("space_traveled7", (value) -> value + " km");
    //public static RegistrySupplier<Identifier> SPACE_TRAVELED8 = register("space_traveled8", (value) -> value + " km");
    // endregion

    public static RegistrySupplier<Identifier> register(String key) {
        return register(key, StatFormatter.DEFAULT);
    }

    public static RegistrySupplier<Identifier> register(String key, StatFormatter formatter) {
        Identifier Identifier = IdentifierUtils.id(key);
        RegistrySupplier<Identifier> supplier = STATS.register(key, () -> Identifier);
        //Stats.CUSTOM.get(Identifier, formatter); // TODO : find a way to make this working on neoforge
        return supplier;
    }

}
