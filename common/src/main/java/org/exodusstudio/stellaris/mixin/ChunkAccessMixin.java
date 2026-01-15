package org.exodusstudio.stellaris.mixin;

import net.minecraft.world.level.chunk.ChunkAccess;
import org.exodusstudio.stellaris.common.oil.ChunkOilLevelGetter;
import org.exodusstudio.stellaris.common.oil.OilUtils;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.platform.DataAttachmentsPlatform;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkAccess.class)
public class ChunkAccessMixin implements ChunkOilLevelGetter {


    @Override
    public int stellaris$getChunkOilLevel() {

        ChunkAccess access = (ChunkAccess) (Object) this;
        Integer oil = DataAttachmentsPlatform.getChunkData(access, IdentifierUtils.id("oil"), Integer.class);

        if (oil != null) {
            if (oil == -1) stellaris$setChunkOilLevel(OilUtils.getRandomOilLevel());
            return DataAttachmentsPlatform.getChunkData(access, IdentifierUtils.id("oil"), Integer.class);
        }

        return -1;
    }

    @Override
    public void stellaris$setChunkOilLevel(int level) {
        ChunkAccess access = (ChunkAccess) (Object) this;

        DataAttachmentsPlatform.saveChunkData(access, IdentifierUtils.id("oil"), level);
    }

    @Override
    public boolean stellaris$hasOilSaved() {
        ChunkAccess access = (ChunkAccess) (Object) this;

        return DataAttachmentsPlatform.hasChunkData(access, IdentifierUtils.id("oil"));
    }
}
