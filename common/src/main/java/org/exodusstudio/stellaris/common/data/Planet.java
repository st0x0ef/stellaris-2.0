package org.exodusstudio.stellaris.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public record Planet(String translationKey, ResourceLocation dimension) {
    public static final Codec<Planet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("translation_key").forGetter(Planet::translationKey),
        ResourceLocation.CODEC.fieldOf("dimension").forGetter(Planet::dimension)).apply(instance, Planet::new)
    );

    public boolean is(ServerLevel level) {
        return level.dimension().location().equals(this.dimension);
    }
}
