package org.exodusstudio.stellaris.common.oil;

public interface ChunkOilLevelGetter {

    default int stellaris$getChunkOilLevel() {
        return 0;
    }

    void stellaris$setChunkOilLevel(int level);

    boolean stellaris$hasOilSaved();
}