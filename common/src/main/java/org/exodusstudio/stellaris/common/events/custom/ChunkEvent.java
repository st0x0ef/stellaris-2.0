package org.exodusstudio.stellaris.common.events.custom;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;

public interface ChunkEvent {

    Event<Load> LOAD = EventFactory.createLoop();

    interface Load {
        /**
         * Invoked when a chunk's data is saved, just before the data is written.
         * Add your own data to the {@link CompoundTag} parameter to get your data saved as well.
         * Equivalent to Forge's {@code ChunkDataEvent.Save}.
         *
         * @param chunk The chunk that is saved.
         * @param level The level the chunk is in.
         * @param isNew  If the chunk is new.
         */
        void load(ChunkAccess chunk, LevelAccessor level, boolean isNew);
    }

}
