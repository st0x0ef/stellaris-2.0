package org.exodusstudio.stellaris.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.events.custom.ChunkEvent;
import org.exodusstudio.stellaris.fabric.common.registries.DataAttachmentRegistry;

public final class StellarisFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Stellaris.init();
        DataAttachmentRegistry.register();
        registerEvents();
    }

    public static void registerEvents() {
        ServerChunkEvents.CHUNK_GENERATE.register((level, chunk) -> ChunkEvent.LOAD.invoker().load(chunk, level, true));
        ServerChunkEvents.CHUNK_LOAD.register((level, chunk) ->ChunkEvent.LOAD.invoker().load(chunk, level, false));
    }
}
