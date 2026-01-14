package org.exodusstudio.stellaris.common.modules;

public interface ModuleLike<M extends Module<M>> {
    default M asModule() {
        return (M) this;
    }
}
