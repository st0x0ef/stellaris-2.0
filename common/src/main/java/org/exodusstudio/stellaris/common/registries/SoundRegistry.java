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
    public static final RegistrySupplier<SoundEvent> WELCOME_TO_SPACE = SOUNDS.register("welcome_to_space", () -> SoundEvent.createVariableRangeEvent(IdentifierUtils.id("welcome_to_space")));
    public static final RegistrySupplier<SoundEvent> GEIGER_LOW = SOUNDS.register("geiger_low", () -> SoundEvent.createVariableRangeEvent(IdentifierUtils.id("geiger_low")));
    public static final RegistrySupplier<SoundEvent> GEIGER_HIGH = SOUNDS.register("geiger_high", () -> SoundEvent.createVariableRangeEvent(IdentifierUtils.id("geiger_high")));
    public static final RegistrySupplier<SoundEvent> GEIGER_MEDIUM = SOUNDS.register("geiger_medium", () -> SoundEvent.createVariableRangeEvent(IdentifierUtils.id("geiger_medium")));

}
