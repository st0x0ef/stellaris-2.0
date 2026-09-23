package org.exodusstudio.stellaris.common.modules.rover;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.modules.Modules;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModules;
import org.exodusstudio.stellaris.common.registries.StellarisRegistries;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record RoverUpgrades(Modules<RocketModule> rocketModules, Modules<RoverModule> roverModules) {
    private static final Map<Identifier, Identifier> MERGED_INTO_ROCKET_MODULES = Map.of(
            IdentifierUtils.id("rover_cargo_module"), IdentifierUtils.id("cargo"),
            IdentifierUtils.id("rover_hydrogen_motor"), IdentifierUtils.id("hydrogen_motor")
    );

    private static final Codec<RoverUpgrades> CURRENT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RocketModules.CODEC.optionalFieldOf("rocket_modules", RocketModules.empty()).forGetter(RoverUpgrades::rocketModules),
            RoverModules.CODEC.optionalFieldOf("rover_modules", RoverModules.empty()).forGetter(RoverUpgrades::roverModules)
    ).apply(instance, RoverUpgrades::new));

    private static final Codec<RoverUpgrades> LEGACY_CODEC = Identifier.CODEC.listOf().xmap(RoverUpgrades::fromLegacyIds, upgrades -> {
        throw new UnsupportedOperationException("Rover upgrades are only written in the current format");
    });

    public static final Codec<RoverUpgrades> CODEC = Codec.withAlternative(CURRENT_CODEC, LEGACY_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, RoverUpgrades> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CURRENT_CODEC);

    public static RoverUpgrades empty() {
        return new RoverUpgrades(RocketModules.empty(), RoverModules.empty());
    }

    public RoverUpgrades withRocketModules(Modules<RocketModule> rocketModules) {
        return new RoverUpgrades(rocketModules, this.roverModules);
    }

    public RoverUpgrades withRoverModules(Modules<RoverModule> roverModules) {
        return new RoverUpgrades(this.rocketModules, roverModules);
    }

    private static RoverUpgrades fromLegacyIds(List<Identifier> ids) {
        List<RocketModule> rocketModules = new ArrayList<>();
        List<RoverModule> roverModules = new ArrayList<>();

        for (Identifier id : ids) {
            Identifier mergedInto = MERGED_INTO_ROCKET_MODULES.get(id);
            if (mergedInto != null) {
                rocketModules.add(StellarisRegistries.ROCKET_MODULES.get(mergedInto));
                continue;
            }

            RoverModule roverModule = StellarisRegistries.ROVER_MODULES.get(id);
            if (roverModule != null) {
                roverModules.add(roverModule);
            } else {
                Stellaris.LOG.warn("Dropping unknown rover module {}", id);
            }
        }

        return new RoverUpgrades(new RocketModules(rocketModules), new RoverModules(roverModules));
    }
}
