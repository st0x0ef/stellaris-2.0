package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.advancements.ParasiteAttachedTrigger;
import org.exodusstudio.stellaris.common.advancements.RocketLaunchedTrigger;

import java.util.function.Supplier;

public class AdvancementTriggerRegistry {

    public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_TYPES =
            DeferredRegister.create(Stellaris.MOD_ID, Registries.TRIGGER_TYPE);

    public static final Supplier<ParasiteAttachedTrigger> PARASITE_ATTACHED =
            TRIGGER_TYPES.register("parasite_attached", ParasiteAttachedTrigger::new);

    public static final Supplier<RocketLaunchedTrigger> ROCKET_LAUNCHED =
            TRIGGER_TYPES.register("rocket_launched", RocketLaunchedTrigger::new);


}
