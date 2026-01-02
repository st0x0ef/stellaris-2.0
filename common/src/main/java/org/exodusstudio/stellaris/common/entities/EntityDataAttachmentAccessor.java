package org.exodusstudio.stellaris.common.entities;

import net.minecraft.resources.Identifier;

public interface EntityDataAttachmentAccessor {

    default boolean hasDataAttachments(Identifier key) {
        return false;
    }

    default <T> T getDataAttachments(Identifier location, Class<T> clazz) {
        return null;
    }

    default <T> void saveDataAttachments(Identifier key, T value) {

    }

}
