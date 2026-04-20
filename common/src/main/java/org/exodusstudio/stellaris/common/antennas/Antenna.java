package org.exodusstudio.stellaris.common.antennas;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.exodusstudio.stellaris.common.network.packets.AwardStatPacket;

import java.util.List;
import java.util.UUID;

public record Antenna(
        BlockPos blockPos,
        ResourceKey<Level> dimension,
        String name,
        Boolean isPublic,
        UUID ownerUUID,
        List<UUID> whitelist) {

    public static final Codec<Antenna> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("position").forGetter(Antenna::blockPos),
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(Antenna::dimension),
            Codec.STRING.fieldOf("name").forGetter(Antenna::name),
            Codec.BOOL.fieldOf("public").forGetter(Antenna::isPublic),

            UUIDUtil.CODEC.fieldOf("ownerUUID").forGetter(Antenna::ownerUUID),
            UUIDUtil.CODEC.listOf().fieldOf("whitelist").forGetter(Antenna::whitelist)
    ).apply(instance, Antenna::new));


    public static final StreamCodec<ByteBuf, Antenna> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, Antenna::blockPos,
            ResourceKey.streamCodec(Registries.DIMENSION), Antenna::dimension,
            ByteBufCodecs.STRING_UTF8, Antenna::name,
            ByteBufCodecs.BOOL, Antenna::isPublic,
            UUIDUtil.STREAM_CODEC, Antenna::ownerUUID,
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()), Antenna::whitelist,
            Antenna::new
    );



}
