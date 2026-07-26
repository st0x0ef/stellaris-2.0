package org.exodusstudio.stellaris.common.utils;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.exodusstudio.stellaris.common.assistant.AssistantManager;
import org.exodusstudio.stellaris.common.data.assistant.AssistantTrigger;
import org.exodusstudio.stellaris.common.infection.ParasitePlayerData;
import org.exodusstudio.stellaris.common.registries.EffectsRegistry;

public class InfectionUtils {

    public static void infect(LivingEntity target, int durationTicks) {
        if (MoonLoreUtils.isImmuneToInfection(target)) return;

        Holder<MobEffect> infected = EffectsRegistry.getHolder(EffectsRegistry.INFECTED);
        boolean alreadyInfected = target.hasEffect(infected);

        target.addEffect(new MobEffectInstance(infected, durationTicks, 0));

        if (!alreadyInfected && target instanceof ServerPlayer player) {
            ParasitePlayerData.recordInfection(player, player.level().getGameTime());
            AssistantManager.fire(player, AssistantTrigger.INFECTED);
        }
    }
}
