package org.exodusstudio.stellaris.platform.fabric;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityDataRegistry;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;

public class RegistryPlatformImpl {

    public static <T> void registerEntityDataSerializer(Identifier location, EntityDataSerializer<T> serializer) {
        FabricEntityDataRegistry.register(location, serializer);
    }
}
