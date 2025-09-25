package org.exodusstudio.stellaris.common.events;

import org.exodusstudio.stellaris.common.events.custom.ChunkEvent;
import org.exodusstudio.stellaris.common.oil.OilUtils;

public class Events {

    public static void register() {
        ChunkEvent.LOAD.register(((chunk, level, isNew) -> {
            if (!chunk.stellaris$hasOilSaved()) {
                chunk.stellaris$setChunkOilLevel(OilUtils.getRandomOilLevel());
            }
        }));
    }

}
