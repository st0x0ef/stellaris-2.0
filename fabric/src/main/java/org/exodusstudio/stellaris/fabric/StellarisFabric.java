package org.exodusstudio.stellaris.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.events.custom.ChunkEvent;
import org.exodusstudio.stellaris.fabric.common.registries.DataAttachmentRegistry;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

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
        onAddReloadListener();
    }

    public static void onAddReloadListener() {
        Stellaris.onAddReloadListenerEvent((id, listener) -> ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return id;
            }

            @Override
            public CompletableFuture<Void> reload(SharedState sharedState, Executor exectutor, PreparationBarrier barrier, Executor applyExectutor) {
                return listener.reload(sharedState, exectutor, barrier, applyExectutor);
            }
        }));
    }
}
