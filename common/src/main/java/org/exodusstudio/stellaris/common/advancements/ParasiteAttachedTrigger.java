package org.exodusstudio.stellaris.common.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ParasiteAttachedTrigger extends SimpleCriterionTrigger<ParasiteAttachedTrigger.@NotNull ExampleTriggerInstance> {
    // This method is unique for each trigger and is as such not a method to override
    public void trigger(ServerPlayer player) {
        this.trigger(player,
                // The condition checker method within the SimpleCriterionTrigger.SimpleInstance subclass
                triggerInstance -> true
        );
    }

    @Override
    public Codec<ExampleTriggerInstance> codec() {
        return ExampleTriggerInstance.CODEC;
    }


    public record ExampleTriggerInstance(Optional<ContextAwarePredicate> player)
            implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<ExampleTriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(ExampleTriggerInstance::player)
        ).apply(instance, ExampleTriggerInstance::new));



    }



}




