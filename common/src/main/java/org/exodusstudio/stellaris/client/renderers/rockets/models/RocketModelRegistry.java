package org.exodusstudio.stellaris.client.renderers.rockets.models;

import net.minecraft.client.model.geom.EntityModelSet;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class RocketModelRegistry {

    private static final Map<String, Function<EntityModelSet, ? extends RocketModel>> MODELS = new HashMap<>();

    static {
        register("tiny", TinyRocketModel::new);
        register("small", SmallRocketModel::new);
        register("big", BigRocketModel::new);
    }

    private RocketModelRegistry() {
    }

    public static void register(String id, Function<EntityModelSet, ? extends RocketModel> factory) {
        MODELS.put(id, factory);
    }

    public static @NotNull RocketModel create(String id, EntityModelSet entityModelSet) {
        Function<EntityModelSet, ? extends RocketModel> factory = MODELS.get(id);
        if (factory == null) {
            throw new IllegalStateException("No rocket model registered for id '" + id + "'. Register it on the client before using the module.");
        }

        return factory.apply(entityModelSet);
    }
}

