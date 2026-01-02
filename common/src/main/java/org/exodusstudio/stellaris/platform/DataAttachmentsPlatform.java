package org.exodusstudio.stellaris.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.ChunkAccess;

public class DataAttachmentsPlatform {


    @ExpectPlatform
    public static <T> T getChunkData(ChunkAccess chunk, ResourceLocation location, Class<T> clazz) {
        throw new AssertionError();
    }
    @ExpectPlatform
    public static <T> void saveChunkData(ChunkAccess chunk, ResourceLocation key, T value) {
        throw new AssertionError();
    }
    @ExpectPlatform
    public static boolean hasChunkData(ChunkAccess chunk, ResourceLocation key) {
        throw new AssertionError();
    }


    @ExpectPlatform
    public static boolean hasEntityData(Entity entity, ResourceLocation key) {
        throw new AssertionError();
    }
    @ExpectPlatform
    public static <T> T getEntityData(Entity entity, ResourceLocation location, Class<T> clazz) {
        throw new AssertionError();
    }
    @ExpectPlatform
    public static <T> void saveEntityData(Entity entity, ResourceLocation key, T value) {
        throw new AssertionError();
    }
}
