package org.exodusstudio.stellaris.common.utils;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.platform.DataAttachmentsPlatform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RadiationUtils {

    public static Identifier RADIATION_BLOCKS_MAP_ID = IdentifierUtils.id("radioactive_blocks_map");

    public static Codec<List<Pair<BlockPos, Float>>> RADIATION_BLOCK_MAP_CODEC = Codec.pair(BlockPos.CODEC.fieldOf("blockPos").codec(), Codec.FLOAT.fieldOf("radiationLevel").codec()).listOf().fieldOf("radiationBlock").codec();

    public static final StreamCodec<ByteBuf, List<Pair<BlockPos, Float>>> RADIATION_BLOCK_MAP_STREAMCODEC = Utils.pairStreamCodec(BlockPos.STREAM_CODEC, ByteBufCodecs.FLOAT).apply(ByteBufCodecs.list());


    @SuppressWarnings("unchecked")
    public static void modifyBlockMap(ChunkAccess chunk, BlockPos pos, float radiationLevel, boolean remove) {

        List<Pair<BlockPos, Float>> blockList = DataAttachmentsPlatform.getChunkData(chunk, RADIATION_BLOCKS_MAP_ID, List.class);

        if (blockList == null) blockList = new ArrayList<>();

        HashMap<BlockPos, Float> blockMap = pairListToMap(blockList);


        if (remove) {
            blockMap.remove(pos);
        } else {
            if (blockMap.containsKey(pos)) {
                Stellaris.LOG.error("Saving an already existing radioactive block at {}. Overriding it", pos);
            }
            blockMap.put(pos, radiationLevel);
        }


        DataAttachmentsPlatform.saveChunkData(chunk, RADIATION_BLOCKS_MAP_ID, mapToPairList(blockMap));
    }

    public static List<Pair<BlockPos, Float>> getRadioactiveBlocksInRadius(ChunkAccess chunk, BlockPos pos, int radius) {
        List<Pair<BlockPos, Float>> blockList = DataAttachmentsPlatform.getChunkData(chunk, RADIATION_BLOCKS_MAP_ID, List.class);

        if (blockList == null) blockList = new ArrayList<>();

        List<Pair<BlockPos, Float>> blocksInRadius = new ArrayList<>();

        for (Pair<BlockPos, Float> pair : blockList) {
            if (pair.getFirst().closerThan(pos, radius)) {
                blocksInRadius.add(pair);
            }
        }

        return blocksInRadius;
    }


    public static List<Pair<BlockPos, Float>> getRadioactiveBlocks(ChunkAccess chunk) {
        List<Pair<BlockPos, Float>> blockList = DataAttachmentsPlatform.getChunkData(chunk, RADIATION_BLOCKS_MAP_ID, List.class);

        if (blockList == null) blockList = new ArrayList<>();

        return blockList;
    }

    public static HashMap<BlockPos, Float> pairListToMap(List<Pair<BlockPos, Float>> blockList) {
        return blockList.stream().collect(HashMap::new, (m, p) -> m.put(p.getFirst(), p.getSecond()), HashMap::putAll);
    }

    public static List<Pair<BlockPos, Float>> mapToPairList(Map<BlockPos, Float> map) {

        return map.entrySet().stream().map((e) -> Pair.of(e.getKey(), e.getValue())).toList();
    }


}
