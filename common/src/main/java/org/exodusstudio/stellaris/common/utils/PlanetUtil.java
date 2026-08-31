package org.exodusstudio.stellaris.common.utils;

import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;

public class PlanetUtil {

    public static void teleportToPlanet(Entity entity, Planet planet, int fallbackY) {
        ServerLevel level = PlanetsData.getPlanetLevel(entity.level().getServer(), planet);
        if (level == null) {
            return;
        }

        int x = Mth.floor(entity.getX());
        int z = Mth.floor(entity.getZ());
        int y = getSurfaceY(level, x, z, fallbackY);

        entity.teleport(new TeleportTransition(level, new Vec3(x + 0.5, y, z + 0.5), Vec3.ZERO, entity.getYRot(), entity.getXRot(), e -> e.fallDistance = 0.0F));
    }

    public static int getSurfaceY(ServerLevel level, int x, int z, int fallbackY) {
        level.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));

        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        return y > level.getMinY() ? y : fallbackY;
    }
}
