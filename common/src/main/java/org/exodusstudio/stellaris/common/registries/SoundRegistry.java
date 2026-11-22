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
    public static final RegistrySupplier<SoundEvent> BEEP_SOUND = SOUNDS.register("beep", () -> SoundEvent.createVariableRangeEvent(IdentifierUtils.id("beep")));
    public static final RegistrySupplier<SoundEvent> SONIC_BOOM_SOUND = SOUNDS.register("sonic_boom", () -> SoundEvent.createVariableRangeEvent(IdentifierUtils.id("sonic_boom")));

    public static final RegistrySupplier<SoundEvent> BRITTLE_ICE_BREAK = registerSound("block.brittle_ice.break");
    public static final RegistrySupplier<SoundEvent> BRITTLE_ICE_STEP = registerSound("block.brittle_ice.step");
    public static final RegistrySupplier<SoundEvent> BRITTLE_ICE_PLACE = registerSound("block.brittle_ice.place");
    public static final RegistrySupplier<SoundEvent> BRITTLE_ICE_HIT = registerSound("block.brittle_ice.hit");
    public static final RegistrySupplier<SoundEvent> BRITTLE_ICE_FALL = registerSound("block.brittle_ice.fall");
    public static final RegistrySupplier<SoundEvent> BRITTLE_ICE_CRACK = registerSound("block.brittle_ice.crack");

    public static final RegistrySupplier<SoundEvent> ICE_CRYSTAL_BREAK = registerSound("block.ice_crystal.break");
    public static final RegistrySupplier<SoundEvent> ICE_CRYSTAL_STEP = registerSound("block.ice_crystal.step");
    public static final RegistrySupplier<SoundEvent> ICE_CRYSTAL_PLACE = registerSound("block.ice_crystal.place");
    public static final RegistrySupplier<SoundEvent> ICE_CRYSTAL_HIT = registerSound("block.ice_crystal.hit");
    public static final RegistrySupplier<SoundEvent> ICE_CRYSTAL_FALL = registerSound("block.ice_crystal.fall");
    public static final RegistrySupplier<SoundEvent> ICE_CRYSTAL_AMBIENT = registerSound("block.ice_crystal.ambient");

    public static final RegistrySupplier<SoundEvent> ICICLE_BREAK = registerSound("block.icicle.break");
    public static final RegistrySupplier<SoundEvent> ICICLE_STEP = registerSound("block.icicle.step");
    public static final RegistrySupplier<SoundEvent> ICICLE_PLACE = registerSound("block.icicle.place");
    public static final RegistrySupplier<SoundEvent> ICICLE_HIT = registerSound("block.icicle.hit");
    public static final RegistrySupplier<SoundEvent> ICICLE_FALL = registerSound("block.icicle.fall");


    public static RegistrySupplier<SoundEvent> registerSound(String key) {
        return SOUNDS.register(key, () -> SoundEvent.createVariableRangeEvent(IdentifierUtils.id(key)));
    }

}
