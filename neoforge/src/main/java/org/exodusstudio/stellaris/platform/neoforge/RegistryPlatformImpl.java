package org.exodusstudio.stellaris.platform.neoforge;

import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.exodusstudio.stellaris.Stellaris;

import java.util.function.Supplier;

public class RegistryPlatformImpl {

    public static DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, Stellaris.MOD_ID);

    public static <T> void registerEntityDataSerializer(ResourceLocation location, EntityDataSerializer<T> serializer) {
        ENTITY_DATA_SERIALIZERS.register(location.getPath(), () -> serializer);
    }

}
