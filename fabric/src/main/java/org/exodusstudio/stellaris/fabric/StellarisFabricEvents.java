package org.exodusstudio.stellaris.fabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.world.entity.LivingEntity;
import org.exodusstudio.stellaris.common.utils.GravityUtils;

public class StellarisFabricEvents {
    public static void entityLoadEvent() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, server) -> {
            if (entity instanceof LivingEntity living) {
                GravityUtils.setGravity(living);
            }
        });
    }
}
