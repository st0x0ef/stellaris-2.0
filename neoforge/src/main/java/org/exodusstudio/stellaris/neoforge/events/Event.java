package org.exodusstudio.stellaris.neoforge.events;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;

@EventBusSubscriber(modid = "stellaris")
public class Event {

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        org.exodusstudio.stellaris.common.events.custom.ChunkEvent.LOAD.invoker().load(event.getChunk(), event.getLevel(), event.isNewChunk());
    }
}
