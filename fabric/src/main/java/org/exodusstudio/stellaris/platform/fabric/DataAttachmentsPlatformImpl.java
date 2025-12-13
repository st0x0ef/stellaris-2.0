package org.exodusstudio.stellaris.platform.fabric;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.exodusstudio.stellaris.fabric.common.registries.DataAttachmentRegistry;
import org.jetbrains.annotations.Nullable;

public class DataAttachmentsPlatformImpl {

    @Nullable
    public static <T> T getChunkData(ChunkAccess chunk, ResourceLocation location, Class<T> clazz) {
        return chunk.getAttached(getAttachment(location));
    }

    public static <T> void saveChunkData(ChunkAccess chunk, ResourceLocation key, T value) {
        AttachmentType<T> attachmentType = getAttachment(key);
        chunk.setAttached(getAttachment(key), value);
    }

    public static boolean hasChunkData(ChunkAccess chunk, ResourceLocation key) {
        return chunk.hasAttached(getAttachment(key));
    }

    public static <T> AttachmentType<T> getAttachment(ResourceLocation key) {
        return (AttachmentType<T>) DataAttachmentRegistry.ATTACHMENT_TYPES.get(key);
    }

}
