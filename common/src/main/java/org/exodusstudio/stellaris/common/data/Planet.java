package org.exodusstudio.stellaris.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record Planet(String translationKey, Identifier dimension, double gravity, boolean hasOxygen, Boolean allowSpaceStation, Optional<Temperature> temperature, Optional<ResourceKey<Level>> parentPlanet, Optional<Identifier> planetBar) {
    public static final Codec<Planet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("translation_key").forGetter(Planet::translationKey),
            Identifier.CODEC.fieldOf("dimension").forGetter(Planet::dimension),
            Codec.DOUBLE.fieldOf("gravity").forGetter(Planet::gravity),
            Codec.BOOL.fieldOf("has_oxygen").forGetter(Planet::hasOxygen),
            Codec.BOOL.optionalFieldOf("allow_space_stations", false).forGetter(Planet::allowSpaceStation),
            Temperature.CODEC.optionalFieldOf("temperature").forGetter(Planet::temperature),
            ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("parent_planet").forGetter(Planet::parentPlanet),
            Identifier.CODEC.optionalFieldOf("planet_bar").forGetter(Planet::planetBar)
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
            ByteBufCodecs.optional(Identifier.STREAM_CODEC), Planet::planetBar,
            Planet::new
    );


    public static final Planet FALLBACK = new Planet(
            "dimension.minecraft.overworld",
            Level.OVERWORLD.identifier(),
            9.81, true, false, Optional.empty(), Optional.empty(), Optional.empty());

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
        MutableComponent component = Component.translatable("command.stellaris.planet_info.header")
                .append("\n").append(Component.translatable("command.stellaris.planet_info.planet", Component.translatable(translationKey)))
                .append("\n").append(Component.translatable("command.stellaris.planet_info.dimension", dimension.toString()))
                .append("\n").append(Component.translatable("command.stellaris.planet_info.gravity", gravity))
                .append("\n").append(Component.translatable("command.stellaris.planet_info.has_oxygen", yesNo(hasOxygen)))
                .append("\n").append(Component.translatable("command.stellaris.planet_info.allow_space_station", yesNo(allowSpaceStation)));

        if (temperature.isPresent()) {
            component.append("\n").append(Component.translatable("command.stellaris.planet_info.day_temperature", temperature.get().dayTimeTemperature()))
                    .append("\n").append(Component.translatable("command.stellaris.planet_info.night_temperature", temperature.get().nightTimeTemperature()));
        }

        return component.append("\n").append(Component.translatable("command.stellaris.planet_info.footer"));
    }

    private static Component yesNo(boolean value) {
        return Component.translatable(value ? "command.stellaris.yes" : "command.stellaris.no");
    }


}
