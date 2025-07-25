package org.exodusstudio.stellaris.registries;

import dev.architectury.registry.registries.Registrar;

import java.util.HashSet;
import java.util.Set;

/// This is to make sure all the registrars are present on neoforge
public abstract class ModRegistries {

    protected static Set<ModRegistries> REGISTRIES = new HashSet<>();

    public static void registerAll() {
        REGISTRIES.forEach(modRegistries -> modRegistries.getRegistrar().key());
    }

    public abstract Registrar<?> getRegistrar();
    /// To help developers remember to create a static instance
    public abstract ModRegistries getStaticInstance();

    protected void registerSelf() {
        REGISTRIES.add(this);
    }

    protected ModRegistries() {
        registerSelf();
    }
}
