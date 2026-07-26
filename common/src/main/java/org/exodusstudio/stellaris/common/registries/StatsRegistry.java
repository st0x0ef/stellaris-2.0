package org.exodusstudio.stellaris.common.registries;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class StatsRegistry {

    public static final DeferredRegister<Identifier> STATS = DeferredRegister.create(Stellaris.MOD_ID, Registries.CUSTOM_STAT);

    public static RegistrySupplier<Identifier> ROCKET_LAUNCHED = register("rocket_launched");

    public static RegistrySupplier<Identifier> SPACE_TRAVELED = register("space_traveled", (value) -> value + " km");
    public static RegistrySupplier<Identifier> SD_CARD_READ = register("sd_card_read");
    public static RegistrySupplier<Identifier> LANDER_EXPLODED = register("lander_exploded");

    public static RegistrySupplier<Identifier> register(String key) {
        return register(key, StatFormatter.DEFAULT);
    }

    public static RegistrySupplier<Identifier> register(String key, StatFormatter formatter) {
        Identifier identifier = IdentifierUtils.id(key);
        RegistrySupplier<Identifier> supplier = STATS.register(key, () -> identifier);
        LifecycleEvent.SETUP.register(() -> Stats.CUSTOM.get(identifier, formatter));
        return supplier;
    }

}
