package org.exodusstudio.stellaris.common.modules;

import net.minecraft.world.level.ItemLike;

import java.io.Serializable;
import java.util.Set;

public interface Module<M extends Module<M>> extends Serializable, ItemLike, ModuleLike<M> {
    default Set<? extends Module<?>> requires() {
        return Set.of();
    }

    default Set<? extends Module<?>> incompatible() {
        return Set.of();
    }


}
