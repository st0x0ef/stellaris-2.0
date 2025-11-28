package org.exodusstudio.stellaris.common.module;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.ItemLike;

import java.io.Serializable;
import java.util.Set;

public interface Module<M extends Module<M>> extends Serializable, ItemLike, ModuleLike<M> {

    MutableComponent displayName(); //TODO get name from translation key with registry

    default Set<? extends Module<?>> requires() {
        return Set.of();
    }

    default Set<? extends Module<?>> incompatible() {
        return Set.of();
    }


}
