package org.exodusstudio.stellaris.common.sound;

import net.minecraft.world.level.block.SoundType;
import org.exodusstudio.stellaris.common.registries.SoundRegistry;

public class StellarisSoundType {
    public static final SoundType BRITTLE_ICE = new SoundType(
            1, 1,
            SoundRegistry.BRITTLE_ICE_BREAK.get(),
            SoundRegistry.BRITTLE_ICE_STEP.get(),
            SoundRegistry.BRITTLE_ICE_PLACE.get(),
            SoundRegistry.BRITTLE_ICE_HIT.get(),
            SoundRegistry.BRITTLE_ICE_FALL.get()
    );
    public static final SoundType ICICLE = new SoundType(
            1, 1,
            SoundRegistry.ICICLE_BREAK.get(),
            SoundRegistry.ICICLE_STEP.get(),
            SoundRegistry.ICICLE_PLACE.get(),
            SoundRegistry.ICICLE_HIT.get(),
            SoundRegistry.ICICLE_FALL.get()
    );
    public static final SoundType ICE_CRYSTAL = new SoundType(
            1, 1,
            SoundRegistry.ICE_CRYSTAL_BREAK.get(),
            SoundRegistry.ICE_CRYSTAL_STEP.get(),
            SoundRegistry.ICE_CRYSTAL_PLACE.get(),
            SoundRegistry.ICE_CRYSTAL_HIT.get(),
            SoundRegistry.ICE_CRYSTAL_FALL.get()
    );
}
