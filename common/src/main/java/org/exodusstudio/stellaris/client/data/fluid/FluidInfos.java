package org.exodusstudio.stellaris.client.data.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record FluidInfos(Identifier spriteTexture, Optional<Identifier>  flowingTexture, Optional<Identifier> overlayTexture, int color) {

    public static Codec<FluidInfos> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("spriteTexture").forGetter(FluidInfos::spriteTexture),
            Identifier.CODEC.optionalFieldOf("flowingTexture").forGetter(FluidInfos::flowingTexture),
            Identifier.CODEC.optionalFieldOf("overlayTexture").forGetter(FluidInfos::overlayTexture),
            Codec.INT.fieldOf("color").forGetter(FluidInfos::color)
    ).apply(instance, FluidInfos::new));

    public static final Codec<Map<Identifier, FluidInfos>> CONTAINER = Codec.unboundedMap(Identifier.CODEC, CODEC);



}
