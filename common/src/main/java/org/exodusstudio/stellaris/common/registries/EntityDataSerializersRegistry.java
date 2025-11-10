package org.exodusstudio.stellaris.common.registries;

import net.minecraft.network.syncher.EntityDataSerializer;
import org.exodusstudio.stellaris.common.rocket.RocketModules;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;
import org.exodusstudio.stellaris.platform.RegistryPlatform;

public class EntityDataSerializersRegistry {

    public static EntityDataSerializer<RocketModules> ROCKET_MODULES = EntityDataSerializer.forValueType(RocketModules.STREAM_CODEC);

    public static void register() {
        RegistryPlatform.registerEntityDataSerializer(ResourceLocationUtils.id("rocket_modules"), ROCKET_MODULES);
    }
}
