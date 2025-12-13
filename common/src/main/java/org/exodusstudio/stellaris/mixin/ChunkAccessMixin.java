package org.exodusstudio.stellaris.mixin;

import net.minecraft.world.level.chunk.ChunkAccess;
import org.exodusstudio.stellaris.common.oil.ChunkOilLevelGetter;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;
import org.exodusstudio.stellaris.platform.DataAttachmentsPlatform;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkAccess.class)
public class ChunkAccessMixin implements ChunkOilLevelGetter {


    @Override
    public int stellaris$getChunkOilLevel() {

        ChunkAccess access = (ChunkAccess) (Object) this;

        if (DataAttachmentsPlatform.getChunkData(access, ResourceLocationUtils.id("oil"), Integer.class) != null) {
            return DataAttachmentsPlatform.getChunkData(access, ResourceLocationUtils.id("oil"), Integer.class);
        }

        return -1;
    }

    @Override
    public void stellaris$setChunkOilLevel(int level) {
        ChunkAccess access = (ChunkAccess) (Object) this;

        DataAttachmentsPlatform.saveChunkData(access, ResourceLocationUtils.id("oil"), level);
    }

    @Override
    public boolean stellaris$hasOilSaved() {
        ChunkAccess access = (ChunkAccess) (Object) this;

        return DataAttachmentsPlatform.hasChunkData(access, ResourceLocationUtils.id("oil"));
    }
}
