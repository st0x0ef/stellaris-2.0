package org.exodusstudio.stellaris.platform.fabric;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.Map;

public final class RegistryAliases {

    private static final Map<ResourceKey<? extends Registry<?>>, Map<Identifier, Identifier>> ALIASES = new HashMap<>();

    private RegistryAliases() {}

    public static void add(ResourceKey<? extends Registry<?>> registry, Identifier from, Identifier to) {
        ALIASES.computeIfAbsent(registry, k -> new HashMap<>()).put(from, to);
    }

    public static Identifier resolve(ResourceKey<? extends Registry<?>> registry, Identifier id) {
        Map<Identifier, Identifier> aliases = ALIASES.get(registry);
        if (aliases == null) {
            return id;
        }
        return aliases.getOrDefault(id, id);
    }
}
