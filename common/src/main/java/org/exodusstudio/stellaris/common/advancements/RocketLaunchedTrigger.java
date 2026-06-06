package org.exodusstudio.stellaris.common.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.OptionalInt;

public class RocketLaunchedTrigger extends SimpleCriterionTrigger<RocketLaunchedTrigger.@NotNull ExampleTriggerInstance> {
    // This method is unique for each trigger and is as such not a method to override
    public void trigger(ServerPlayer player, int rocketLaunched) {
        this.trigger(player,
                // The condition checker method within the SimpleCriterionTrigger.SimpleInstance subclass
                triggerInstance -> triggerInstance.matches(rocketLaunched)
        );
    }

    @Override
    public Codec<ExampleTriggerInstance> codec() {
        return ExampleTriggerInstance.CODEC;
    }


    public record ExampleTriggerInstance(Optional<ContextAwarePredicate> player, Optional<Integer> rocketLaunched)
            implements SimpleInstance {


        public boolean matches(int rocketLaunched) {
            return this.rocketLaunched().map(i -> i == rocketLaunched).orElse(true);
        }

        public static final Codec<ExampleTriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(ExampleTriggerInstance::player),
                Codec.INT.optionalFieldOf("rocket_launched").forGetter(ExampleTriggerInstance::rocketLaunched)
        ).apply(instance, ExampleTriggerInstance::new));



    }



}




