package org.exodusstudio.stellaris.common.entities;

import net.minecraft.resources.ResourceLocation;

public interface EntityDataAttachmentAccessor {

    default boolean hasEntityData(ResourceLocation key) {
        return false;
    }

    default <T> T getEntityData(ResourceLocation location, Class<T> clazz) {
        return null;
    }

    default <T> void saveEntityData(ResourceLocation key, T value) {

    }

}
