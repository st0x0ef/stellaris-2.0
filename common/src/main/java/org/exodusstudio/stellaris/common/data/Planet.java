package org.exodusstudio.stellaris.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public record Planet(String translationKey, Identifier dimension, double gravity) {
    public static final Codec<Planet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("translation_key").forGetter(Planet::translationKey),
        Identifier.CODEC.fieldOf("dimension").forGetter(Planet::dimension),
        Codec.DOUBLE.fieldOf("gravity").forGetter(Planet::gravity))
            .apply(instance, Planet::new)
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
