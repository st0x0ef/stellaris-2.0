package org.exodusstudio.stellaris.common.effects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;

public class InfectedEffect extends MobEffect {
    public InfectedEffect() {
        super(MobEffectCategory.HARMFUL, 0xAB49DB);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        if (entity.is(TagsRegistry.EntityTags.INFECTION_IMMUNE)) {
            return false;
        }

        entity.hurtServer(level, entity.damageSources().magic(), 1.0F + ((float) amplifier / 2));
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return RandomSource.create().nextInt(100) == 0; // TODO : make this value configurable
    }
}
