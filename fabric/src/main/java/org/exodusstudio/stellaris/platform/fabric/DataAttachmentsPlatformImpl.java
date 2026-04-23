package org.exodusstudio.stellaris.platform.fabric;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.exodusstudio.stellaris.fabric.common.registries.DataAttachmentRegistry;
import org.jetbrains.annotations.Nullable;

public class DataAttachmentsPlatformImpl {

    @Nullable
    public static <T> T getChunkData(ChunkAccess chunk, Identifier location, Class<T> clazz) {
        return chunk.getAttached(getAttachment(location));
    }

    public static <T> void saveChunkData(ChunkAccess chunk, Identifier key, T value) {
        AttachmentType<T> attachmentType = getAttachment(key);
        chunk.setAttached(getAttachment(key), value);
    }

    public static boolean hasChunkData(ChunkAccess chunk, Identifier key) {
        return chunk.hasAttached(getAttachment(key));
    }

    public static boolean hasEntityData(Entity entity, Identifier key) {
        return entity.hasAttached(getAttachment(key));
    }
    public static <T> T getEntityData(Entity entity, Identifier location, Class<T> clazz) {
        return entity.getAttached(getAttachment(location));
    }
    public static <T> void saveEntityData(Entity entity, Identifier key, T value) {
        AttachmentType<T> attachmentType = getAttachment(key);
        entity.setAttached(getAttachment(key), value);
    }

    public static <T> AttachmentType<T> getAttachment(Identifier key) {
        return (AttachmentType<T>) DataAttachmentRegistry.ATTACHMENT_TYPES.get(key);
    }

}
