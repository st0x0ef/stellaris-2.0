package org.exodusstudio.stellaris.common.effects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;

public class CorrosionEffect extends MobEffect {

    public CorrosionEffect() {
        super(MobEffectCategory.HARMFUL, 0x360101);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        if (entity.is(TagsRegistry.EntityTags.INFECTION_IMMUNE)) {
            return false;
        }

        entity.hurtServer(level, entity.damageSources().magic(), Stellaris.CONFIG.effectsConfig.corrosionDamage + ((float) amplifier / 2));
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int interval = Stellaris.CONFIG.effectsConfig.corrosionTickInterval;
        return interval > 0 && duration % interval == 0;
    }
}
