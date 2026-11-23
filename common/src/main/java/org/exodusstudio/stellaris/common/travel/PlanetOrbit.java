package org.exodusstudio.stellaris.common.travel;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record PlanetOrbit(Identifier around, double distance) {
    public static final Codec<PlanetOrbit> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("around").forGetter(PlanetOrbit::around),
            Codec.DOUBLE.fieldOf("distance").forGetter(PlanetOrbit::distance)
        ).apply(instance, PlanetOrbit::new)
    );

    public static final StreamCodec<ByteBuf, PlanetOrbit> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, PlanetOrbit::around,
            ByteBufCodecs.DOUBLE, PlanetOrbit::distance,
            PlanetOrbit::new
    );
}
