package org.exodusstudio.stellaris.common.antennas;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public class Antenna {


    public BlockPos blockPos;
    public ResourceKey<Level> dimension;
    public String name;
    public Boolean isPublic;
    public UUID ownerUUID;
    public List<UUID> whitelist;

    public Antenna(BlockPos blockPos,
                   ResourceKey<Level> dimension,
                   String name,
                   Boolean isPublic,
                   UUID ownerUUID,
                   List<UUID> whitelist) {
        this.blockPos = blockPos;
        this.dimension = dimension;
        this.name = name;
        this.isPublic = isPublic;
        this.ownerUUID = ownerUUID;
        this.whitelist = whitelist;
    }

    public static final Codec<Antenna> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("position").forGetter((a) -> a.blockPos),
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter((a) -> a.dimension),
            Codec.STRING.fieldOf("name").forGetter((a) -> a.name),
            Codec.BOOL.fieldOf("public").forGetter((a) -> a.isPublic),

            UUIDUtil.CODEC.fieldOf("ownerUUID").forGetter((a) -> a.ownerUUID),
            UUIDUtil.CODEC.listOf().fieldOf("whitelist").forGetter((a) -> a.whitelist)
    ).apply(instance, Antenna::new));


    public static final StreamCodec<ByteBuf, Antenna> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, (a) -> a.blockPos,
            ResourceKey.streamCodec(Registries.DIMENSION), (a) -> a.dimension,
            ByteBufCodecs.STRING_UTF8, (a) -> a.name,
            ByteBufCodecs.BOOL, (a) -> a.isPublic,
            UUIDUtil.STREAM_CODEC, (a) -> a.ownerUUID,
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()), (a) -> a.whitelist,
            Antenna::new
    );





}
