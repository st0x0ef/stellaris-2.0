package org.exodusstudio.stellaris.common.entities;

import net.minecraft.resources.Identifier;

public interface EntityDataAttachmentAccessor {

    default boolean stellaris$hasDataAttachments(Identifier key) {
        return false;
    }

    default <T> T stellaris$getDataAttachments(Identifier location, Class<T> clazz) {
        return null;
    }

    default <T> void stellaris$saveDataAttachments(Identifier key, T value) {

    }

}
