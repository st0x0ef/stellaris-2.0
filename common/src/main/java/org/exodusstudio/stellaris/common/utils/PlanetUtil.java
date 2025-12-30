package org.exodusstudio.stellaris.common.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.TeleportTransition;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;

public class PlanetUtil {

    public static void teleportToPlanet(Entity entity, Planet planet, int yPos) {
        ServerLevel level = PlanetsData.getPlanetLevel(entity.getServer(), planet);
        if (level == null) {
            return;
        }

        entity.teleport(new TeleportTransition(level, entity, e -> e.setPos(entity.getX(), yPos, entity.getZ())));
    }
}
