package org.exodusstudio.stellaris.common.effects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.utils.MoonLoreUtils;

public class InfectedEffect extends MobEffect {
    public InfectedEffect() {
        super(MobEffectCategory.HARMFUL, 0xAB49DB);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        if (MoonLoreUtils.isImmuneToInfection(entity)) {
            return false;
        }

        entity.hurtServer(level, entity.damageSources().magic(), Stellaris.CONFIG.effectsConfig.infectionDamage + ((float) amplifier / 2));
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int chance = Stellaris.CONFIG.effectsConfig.infectionTickChance;
        return chance > 0 && RandomSource.create().nextInt(chance) == 0;
    }
}
