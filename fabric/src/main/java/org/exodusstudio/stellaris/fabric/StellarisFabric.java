package org.exodusstudio.stellaris.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
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
        StellarisFabricEvents.entityLoadEvent();
    }

    public static void onAddReloadListener() {
        Stellaris.onAddReloadListenerEvent((id, listener) -> ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return id;
            }

            @Override
            public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier preparationBarrier, ResourceManager resourceManager, Executor prepareExecutor, Executor applyExecutor) {
                return listener.reload(preparationBarrier, resourceManager, prepareExecutor, applyExecutor);
            }
        }));
    }
}
