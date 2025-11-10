package org.exodusstudio.stellaris.platform.fabric;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricTrackedDataRegistry;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;

public class RegistryPlatformImpl {

    public static <T> void registerEntityDataSerializer(ResourceLocation location, EntityDataSerializer<T> serializer) {
        FabricTrackedDataRegistry.register(location, serializer);
    }



}
