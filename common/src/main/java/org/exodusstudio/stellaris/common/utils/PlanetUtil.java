package org.exodusstudio.stellaris.common.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;

public class PlanetUtil {

    public static void teleportToPlanet(Entity entity, Planet planet, int yPos) {
        ServerLevel level = PlanetsData.getPlanetLevel(entity.level().getServer(), planet);
        if (level == null) {
            return;
        }

        entity.teleport(new TeleportTransition(level, new Vec3(entity.getX(), yPos, entity.getZ()), Vec3.ZERO, entity.getYRot(), entity.getXRot(), e -> e.fallDistance = 0.0F));
    }
}
