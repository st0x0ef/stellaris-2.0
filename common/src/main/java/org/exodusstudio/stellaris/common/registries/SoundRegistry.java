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


    public static final RegistrySupplier<SoundEvent> BLENDER_LOOP = SOUNDS.register("blender_loop", () -> SoundEvent.createVariableRangeEvent(IdentifierUtils.id("blender_loop")));
    public static final RegistrySupplier<SoundEvent> BLENDER_START = SOUNDS.register("blender_start", () -> SoundEvent.createVariableRangeEvent(IdentifierUtils.id("blender_start")));
    public static final RegistrySupplier<SoundEvent> BLENDER_END = SOUNDS.register("blender_end", () -> SoundEvent.createVariableRangeEvent(IdentifierUtils.id("blender_end")));

}
