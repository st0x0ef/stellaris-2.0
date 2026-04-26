package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;


public class SoundRegistry {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Stellaris.MOD_ID, Registries.SOUND_EVENT);

    /** SOUNDS */
    public static final RegistrySupplier<SoundEvent> ROCKET_SOUND = SOUNDS.register("rocket_fly", () -> SoundEvent.createVariableRangeEvent(IdentifierUtils.id("rocket_fly")));
    public static final RegistrySupplier<SoundEvent> BOOST_SOUND = SOUNDS.register("boost", () -> SoundEvent.createVariableRangeEvent(IdentifierUtils.id("boost")));
}
