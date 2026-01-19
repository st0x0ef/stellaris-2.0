package org.exodusstudio.stellaris.platform.neoforge;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.exodusstudio.stellaris.neoforge.common.registries.DataAttachmentRegistry;
import org.jetbrains.annotations.Nullable;

public class DataAttachmentsPlatformImpl {

    @Nullable
    public static <T> T getChunkData(ChunkAccess chunk, Identifier location, Class<T> clazz) {
        return chunk.getData(getAttachment(location));
    }

    public static <T> void saveChunkData(ChunkAccess chunk, Identifier key, T value) {
        AttachmentType<T> attachmentType = getAttachment(key);
        chunk.setData(getAttachment(key), value);
    }

    public static boolean hasChunkData(ChunkAccess chunk, Identifier key) {
        return chunk.hasData(getAttachment(key));
    }

    public static boolean hasEntityData(Entity entity, Identifier key) {
        return entity.hasData(getAttachment(key));
    }
    public static <T> T getEntityData(Entity entity, Identifier location, Class<T> clazz) {
        return entity.getData(getAttachment(location));
    }
    public static <T> void saveEntityData(Entity entity, Identifier key, T value) {
        AttachmentType<T> attachmentType = getAttachment(key);
        entity.setData(getAttachment(key), value);
    }

    public static <T> AttachmentType<T> getAttachment(Identifier key) {
        return (AttachmentType<T>) DataAttachmentRegistry.ATTACHMENTS.get(key).get();
    }

}
