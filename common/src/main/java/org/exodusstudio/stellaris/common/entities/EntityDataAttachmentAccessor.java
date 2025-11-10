package org.exodusstudio.stellaris.common.entities;

import net.minecraft.resources.ResourceLocation;

public interface EntityDataAttachmentAccessor {

    default boolean hasDataAttachments(ResourceLocation key) {
        return false;
    }

    default <T> T getDataAttachments(ResourceLocation location, Class<T> clazz) {
        return null;
    }

    default <T> void saveDataAttachments(ResourceLocation key, T value) {

    }

}
