package org.exodusstudio.stellaris.common.module;

public interface ModuleLike<M extends Module<M>> {
    default M asModule() {
        return (M) this;
    }
}
