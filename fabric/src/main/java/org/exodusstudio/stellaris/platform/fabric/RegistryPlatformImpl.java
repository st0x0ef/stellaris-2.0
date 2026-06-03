package org.exodusstudio.stellaris.platform.fabric;

import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;

public class RegistryPlatformImpl {

    public static <T> void registerEntityDataSerializer(Identifier location, EntityDataSerializer<T> serializer) {
        //FabricTrackedDataRegistry.register(location, serializer);
    }
}
