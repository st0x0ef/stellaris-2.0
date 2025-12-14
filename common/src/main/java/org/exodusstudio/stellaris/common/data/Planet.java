package org.exodusstudio.stellaris.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public record Planet(String translationKey, ResourceLocation dimension, String gravity) {
    public static final Codec<Planet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("translation_key").forGetter(Planet::translationKey),
        ResourceLocation.CODEC.fieldOf("dimension").forGetter(Planet::dimension),
        Codec.STRING.fieldOf("gravity").forGetter(Planet::gravity))
            .apply(instance, Planet::new)
    );

    public boolean is(ServerLevel level) {
        return is(level.dimension());
    }

    public boolean is(ResourceKey<Level> level) {
        return level.location().equals(this.dimension);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Planet other)
            return this.dimension.equals(other.dimension);

        return false;
    }

    public Component getDisplayInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("----- Planet Info -----").append("\n");
        sb.append("Planet: ").append(translationKey).append("\n");
        sb.append("Dimension: ").append(dimension.toString()).append("\n");
        sb.append("Gravity: ").append(gravity).append(" m/s²").append("\n");
        sb.append("-----------------------");
        return Component.literal(sb.toString());
    }
}
