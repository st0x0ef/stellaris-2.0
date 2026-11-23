package org.exodusstudio.stellaris.common.travel;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.exodusstudio.stellaris.common.modules.Modules;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.registries.StellarisRegistries;

public record ModuleRequirement(Identifier module) implements PlanetRequirement {
    public static final MapCodec<ModuleRequirement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("module").forGetter(ModuleRequirement::module)
        ).apply(instance, ModuleRequirement::new)
    );

    @Override
    public MapCodec<ModuleRequirement> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Player player, Modules<RocketModule> modules) {
        for (RocketModule installed : modules) {
            if (this.module.equals(StellarisRegistries.ROCKET_MODULES.getId(installed))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Component description() {
        RocketModule rocketModule = StellarisRegistries.ROCKET_MODULES.get(this.module);
        Component name = rocketModule != null ? rocketModule.getDisplayName() : Component.literal(this.module.toString());
        return Component.translatable("message.stellaris.planet_requirement.module", name);
    }
}
