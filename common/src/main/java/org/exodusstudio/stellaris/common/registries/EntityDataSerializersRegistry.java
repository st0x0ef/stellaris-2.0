package org.exodusstudio.stellaris.common.registries;

import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import org.exodusstudio.stellaris.common.rocket.RocketModules;

public class EntityDataSerializersRegistry {

    public static EntityDataSerializer<RocketModules> ROCKET_MODULES = EntityDataSerializer.forValueType(RocketModules.STREAM_CODEC);

    public static void register() {
        EntityDataSerializers.registerSerializer(ROCKET_MODULES);
    }
}
