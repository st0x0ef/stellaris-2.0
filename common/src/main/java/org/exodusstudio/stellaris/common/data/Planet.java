package org.exodusstudio.stellaris.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record Planet(String translationKey, Identifier dimension, double gravity, boolean hasOxygen, Boolean allowSpaceStation, Optional<Temperature> temperature, Optional<ResourceKey<Level>> parentPlanet) {
    public static final Codec<Planet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("translation_key").forGetter(Planet::translationKey),
            Identifier.CODEC.fieldOf("dimension").forGetter(Planet::dimension),
            Codec.DOUBLE.fieldOf("gravity").forGetter(Planet::gravity),
            Codec.BOOL.fieldOf("has_oxygen").forGetter(Planet::hasOxygen),
            Codec.BOOL.optionalFieldOf("allow_space_stations", false).forGetter(Planet::allowSpaceStation),
            Temperature.CODEC.optionalFieldOf("temperature").forGetter(Planet::temperature),
            ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("parent_planet").forGetter(Planet::parentPlanet)
        ).apply(instance, Planet::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, Planet> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, Planet::translationKey,
            Identifier.STREAM_CODEC, Planet::dimension,
            ByteBufCodecs.DOUBLE, Planet::gravity,
            ByteBufCodecs.BOOL, Planet::hasOxygen,
            ByteBufCodecs.BOOL, Planet::allowSpaceStation,
            ByteBufCodecs.optional(Temperature.STREAM_CODEC), Planet::temperature,
            ByteBufCodecs.optional(ResourceKey.streamCodec(Registries.DIMENSION)), Planet::parentPlanet,
            Planet::new
    );


    public boolean is(ServerLevel level) {
        return is(level.dimension());
    }

    public boolean is(ResourceKey<Level> level) {
        return level.identifier().equals(this.dimension);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Planet other)
            return this.dimension.equals(other.dimension);

        return false;
    }

    @Override
    public int hashCode() {
        return this.dimension.hashCode();
    }

    public Component getDisplayInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("----- Planet Info -----").append("\n");
        sb.append("Planet: ").append(translationKey).append("\n");
        sb.append("Dimension: ").append(dimension.toString()).append("\n");
        sb.append("Gravity: ").append(gravity).append(" m/s²").append("\n");
        sb.append("Has Oxygen: ").append(hasOxygen ? "Yes" : "No").append("\n");
        sb.append("Allow Space Station: ").append(allowSpaceStation ? "Yes" : "No").append("\n");
        if (temperature.isPresent()) {
            sb.append("Day Time Temperature: ").append(temperature.get().dayTimeTemperature()).append(" °C").append("\n");
            sb.append("Night Time Temperature: ").append(temperature.get().nightTimeTemperature()).append(" °C").append("\n");
        }
        sb.append("-----------------------");
        return Component.literal(sb.toString());
    }


}
