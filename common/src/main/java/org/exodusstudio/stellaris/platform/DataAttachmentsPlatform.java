package org.exodusstudio.stellaris.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.ChunkAccess;

public class DataAttachmentsPlatform {


    @ExpectPlatform
    public static <T> T getChunkData(ChunkAccess chunk, Identifier location, Class<T> clazz) {
        throw new AssertionError();
    }
    @ExpectPlatform
    public static <T> void saveChunkData(ChunkAccess chunk, Identifier key, T value) {
        throw new AssertionError();
    }
    @ExpectPlatform
    public static boolean hasChunkData(ChunkAccess chunk, Identifier key) {
        throw new AssertionError();
    }


    @ExpectPlatform
    public static boolean hasEntityData(Entity entity, Identifier key) {
        throw new AssertionError();
    }
    @ExpectPlatform
    public static <T> T getEntityData(Entity entity, Identifier location, Class<T> clazz) {
        throw new AssertionError();
    }
    @ExpectPlatform
    public static <T> void saveEntityData(Entity entity, Identifier key, T value) {
        throw new AssertionError();
    }
}
