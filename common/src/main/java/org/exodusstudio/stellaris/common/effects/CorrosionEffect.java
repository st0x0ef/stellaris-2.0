package org.exodusstudio.stellaris.common.effects;

import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class CorrosionEffect extends MobEffect {

    public CorrosionEffect() {
        super(MobEffectCategory.HARMFUL, 0x80F2AD);
    }

    @Override
    public void applyInstantenousEffect(ServerLevel level, @Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity, int amplifier, double health) {
        if (entity.getType().is(TagsRegistry.ItemTags.CORROSION_IMMUNE)) {
            return;
        }
        entity.hurt(entity.damageSources().magic(), 1.0F + amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {

        return true;
    }
}