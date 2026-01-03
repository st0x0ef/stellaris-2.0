package org.exodusstudio.stellaris.platform.neoforge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.exodusstudio.stellaris.neoforge.common.registries.DataAttachmentRegistry;
import org.jetbrains.annotations.Nullable;

public class DataAttachmentsPlatformImpl {

    @Nullable
    public static <T> T getChunkData(ChunkAccess chunk, ResourceLocation location, Class<T> clazz) {
        return chunk.getData(getAttachment(location));
    }

    public static <T> void saveChunkData(ChunkAccess chunk, ResourceLocation key, T value) {
        AttachmentType<T> attachmentType = getAttachment(key);
        chunk.setData(getAttachment(key), value);
    }

    public static boolean hasChunkData(ChunkAccess chunk, ResourceLocation key) {
        return chunk.hasData(getAttachment(key));
    }

    public static boolean hasEntityData(Entity entity, ResourceLocation key) {
        return entity.hasData(getAttachment(key));
    }
    public static <T> T getEntityData(Entity entity, ResourceLocation location, Class<T> clazz) {
        return entity.getData(getAttachment(location));
    }
    public static <T> void saveEntityData(Entity entity, ResourceLocation key, T value) {
        AttachmentType<T> attachmentType = getAttachment(key);
        entity.setData(getAttachment(key), value);
    }

    public static <T> AttachmentType<T> getAttachment(ResourceLocation key) {
        return (AttachmentType<T>) DataAttachmentRegistry.ATTACHMENTS.get(key);
    }

}
