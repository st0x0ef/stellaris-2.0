package org.exodusstudio.stellaris.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.NotImplementedException;

public class RegistryPlatform {

    @ExpectPlatform
    public static <T> void registerEntityDataSerializer(ResourceLocation location,  EntityDataSerializer<T> serializer) {
        throw new NotImplementedException();
    }

}
