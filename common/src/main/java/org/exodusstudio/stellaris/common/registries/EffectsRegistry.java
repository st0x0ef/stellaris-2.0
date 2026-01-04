package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.effects.CorrosionEffect;
import org.exodusstudio.stellaris.common.effects.InfectedEffect;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class EffectsRegistry {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Stellaris.MOD_ID, Registries.MOB_EFFECT);

    public static final Identifier CORROSION = IdentifierUtils.id("corrosion");
    public static final Identifier INFECTED = IdentifierUtils.id("infected");

    public static void register() {
        // DO NOT CONVERT THESE TO CONSTANTS AND DO NOT REFERENCE THESE DIRECTLY
        EFFECTS.register(CORROSION, CorrosionEffect::new);
        EFFECTS.register(INFECTED, InfectedEffect::new);

        EFFECTS.register();
    }

    public static Holder<MobEffect> getHolder(Identifier id) {
        Holder<MobEffect> holder = EFFECTS.getRegistrar().getHolder(id);
        if (holder == null) {
            throw new IllegalArgumentException("MobEffect with id " + id + " does not exist");
        }
        return holder;
    }
}